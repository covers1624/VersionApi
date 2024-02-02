package net.covers1624.versionapi.controller;

import com.google.gson.Gson;
import net.covers1624.quack.collection.FastStream;
import net.covers1624.quack.maven.MavenNotation;
import net.covers1624.versionapi.entity.ApiKey;
import net.covers1624.versionapi.entity.JsonCache;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.json.MarkJson;
import net.covers1624.versionapi.repo.ApiKeyRepository;
import net.covers1624.versionapi.repo.JsonCacheRepo;
import net.covers1624.versionapi.repo.ModVersionRepository;
import net.covers1624.versionapi.security.ApiAuth;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/api/v1/")
public class ApiV1Controller {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    private final ApiKeyRepository apiKeyRepo;
    private final ModVersionRepository modVersionRepo;
    private final JsonCacheRepo cacheRepo;

    public ApiV1Controller(ApiKeyRepository apiKeyRepo, ModVersionRepository modVersionRepo, JsonCacheRepo cacheRepo) {
        this.apiKeyRepo = apiKeyRepo;
        this.modVersionRepo = modVersionRepo;
        this.cacheRepo = cacheRepo;
    }

    @PutMapping ("admin/add_key")
    public ResponseEntity<String> addApiKey(ApiAuth auth) {
        auth.requireAdmin("Only admins can add api keys.");

        ApiKey newKey = new ApiKey(UUID.randomUUID().toString());
        apiKeyRepo.save(newKey);
        LOGGER.info("Added new API Key: " + newKey.getSecret());
        return ResponseEntity.ok(newKey.getSecret());
    }

    @ResponseBody
    @PostMapping (
            value = "mark_latest",
            consumes = "application/json"
    )
    public ResponseEntity<String> markLatest(ApiAuth auth, @RequestBody MarkJson json) throws IOException {
        MavenNotation notation = computeVersion(json);
        String[] segs = notation.version.split("-");
        if (segs.length != 2) throw new RuntimeException("Invalid detected version. Expected 2 splits. " + notation.version);

        String mcVersion = segs[0];
        String modVersion = segs[1];

        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(notation.module, mcVersion);
        if (version == null) {
            version = new ModVersion(notation.module, mcVersion, json.homepage());
            version.setLatest(modVersion);
            modVersionRepo.save(version);
        }
        rebuildCache(version);

        return ResponseEntity.ok(version.getLatest());
    }

    @ResponseBody
    @PostMapping (
            value = "mark_recommended",
            consumes = "application/json"
    )
    public ResponseEntity<String> markRecommended(ApiAuth auth, @RequestBody MarkJson json) throws IOException {
        MavenNotation notation = computeVersion(json);
        String[] segs = notation.version.split("-");
        if (segs.length != 2) throw new RuntimeException("Invalid detected version. Expected 2 splits. " + notation.version);

        String mcVersion = segs[0];
        String modVersion = segs[1];

        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(notation.module, mcVersion);
        if (version == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Version does not exist. Mark as Latest first. :" + json.suffix());

        version.setRecommended(modVersion);
        modVersionRepo.save(version);
        rebuildCache(version);

        return ResponseEntity.ok(version.getRecommended());
    }

    private void rebuildCache(ModVersion version) {
        JsonCache cache = cacheRepo.findByModIdAndMcVersion(version.getModId(), version.getMcVersion());
        if (cache == null) {
            cache = new JsonCache(version.getModId(), version.getMcVersion());
        }
        cache.setValue(GSON.toJson(ForgeVersionJson.create(version)));
        cacheRepo.save(cache);
    }

    private static MavenNotation computeVersion(MarkJson json) throws IOException {
        MavenNotation notation = MavenNotation.parse(json.coordinates());
        URL url = new URL(StringUtils.appendIfMissing(json.mavenRepo(), "/") + notation.toModulePath() + "maven-metadata.xml");
        try (InputStream is = openUrlStream(url)) {
            Metadata metadata = new MetadataXpp3Reader().read(is);
            return notation.withVersion(FastStream.of(metadata.getVersioning().getVersions())
                    .filter(e -> e.endsWith(json.suffix()))
                    .first());
        } catch (XmlPullParserException e) {
            throw new IOException("Failed to parse MavenMetadata.", e);
        }
    }

    private static InputStream openUrlStream(URL url) throws IOException {
        URL currentUrl = url;
        for (int redirects = 0; redirects < 20; redirects++) {
            URLConnection c = currentUrl.openConnection();
            if (c instanceof HttpURLConnection huc) {
                huc.setInstanceFollowRedirects(false);
                int responseCode = huc.getResponseCode();
                if (responseCode >= 300 && responseCode <= 399) {
                    try {
                        String loc = huc.getHeaderField("Location");
                        currentUrl = new URL(currentUrl, loc);
                        continue;
                    } finally {
                        huc.disconnect();
                    }
                }
            }

            return c.getInputStream();
        }
        throw new IOException("Too many redirects while trying to fetch " + url);
    }
}

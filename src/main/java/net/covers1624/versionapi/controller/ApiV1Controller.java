package net.covers1624.versionapi.controller;

import net.covers1624.quack.maven.MavenNotation;
import net.covers1624.versionapi.entity.ApiKey;
import net.covers1624.versionapi.entity.ModData;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.MarkJson;
import net.covers1624.versionapi.json.MarkLatestJson;
import net.covers1624.versionapi.json.MarkRecommendedJson;
import net.covers1624.versionapi.repo.ApiKeyRepository;
import net.covers1624.versionapi.repo.ModDataRepository;
import net.covers1624.versionapi.repo.ModVersionRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/api/v1/")
public class ApiV1Controller {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ApiKeyRepository apiKeyRepo;
    private final ModVersionRepository modVersionRepo;
    private final ModDataRepository modDataRepo;

    public ApiV1Controller(ApiKeyRepository apiKeyRepo, ModVersionRepository modVersionRepo, ModDataRepository modDataRepo) {
        this.apiKeyRepo = apiKeyRepo;
        this.modVersionRepo = modVersionRepo;
        this.modDataRepo = modDataRepo;
    }

    @PutMapping ("admin/add_key")
    public ResponseEntity<String> addApiKey(Authentication auth) {
        ApiKey key = (ApiKey) auth.getCredentials();
        if (key == null || !key.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    public ResponseEntity<String> markLatest(@RequestBody MarkLatestJson json, Authentication auth) throws IOException {
        ApiKey key = (ApiKey) auth.getCredentials();
        if (key == null || !key.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        MavenNotation notation = computeVersion(json);
        String[] segs = notation.version.split("-");
        if (segs.length != 2) {
            throw new RuntimeException("Invalid detected version. Expected 2 splits. " + notation.version);
        }
        String mcVersion = segs[0];
        String modVersion = segs[1];

        Optional<ModVersion> versionOpt = modVersionRepo.findVersionByModIdAndMcVersion(notation.module, mcVersion);

        ModVersion version = versionOpt.orElseGet(() -> {
            Optional<ModData> modDataOpt = modDataRepo.findVersionByModIdAndMcVersion(notation.module, mcVersion);

            ModData modData = modDataOpt.orElseGet(() -> {
                ModData m = new ModData();
                m.setModId(notation.module);
                m.setMcVersion(mcVersion);
                m.setHomepage(json.homepage);
                modDataRepo.save(m);
                return m;
            });

            ModVersion m = new ModVersion(notation.module, mcVersion);
            m.setModData(modData);
            m.setLatest(modVersion);
            modVersionRepo.save(m);
            return m;
        });

        return ResponseEntity.ok(version.getLatest());
    }

    @ResponseBody
    @PostMapping (
            value = "mark_recommended",
            consumes = "application/json"
    )
    public ResponseEntity<String> markRecommended(@RequestBody MarkRecommendedJson json, Authentication auth) throws IOException {
        ApiKey key = (ApiKey) auth.getCredentials();
        if (key == null || !key.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        MavenNotation notation = computeVersion(json);
        String[] segs = notation.version.split("-");
        if (segs.length != 2) {
            throw new RuntimeException("Invalid detected version. Expected 2 splits. " + notation.version);
        }
        String mcVersion = segs[0];
        String modVersion = segs[1];

        Optional<ModVersion> versionOpt = modVersionRepo.findVersionByModIdAndMcVersion(notation.module, mcVersion);

        if (versionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Version does not exist. Mark as Latest first. :" + json.suffix);
        }
        ModVersion version = versionOpt.get();
        version.setRecommended(modVersion);
        modVersionRepo.save(version);

        return ResponseEntity.ok(version.getRecommended());
    }

    private static MavenNotation computeVersion(MarkJson json) throws IOException {
        MavenNotation notation = MavenNotation.parse(json.coordinates);
        URL url = new URL(StringUtils.appendIfMissing(json.mavenRepo, "/") + notation.toModulePath() + "maven-metadata.xml");
        try (InputStream is = openUrlStream(url)) {
            Metadata metadata = new MetadataXpp3Reader().read(is);
            Optional<String> versionOpt = metadata.getVersioning().getVersions()
                    .stream()
                    .filter(e -> e.endsWith(json.suffix))
                    .findFirst();
            return notation.withVersion(versionOpt.orElseThrow(() -> new RuntimeException("Failed to find version for build number: " + json.suffix)));
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Failed to parse MavenMetadata.", e);
        }
    }

    private static InputStream openUrlStream(URL url) throws IOException {
        URL currentUrl = url;
        for (int redirects = 0; redirects < 20; redirects++) {
            URLConnection c = currentUrl.openConnection();
            if (c instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection) c;
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

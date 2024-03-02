package net.covers1624.versionapi.controller.v1;

import net.covers1624.quack.collection.FastStream;
import net.covers1624.quack.maven.MavenNotation;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.MarkJson;
import net.covers1624.versionapi.repo.ModVersionRepository;
import net.covers1624.versionapi.security.ApiAuth;
import net.covers1624.versionapi.service.VersionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/api/v1/")
public class ApiV1Controller {

    private final ModVersionRepository modVersionRepo;
    private final VersionService versionService;

    public ApiV1Controller(ModVersionRepository modVersionRepo, VersionService versionService) {
        this.modVersionRepo = modVersionRepo;
        this.versionService = versionService;
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
        }
        version.setLatest(modVersion);
        modVersionRepo.save(version);
        versionService.buildCache(version);

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
        versionService.buildCache(version);

        return ResponseEntity.ok(version.getRecommended());
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

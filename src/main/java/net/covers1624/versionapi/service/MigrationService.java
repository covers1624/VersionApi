package net.covers1624.versionapi.service;

import net.covers1624.quack.collection.FastStream;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.repo.JsonCacheRepo;
import net.covers1624.versionapi.repo.ModVersionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 2/2/24.
 */
@Service
public class MigrationService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ModVersionRepository versionRepo;
    private final VersionService versionService;

    public MigrationService(ModVersionRepository versionRepo, VersionService versionService) {
        this.versionRepo = versionRepo;
        this.versionService = versionService;
    }

    @PostConstruct
    public void migrate() throws IOException {
        boolean migrated = migrateLegacy();
        migrated |= migrateOld();

        if (!migrated) return;

        LOGGER.info("Rebuilding cache..");
        for (ModVersion version : versionRepo.findAll()) {
            versionService.buildCache(version);
        }
    }

    // Legacy, old version.php database.
    private boolean migrateLegacy() throws IOException {
        Path file = Path.of("./migrations/legacy.csv");
        if (Files.notExists(file)) return false;

        LOGGER.info("Importing legacy data..");

        List<String> lines = Files.readAllLines(file);
        for (String line : lines.subList(1, lines.size())) {
            String[] segs = line.split(",");
            String mod = segs[0];
            String mc = segs[1];
            String r = segs[2];
            String n = segs[3];
            if (!n.equals(r)) throw new RuntimeException("ASDF");
            ModVersion version = versionRepo.findVersionByModIdAndMcVersion(mod, mc);
            if (version == null) {
                version = new ModVersion(mod, mc, null);
            }
            version.setLatest(n);
            version.setRecommended(n);
            versionRepo.save(version);
        }
        return true;
    }

    private boolean migrateOld() throws IOException {
        Path vData = Path.of("./migrations/version_data.csv");
        Path versions = Path.of("./migrations/versions.csv");
        if (Files.notExists(vData) || Files.notExists(versions)) return false;

        LOGGER.info("Importing old data..");
        Map<String, String> homePages = FastStream.of(Files.readAllLines(vData))
                .skip(1)
                .map(e -> e.split(","))
                .toMap(e -> e[0], e -> e[1]);

        List<String> lines = Files.readAllLines(versions);
        for (String line : lines.subList(1, lines.size())) {
            String[] segs = line.split(",");
            String mod = segs[3];
            String mc = segs[2];
            String latest = segs[1];
            String recommended = segs[4];
            String homepage = homePages.get(segs[5]);
            ModVersion version = versionRepo.findVersionByModIdAndMcVersion(mod, mc);
            if (version == null) {
                version = new ModVersion(mod, mc, homepage);
            }
            version.setLatest(!latest.isEmpty() ? latest : null);
            version.setRecommended(!recommended.isEmpty() ? recommended : null);
            versionRepo.save(version);
        }
        return true;
    }
}

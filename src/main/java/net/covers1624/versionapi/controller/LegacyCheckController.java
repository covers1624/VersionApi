package net.covers1624.versionapi.controller;

import com.google.gson.Gson;
import net.covers1624.versionapi.entity.JsonCache;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.repo.JsonCacheRepo;
import net.covers1624.versionapi.repo.ModVersionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This matches the format used by CB's legacy version.php script.
 * <p>
 * Created by covers1624 on 2/2/24.
 */
@Controller
@RequestMapping ("/")
public class LegacyCheckController {

    private final Gson gson;
    private final ModVersionRepository modVersionRepo;
    private final JsonCacheRepo cacheRepo;

    public LegacyCheckController(Gson gson, ModVersionRepository modVersionRepo, JsonCacheRepo cacheRepo) {
        this.gson = gson;
        this.modVersionRepo = modVersionRepo;
        this.cacheRepo = cacheRepo;
    }

    @RequestMapping ("/check_legacy")
    public ResponseEntity<String> checkVersion(
            @RequestParam (value = "file", required = false) String file,
            @RequestParam (value = "version", required = false) String version,
            @RequestParam (value = "query", defaultValue = "legacy") String query
    ) {
        if (file == null) return ResponseEntity.ok("Err: Missing param: file");
        if (version == null) return ResponseEntity.ok("Err: Missing param: version");

        if ("forge".equals(query)) {
            JsonCache cache = cacheRepo.findByModIdAndMcVersion(file, version);
            if (cache != null) return ResponseEntity.ok(cache.getValue());
        }

        ModVersion v = modVersionRepo.findVersionByModIdAndMcVersion(file, version);
        if (v == null || v.getRecommended() == null) return ResponseEntity.ok("Err: Unknown mod/version");

        if (query.equals("legacy")) return ResponseEntity.ok("Ret: " + v.getRecommended());
        if (query.equals("forge")) return ResponseEntity.ok(gson.toJson(ForgeVersionJson.create(v)));
        return ResponseEntity.ok("Err: Invalid query type.");
    }
}

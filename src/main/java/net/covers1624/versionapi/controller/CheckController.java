package net.covers1624.versionapi.controller;

import com.google.gson.Gson;
import net.covers1624.versionapi.entity.JsonCache;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.repo.JsonCacheRepo;
import net.covers1624.versionapi.repo.ModVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/")
public class CheckController {

    private static final Gson GSON = new Gson();

    private static final String EMPTY_PROMOS = GSON.toJson(new ForgeVersionJson(null, Map.of()));

    private final ModVersionRepository modVersionRepo;
    private final JsonCacheRepo cacheRepo;

    public CheckController(ModVersionRepository modVersionRepo, JsonCacheRepo cacheRepo) {
        this.modVersionRepo = modVersionRepo;
        this.cacheRepo = cacheRepo;
    }

    @RequestMapping (value = "/check", produces = "application/json")
    public ResponseEntity<String> checkVersion(@RequestParam ("mod") String mod, @RequestParam ("mc") String mc) {
        // Query json cache stored in DB first.
        // This is auto updated when latest/recommended changes.
        JsonCache cache = cacheRepo.findByModIdAndMcVersion(mod, mc);
        if (cache != null) return ResponseEntity.ok(cache.getValue());

        // Try slow path.
        // We will hit this if cache fails to build or the version does not exist yet.
        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(mod, mc);
        if (version == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EMPTY_PROMOS);

        // Full Cache miss, this should never happen, but for integrity, sure.
        return ResponseEntity.ok(GSON.toJson(ForgeVersionJson.create(version)));
    }
}

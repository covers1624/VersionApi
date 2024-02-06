package net.covers1624.versionapi.controller;

import com.google.gson.Gson;
import net.covers1624.versionapi.entity.JsonCache;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.repo.JsonCacheRepo;
import net.covers1624.versionapi.repo.ModVersionRepository;
import net.covers1624.versionapi.service.MetricsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/")
public class CheckController {

    private final Gson gson;
    private final ModVersionRepository modVersionRepo;
    private final JsonCacheRepo cacheRepo;
    private final MetricsService metrics;

    private final String empty;

    public CheckController(Gson gson, ModVersionRepository modVersionRepo, JsonCacheRepo cacheRepo, MetricsService metrics) {
        this.gson = gson;
        this.modVersionRepo = modVersionRepo;
        this.cacheRepo = cacheRepo;
        this.metrics = metrics;

        empty = gson.toJson(new ForgeVersionJson(null, Map.of()));
    }

    @RequestMapping (value = "/check", produces = "application/json")
    public ResponseEntity<String> checkVersion(@RequestParam ("mod") String mod, @RequestParam ("mc") String mc) {
        // Query json cache stored in DB first.
        // This is auto updated when latest/recommended changes.
        JsonCache cache = cacheRepo.findByModIdAndMcVersion(mod, mc);
        if (cache != null) {
            metrics.check("cached", mod, mc);
            return ResponseEntity.ok(cache.getValue());
        }

        // Try slow path.
        // We will hit this if cache fails to build or the version does not exist yet.
        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(mod, mc);
        if (version == null) {
            metrics.check("failed", mod, mc);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(empty);
        }

        metrics.check("uncached", mod, mc);
        // Full Cache miss, this should never happen, but for integrity, sure.
        return ResponseEntity.ok(gson.toJson(ForgeVersionJson.create(version)));
    }
}

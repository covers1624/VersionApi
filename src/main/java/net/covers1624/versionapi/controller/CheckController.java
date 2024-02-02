package net.covers1624.versionapi.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.repo.ModVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/")
public class CheckController {

    private static final Gson GSON = new Gson();

    private static final String EMPTY_PROMOS = GSON.toJson(new ForgeVersionJson(null, Map.of()));

    private final ModVersionRepository modVersionRepo;

    private final Cache<String, String> jsonCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    public CheckController(ModVersionRepository modVersionRepo) {
        this.modVersionRepo = modVersionRepo;
    }

    @Transactional
    @RequestMapping (value = "/check", produces = "application/json")
    public ResponseEntity<String> checkVersion(@RequestParam ("mod") String mod, @RequestParam ("mc") String mc) {
        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(mod, mc);
        if (version == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EMPTY_PROMOS);

        String cacheKey = mod + "," + mc + "," + version.getRecommended() + "," + version.getLatest();
        String cachedJson = jsonCache.getIfPresent(cacheKey);
        //Kay, doesn't exist in cache, lets compute it.
        if (cachedJson == null) {
            synchronized (jsonCache) {
                //We have the lock now, does it really not exist?
                cachedJson = jsonCache.getIfPresent(cacheKey);
                if (cachedJson != null) {
                    return ResponseEntity.ok(cachedJson);
                }

                ForgeVersionJson json = ForgeVersionJson.create(version);
                cachedJson = GSON.toJson(json);
                jsonCache.put(cacheKey, cachedJson);
            }
        }

        return ResponseEntity.ok(cachedJson);
    }
}

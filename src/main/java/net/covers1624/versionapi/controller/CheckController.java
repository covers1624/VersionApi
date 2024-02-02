package net.covers1624.versionapi.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.covers1624.versionapi.entity.ModData;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.repo.ModDataRepository;
import net.covers1624.versionapi.repo.ModVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 6/1/21.
 */
@Controller
@RequestMapping ("/")
public class CheckController {

    private static final String EMPTY_PROMOS = ForgeVersionJson.GSON.toJson(new ForgeVersionJson());

    private final ModVersionRepository modVersionRepo;
    private final ModDataRepository modDataRepo;

    private final Cache<String, String> jsonCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    public CheckController(ModVersionRepository modVersionRepo, ModDataRepository modDataRepo) {
        this.modVersionRepo = modVersionRepo;
        this.modDataRepo = modDataRepo;
    }

    @Transactional
    @RequestMapping (value = "/check", produces = "application/json")
    public ResponseEntity<String> checkVersion(@RequestParam ("mod") String mod, @RequestParam ("mc") String mc) {
        Optional<ModVersion> versionOpt = modVersionRepo.findVersionByModIdAndMcVersion(mod, mc);
        if (versionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EMPTY_PROMOS);
        }
        ModVersion version = versionOpt.get();
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
                Optional<ModData> modDataOpt = modDataRepo.findVersionByModIdAndMcVersion(mod, mc);

                ForgeVersionJson json = new ForgeVersionJson();

                if (version.getRecommended() != null) {
                    json.addPromotion(ForgeVersionJson.PromotionType.RECOMMENDED, version.getMcVersion(), version.getRecommended());
                }

                if (version.getLatest() != null) {
                    json.addPromotion(ForgeVersionJson.PromotionType.LATEST, version.getMcVersion(), version.getLatest());
                }

                if (modDataOpt.isPresent()) {
                    ModData modData = modDataOpt.get();
                    json.homepage = modData.getHomepage();
                }
                cachedJson = ForgeVersionJson.GSON.toJson(json);
                jsonCache.put(cacheKey, cachedJson);
            }
        }

        return ResponseEntity.ok(cachedJson);
    }

}

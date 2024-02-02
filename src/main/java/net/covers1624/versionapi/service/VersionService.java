package net.covers1624.versionapi.service;

import com.google.gson.Gson;
import net.covers1624.versionapi.entity.JsonCache;
import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.ForgeVersionJson;
import net.covers1624.versionapi.repo.JsonCacheRepo;
import org.springframework.stereotype.Service;

/**
 * Created by covers1624 on 2/2/24.
 */
@Service
public class VersionService {

    private final Gson gson;
    private final JsonCacheRepo cacheRepo;

    public VersionService(Gson gson, JsonCacheRepo cacheRepo) {
        this.gson = gson;
        this.cacheRepo = cacheRepo;
    }

    public void buildCache(ModVersion version) {
        JsonCache cache = cacheRepo.findByModIdAndMcVersion(version.getModId(), version.getMcVersion());
        if (cache == null) {
            cache = new JsonCache(version.getModId(), version.getMcVersion());
        }
        cache.setValue(gson.toJson(ForgeVersionJson.create(version)));
        cacheRepo.save(cache);
    }
}

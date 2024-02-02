package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.JsonCache;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by covers1624 on 2/2/24.
 */
public interface JsonCacheRepo extends CrudRepository<JsonCache, Long> {

    JsonCache findByModIdAndMcVersion(String modId, String mcVersion);
}

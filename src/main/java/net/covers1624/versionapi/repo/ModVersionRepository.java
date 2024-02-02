package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.ModVersion;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by covers1624 on 6/1/21.
 */
public interface ModVersionRepository extends CrudRepository<ModVersion, Long> {

    ModVersion findVersionByModIdAndMcVersion(String modId, String mcVersion);
}

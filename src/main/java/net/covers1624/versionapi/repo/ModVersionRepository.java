package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.ModVersion;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by covers1624 on 6/1/21.
 */
public interface ModVersionRepository extends CrudRepository<ModVersion, Long> {

    Optional<ModVersion> findVersionByModIdAndMcVersion(String modId, String mcVersion);

}

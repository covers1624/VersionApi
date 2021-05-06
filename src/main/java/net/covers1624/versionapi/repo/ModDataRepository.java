package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.ModData;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by covers1624 on 6/1/21.
 */
public interface ModDataRepository extends CrudRepository<ModData, Long> {

    Optional<ModData> findVersionByModIdAndMcVersion(String modId, String mcVersion);

}

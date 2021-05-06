package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.ChangeLog;
import net.covers1624.versionapi.entity.ModData;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by covers1624 on 6/1/21.
 */
public interface ChangeLogRepository extends CrudRepository<ChangeLog, Long> {

    Stream<ChangeLog> findByModData(ModData modData);

    @Transactional
    default List<ChangeLog> getByModData(ModData modData, int maxCount) {
        return findByModData(modData)
                .sorted(Comparator.comparing(e -> new DefaultArtifactVersion(e.getVersion())))
                .limit(maxCount)
                .collect(Collectors.toList());
    }

}

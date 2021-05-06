package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.ApiKey;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by covers1624 on 5/11/20.
 */
public interface ApiKeyRepository extends CrudRepository<ApiKey, Long> {

    Optional<ApiKey> findBySecret(String secret);

}

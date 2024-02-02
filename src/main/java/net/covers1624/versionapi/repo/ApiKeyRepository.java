package net.covers1624.versionapi.repo;

import net.covers1624.versionapi.entity.ApiKey;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by covers1624 on 5/11/20.
 */
public interface ApiKeyRepository extends CrudRepository<ApiKey, Long> {

    ApiKey findBySecret(String secret);

}

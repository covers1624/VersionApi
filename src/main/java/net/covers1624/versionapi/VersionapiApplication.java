package net.covers1624.versionapi;

import net.covers1624.versionapi.entity.ApiKey;
import net.covers1624.versionapi.repo.ApiKeyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class VersionapiApplication {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        SpringApplication.run(VersionapiApplication.class, args);
    }

    @Autowired
    public void onApikeyRepoReady(ApiKeyRepository repo) {
        if (repo.count() == 0) {
            ApiKey key = new ApiKey(UUID.randomUUID().toString());
            key.setAdmin(true);
            repo.save(key);
            LOGGER.info("\n\n\nCreated default Admin api key: {}\n\n", key.getSecret());
        }
    }

}

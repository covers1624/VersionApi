package net.covers1624.versionapi.controller.v1;

import net.covers1624.versionapi.entity.ApiKey;
import net.covers1624.versionapi.repo.ApiKeyRepository;
import net.covers1624.versionapi.security.ApiAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * Created by covers1624 on 2/2/24.
 */
@Controller
@RequestMapping ("/api/v1/admin")
public class AdminV1Controller {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ApiKeyRepository apiKeyRepo;

    public AdminV1Controller(ApiKeyRepository apiKeyRepo) {
        this.apiKeyRepo = apiKeyRepo;
    }

    @PutMapping ("admin/add_key")
    public ResponseEntity<String> addApiKey(ApiAuth auth) {
        auth.requireAdmin("Only admins can add api keys.");

        ApiKey newKey = new ApiKey(UUID.randomUUID().toString());
        apiKeyRepo.save(newKey);
        LOGGER.info("Added new API Key: " + newKey.getSecret());
        return ResponseEntity.ok(newKey.getSecret());
    }
}

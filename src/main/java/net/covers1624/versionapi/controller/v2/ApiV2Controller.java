package net.covers1624.versionapi.controller.v2;

import net.covers1624.versionapi.entity.ModVersion;
import net.covers1624.versionapi.json.MarkJsonV2;
import net.covers1624.versionapi.repo.ModVersionRepository;
import net.covers1624.versionapi.security.ApiAuth;
import net.covers1624.versionapi.service.VersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by covers1624 on 12/3/25.
 */
@Controller
@RequestMapping ("/api/v2/")
public class ApiV2Controller {

    private final ModVersionRepository modVersionRepo;
    private final VersionService versionService;

    public ApiV2Controller(ModVersionRepository modVersionRepo, VersionService versionService) {
        this.modVersionRepo = modVersionRepo;
        this.versionService = versionService;
    }

    @ResponseBody
    @PostMapping (
            value = "mark_latest",
            consumes = "application/json"
    )
    public ResponseEntity<String> markLatest(ApiAuth auth, @RequestBody MarkJsonV2 json) {
        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(json.modId(), json.mcVersion());
        if (version == null) {
            version = new ModVersion(json.modId(), json.mcVersion(), json.homepage());
        }
        version.setLatest(json.modVersion());
        modVersionRepo.save(version);
        versionService.buildCache(version);

        return ResponseEntity.ok(version.getLatest());
    }

    @ResponseBody
    @PostMapping (
            value = "mark_recommended",
            consumes = "application/json"
    )
    public ResponseEntity<String> markRecommended(ApiAuth auth, @RequestBody MarkJsonV2 json) {
        ModVersion version = modVersionRepo.findVersionByModIdAndMcVersion(json.modId(), json.mcVersion());
        if (version == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Version does not exist. Mark as Latest first. " + json.modId() + ":" + json.modVersion());

        version.setRecommended(json.modVersion());
        modVersionRepo.save(version);
        versionService.buildCache(version);

        return ResponseEntity.ok(version.getRecommended());
    }
}

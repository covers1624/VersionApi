package net.covers1624.versionapi.json;

import net.covers1624.versionapi.entity.ModVersion;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 6/1/21.
 */
public record ForgeVersionJson(
        @Nullable String homepage,
        Map<String, String> promos
) {

    public static ForgeVersionJson create(ModVersion version) {
        Map<String, String> map = new HashMap<>();
        if (version.getLatest() != null) map.put(version.getMcVersion() + "-latest", version.getLatest());
        if (version.getRecommended() != null) map.put(version.getMcVersion() + "-recommended", version.getRecommended());
        return new ForgeVersionJson(version.getHomepage(), map);
    }
}

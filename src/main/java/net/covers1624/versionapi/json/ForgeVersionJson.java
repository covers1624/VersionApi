package net.covers1624.versionapi.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by covers1624 on 6/1/21.
 */
public class ForgeVersionJson {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ForgeVersionJson.class, new Serializer())
            .create();

    public String homepage;

    public List<Promotion> promos = new ArrayList<>();

    public Map<String, List<ChangeLog>> changelog = new HashMap<>();

    public void addPromotion(PromotionType type, String mcVersion, String version) {
        promos.add(new Promotion(type, mcVersion, version));
    }

    public void addChangelog(String mcVersion, String version, String changelog) {
        this.changelog.computeIfAbsent(mcVersion, e -> new ArrayList<>())
                .add(new ChangeLog(version, changelog));
    }

    public static class Promotion {
        public PromotionType type;
        public String mcVersion;
        public String version;

        public Promotion(PromotionType type, String mcVersion, String version) {
            this.type = type;
            this.mcVersion = mcVersion;
            this.version = version;
        }
    }

    public enum PromotionType {
        RECOMMENDED,
        LATEST,
    }

    public static class ChangeLog {
        public String version;
        public String changelog;

        public ChangeLog(String version, String changelog) {
            this.version = version;
            this.changelog = changelog;
        }
    }

    public static class Serializer implements JsonSerializer<ForgeVersionJson> {

        @Override
        public JsonElement serialize(ForgeVersionJson src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            if (src.homepage != null) {
                json.addProperty("homepage", src.homepage);
            }
            JsonObject promos = new JsonObject();
            for (Promotion promo : src.promos) {
                promos.addProperty(promo.mcVersion + "-" + promo.type.name().toLowerCase(Locale.ROOT), promo.version);
            }

            json.add("promos", promos);

            for (Map.Entry<String, List<ChangeLog>> entry : src.changelog.entrySet()) {
                JsonObject versionChangelog = new JsonObject();
                for (ChangeLog changeLog : entry.getValue()) {
                    versionChangelog.addProperty(changeLog.version, changeLog.changelog);
                }
                json.add(entry.getKey(), versionChangelog);
            }
            return json;
        }
    }

}

package net.covers1624.versionapi.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by covers1624 on 6/1/21.
 */
public class ForgeVersionJson {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ForgeVersionJson.class, new Serializer())
            .create();

    public String homepage;

    public List<Promotion> promos = new ArrayList<>();

    public void addPromotion(PromotionType type, String mcVersion, String version) {
        promos.add(new Promotion(type, mcVersion, version));
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
            return json;
        }
    }

}

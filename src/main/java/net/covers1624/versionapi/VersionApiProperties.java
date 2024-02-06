package net.covers1624.versionapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by covers1624 on 6/2/24.
 */
@Component
@ConfigurationProperties ("versionapi")
public class VersionApiProperties {

    private String influxHost;
    private String influxToken;
    private String influxBucket;
    private String influxOrg;

    // @formatter:off
    public String getInfluxHost() { return influxHost; }
    public String getInfluxToken() { return influxToken; }
    public String getInfluxBucket() { return influxBucket; }
    public String getInfluxOrg() { return influxOrg; }
    public void setInfluxHost(String influxHost) { this.influxHost = influxHost; }
    public void setInfluxToken(String influxToken) { this.influxToken = influxToken; }
    public void setInfluxBucket(String influxBucket) { this.influxBucket = influxBucket; }
    public void setInfluxOrg(String influxOrg) { this.influxOrg = influxOrg; }
    // @formatter:on
}

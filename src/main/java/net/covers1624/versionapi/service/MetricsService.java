package net.covers1624.versionapi.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import net.covers1624.versionapi.VersionApiProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Created by covers1624 on 4/2/24.
 */
@Service
@EnableScheduling
public class MetricsService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final @Nullable InfluxDBClient client;
    private final @Nullable WriteApi writeApi;
    private final @Nullable String org;
    private final @Nullable String bucket;

    public MetricsService(VersionApiProperties properties) {
        String host = properties.getInfluxHost();
        String token = properties.getInfluxToken();
        org = properties.getInfluxOrg();
        bucket = properties.getInfluxBucket();
        if (StringUtils.isEmpty(host) || StringUtils.isEmpty(token) || StringUtils.isEmpty(org) || StringUtils.isEmpty(bucket)) {
            LOGGER.warn("Influx disabled.");
            LOGGER.warn("Have host: {}", StringUtils.isEmpty(host) ? "NO" : "YES");
            LOGGER.warn("Have token: {}", StringUtils.isEmpty(token) ? "NO" : "YES");
            LOGGER.warn("Have org: {}", StringUtils.isEmpty(org) ? "NO" : "YES");
            LOGGER.warn("Have bucket: {}", StringUtils.isEmpty(bucket) ? "NO" : "YES");
            client = null;
            writeApi = null;
            return;
        }

        client = InfluxDBClientFactory.create(host, token.toCharArray());
        writeApi = client.makeWriteApi();
    }

    public void check(String status, String mod, String mc) {
        check(null, status, mod, mc);
    }

    public void check(@Nullable String type, String status, String mod, String mc) {
        if (writeApi == null) return;

        addPoint(
                Point.measurement("check")
                        .addTag("type", type)
                        .addTag("status", status)
                        .addTag("mod", mod)
                        .addTag("mc", mc)
                        .addField("count", 1)
                        .time(Instant.now(), WritePrecision.NS)
        );
    }

    private void addPoint(Point point) {
        assert writeApi != null;
        assert bucket != null;
        assert org != null;

        writeApi.writePoint(bucket, org, point);
    }
}

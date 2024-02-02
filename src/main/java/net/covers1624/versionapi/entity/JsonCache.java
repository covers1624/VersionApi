package net.covers1624.versionapi.entity;

import javax.persistence.*;

/**
 * Created by covers1624 on 2/2/24.
 */
@Entity (name = "cache")
@Table (
        uniqueConstraints = {
                @UniqueConstraint (columnNames = { "modId", "mcVersion" })
        },
        indexes = {
                @Index (columnList = "modId,mcVersion")
        }
)
public class JsonCache {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long vLock;

    @Column (nullable = false)
    private String modId;

    @Column (nullable = false)
    private String mcVersion;

    @Column (nullable = false, columnDefinition = "LONGTEXT")
    private String value;

    public JsonCache() {
    }

    public JsonCache(String modId, String mcVersion) {
        this.modId = modId;
        this.mcVersion = mcVersion;
    }

    // @formatter:off
    public long getId() { return id; }
    public long getVLock() { return vLock; }
    public String getModId() { return modId; }
    public String getMcVersion() { return mcVersion; }
    public String getValue() { return value; }
    public void setModId(String modId) { this.modId = modId; }
    public void setMcVersion(String mcVersion) { this.mcVersion = mcVersion; }
    public void setValue(String value) { this.value = value; }
    // @formatter:on
}

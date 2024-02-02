package net.covers1624.versionapi.entity;

import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

/**
 * Created by covers1624 on 6/1/21.
 */
@Entity (name = "versions")
@Table (
        uniqueConstraints = {
                @UniqueConstraint (columnNames = { "modId", "mcVersion" })
        },
        indexes = {
                @Index (columnList = "modId,mcVersion")
        }
)
public class ModVersion {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long vLock;

    @Column (nullable = false)
    private String modId;

    @Column (nullable = false)
    private String mcVersion;

    private @Nullable String homepage;

    private @Nullable String recommended;

    private @Nullable String latest;

    public ModVersion() {
    }

    public ModVersion(String modId, String mcVersion, @Nullable String homepage) {
        this.modId = modId;
        this.mcVersion = mcVersion;
        this.homepage = homepage;
    }

    //@formatter:off
    public long getId() { return id; }
    public long getVLock() { return vLock; }
    public String getModId() { return modId; }
    public String getMcVersion() { return mcVersion; }
    public @Nullable String getHomepage() { return homepage; }
    public @Nullable String getRecommended() { return recommended; }
    public @Nullable String getLatest() { return latest; }
    public void setModId(String modId) { this.modId = modId; }
    public void setMcVersion(String mcVersion) { this.mcVersion = mcVersion; }
    public void setHomepage(@Nullable String homepage) { this.homepage = homepage; }
    public void setRecommended(@Nullable String recommended) { this.recommended = recommended; }
    public void setLatest(@Nullable String latest) { this.latest = latest; }
    //@formatter:on
}

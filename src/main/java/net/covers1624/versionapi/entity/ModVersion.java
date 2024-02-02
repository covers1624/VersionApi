package net.covers1624.versionapi.entity;

import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

/**
 * Created by covers1624 on 6/1/21.
 */
@Entity (name = "versions")
@Table (
        uniqueConstraints = @UniqueConstraint (
                columnNames = { "modId", "mcVersion" }
        )
)
public class ModVersion {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String modId;

    @Column (nullable = false)
    private String mcVersion;

    private @Nullable String homepage;

    private String recommended;

    private String latest;

    public ModVersion() {
    }

    public ModVersion(String modId, String mcVersion, @Nullable String homepage) {
        this.modId = modId;
        this.mcVersion = mcVersion;
        this.homepage = homepage;
    }

    //@formatter:off
    public void setModId(String modId) { this.modId = modId; }
    public void setMcVersion(String mcVersion) { this.mcVersion = mcVersion; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    public void setRecommended(String recommended) { this.recommended = recommended; }
    public void setLatest(String latest) { this.latest = latest; }
    public long getId() { return id; }
    public String getModId() { return modId; }
    public String getMcVersion() { return mcVersion; }
    public String getHomepage() { return homepage; }
    public String getRecommended() { return recommended; }
    public String getLatest() { return latest; }
    //@formatter:on
}

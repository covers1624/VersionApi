package net.covers1624.versionapi.entity;

import javax.persistence.*;

/**
 * Created by covers1624 on 6/1/21.
 */
@Entity(name = "versions")
//@Table (uniqueConstraints = @UniqueConstraint (columnNames = { "modId", "mcVersion" }))
public class ModVersion {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String modId;

    @Column (nullable = false)
    private String mcVersion;

    private String recommended;

    private String latest;

    @ManyToOne
    private ModData modData;

    public ModVersion() {
    }

    public ModVersion(String modId, String mcVersion) {
        this.modId = modId;
        this.mcVersion = mcVersion;
    }

    //@formatter:off
    public void setModId(String mod) { this.modId = mod; }
    public void setMcVersion(String mcVersion) { this.mcVersion = mcVersion; }
    public void setRecommended(String recommended) { this.recommended = recommended; }
    public void setLatest(String latest) { this.latest = latest; }
    public void setModData(ModData modData) { this.modData = modData; }
    public long getId() { return id; }
    public String getModId() { return modId; }
    public String getMcVersion() { return mcVersion; }
    public String getRecommended() { return recommended; }
    public String getLatest() { return latest; }
    public ModData getModData() { return modData; }
    //@formatter:on
}

package net.covers1624.versionapi.entity;

import javax.persistence.*;

/**
 * Created by covers1624 on 6/1/21.
 */
@Entity (name = "version_data")
//@Table (uniqueConstraints = @UniqueConstraint (columnNames = { "modId", "mcVersion" }))
public class ModData {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String modId;

    @Column (nullable = false)
    private String mcVersion;

    private String homepage;

    //@formatter:off
    public void setModId(String mod) { this.modId = mod; }
    public void setMcVersion(String mcVersion) { this.mcVersion = mcVersion; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    public long getId() { return id; }
    public String getModId() { return modId; }
    public String getMcVersion() { return mcVersion; }
    public String getHomepage() { return homepage; }
    //@formatter:on


    public enum Tag {
        NONE,        //Not tagged
        LATEST,      //Currently tagged latest, only one allowed per mod & mc version
        RECOMMENDED, //
    }
}

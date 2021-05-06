package net.covers1624.versionapi.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by covers1624 on 6/1/21.
 */
@Entity (name = "changelog")
public class ChangeLog {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String version;

    @ElementCollection
    private List<String> changelog;

    @ManyToOne
    private ModData modData;

    //@formatter:off
    public void setVersion(String version) { this.version = version; }
    public void setChangelog(List<String> changelog) { this.changelog = changelog; }
    public void setModData(ModData modData) { this.modData = modData; }
    public long getId() { return id; }
    public String getVersion() { return version; }
    public List<String> getChangelog() { return changelog; }
    public ModData getModData() { return modData; }
    //@formatter:on
}

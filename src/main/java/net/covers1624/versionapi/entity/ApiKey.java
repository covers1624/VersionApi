package net.covers1624.versionapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Created by covers1624 on 5/11/20.
 */
@Entity (name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String secret;

    private boolean admin;

    public ApiKey() {
    }

    public ApiKey(String secret) {
        this.secret = secret;
    }

    //@formatter:off
    public long getId() { return id; }
    public String getSecret() { return secret; }
    public boolean isAdmin() { return admin; }
    public void setSecret(String secret) { this.secret = secret; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    //@formatter:on
}

package net.covers1624.versionapi.security;

import net.covers1624.versionapi.entity.ApiKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Created by covers1624 on 2/2/24.
 */
public record ApiAuth(ApiKey apiKey) implements Authentication {

    public void requireAdmin(String message) {
        if (!apiKey.isAdmin()) {
            throw new InsufficientPermissionsException(message);
        }
    }

    // @formatter:off
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public Object getCredentials() { return apiKey; }
    @Override public Object getDetails() { return null; }
    @Override public Object getPrincipal() { return apiKey; }
    @Override public boolean isAuthenticated() { return true; }
    @Override public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException { }
    @Override public String getName() { return "Api Key: " + apiKey.getSecret(); }
    // @formatter:on
}

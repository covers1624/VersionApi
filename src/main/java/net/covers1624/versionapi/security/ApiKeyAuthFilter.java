package net.covers1624.versionapi.security;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by covers1624 on 7/11/20.
 */
public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static final String HEADER = "API-Key";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(HEADER);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "No";
    }
}

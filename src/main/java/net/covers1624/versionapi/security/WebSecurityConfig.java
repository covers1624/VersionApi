package net.covers1624.versionapi.security;

import net.covers1624.versionapi.entity.ApiKey;
import net.covers1624.versionapi.repo.ApiKeyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Created by covers1624 on 6/1/21.
 */
@Configuration
public class WebSecurityConfig {

    private final ApiKeyRepository apiKeyRepo;

    public WebSecurityConfig(ApiKeyRepository apiKeyRepo) {
        this.apiKeyRepo = apiKeyRepo;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
        filter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            ApiKey key = apiKeyRepo.findBySecret(principal);
            if (key == null) throw new BadCredentialsException("Invalid API Key.");
            return new ApiAuth(key);
        });
        http.headers(HeadersConfigurer::disable)
                .securityMatcher("/api/**")
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(e -> e.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(filter).authorizeHttpRequests(e -> e.anyRequest().authenticated());
        return http.build();
    }
}

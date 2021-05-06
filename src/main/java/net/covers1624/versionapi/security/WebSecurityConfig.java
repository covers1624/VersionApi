package net.covers1624.versionapi.security;

import net.covers1624.versionapi.entity.ApiKey;
import net.covers1624.versionapi.repo.ApiKeyRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.Optional;

/**
 * Created by covers1624 on 6/1/21.
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApiKeyRepository apiKeyRepo;

    public WebSecurityConfig(ApiKeyRepository apiKeyRepo) {
        this.apiKeyRepo = apiKeyRepo;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
        filter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            Optional<ApiKey> keyOpt = apiKeyRepo.findBySecret(principal);
            ApiKey key = keyOpt.orElseThrow(() -> new BadCredentialsException("Invalid API Key."));
            return new UsernamePasswordAuthenticationToken(key, key, null);
        });
        http.headers().disable().antMatcher("/api/**")
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(filter).authorizeRequests().anyRequest().authenticated();
    }
}

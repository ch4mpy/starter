package com.c4soft.commons.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.c4_soft.springaddons.security.oauth2.keycloak.KeycloakOidcIdAuthenticationConverter;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdAuthenticationToken;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${com.c4-soft.security.cors-path}")
    String corsPath;

    @Autowired
    Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;

    @Bean
    public Converter<Jwt, OidcIdAuthenticationToken> authenticationConverter() {
        return new KeycloakOidcIdAuthenticationConverter(authoritiesConverter);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(authenticationConverter());

        // @formatter:off
        http.anonymous().and()
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
                response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            }).and()
            .authorizeRequests()
                .antMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html").permitAll()
                .anyRequest().authenticated();

        http.requiresChannel().anyRequest().requiresSecure();
        // @formatter:on
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(corsPath)
                        .allowedOrigins("http://localhost", "https://localhost", "https://bravo-ch4mp:8100", "https://bravo-ch4mp:4200")
                        .allowedMethods("*")
                        .exposedHeaders("Origin", "Accept", "Content-Type", "Location");
            }

        };
    }
}

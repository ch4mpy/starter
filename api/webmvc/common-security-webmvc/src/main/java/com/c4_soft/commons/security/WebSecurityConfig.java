package com.c4_soft.commons.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.c4_soft.springaddons.security.oauth2.keycloak.KeycloakOidcIdAuthenticationConverter;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdAuthenticationToken;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${com.c4-soft.security.cors-path}")
    String corsPath;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    String issuerUri;

    @Autowired
    KeycloakAuthoritiesConverter authoritiesConverter;

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromOidcIssuerLocation(issuerUri);
    }

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
            });

        http.authorizeRequests().antMatchers(
                "/actuator/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/households/types").permitAll()
            .anyRequest().authenticated();
        // @formatter:on

        http.requiresChannel().anyRequest().requiresSecure();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(corsPath)
                        .allowedOrigins(
                                "http://localhost",
                                "https://localhost",
                                "https://localhost:8100",
                                "https://localhost:4200",
                                "http://localhost:8080",
                                "https://localhost:8443",
                                "https://bravo-ch4mp:8100",
                                "https://bravo-ch4mp:4200",
                                "http://bravo-ch4mp:8080",
                                "https://bravo-ch4mp:8443")
                        .allowedMethods("*")
                        .exposedHeaders("Origin", "Accept", "Content-Type", "Location");
            }

        };
    }

    protected ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorize(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        return registry.antMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll().anyRequest().authenticated();
    }

    @Component
    class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Value("${com.c4-soft.security.oauth2.client-id}")
        String clientId;

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            final var roles =
                    Optional.ofNullable((JSONObject) jwt.getClaims().get("resource_access"))
                            .flatMap(resourceAccess -> Optional.ofNullable((JSONObject) resourceAccess.get(clientId)))
                            .flatMap(clientResourceAccess -> Optional.ofNullable((JSONArray) clientResourceAccess.get("roles")))
                            .orElse(new JSONArray());

            return roles.stream().map(Object::toString).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }

    }
}

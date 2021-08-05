package com.c4_soft.commons.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    String issuerUri;

    @Value("${com.c4-soft.security.cors-path}")
    String corsPath;

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromOidcIssuerLocation(issuerUri);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveKeycloakOidcIdAuthenticationConverter authenticationConverter) {

        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(authenticationConverter);

        // @formatter:off
        http.anonymous().and()
            .cors().and()
            .csrf().disable()
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling()
                .accessDeniedHandler(new CommonServerAccessDeniedHandler());

        http.authorizeExchange().pathMatchers(
                "/actuator/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/swagger-ui/**",
                "/favicon.ico").permitAll()
            .anyExchange().authenticated();
        // @formatter:on

        http.redirectToHttps();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource getCorsConfiguration() {
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsPath, corsConfiguration());
        return source;
    }

    private CorsConfiguration corsConfiguration() {
        final var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Origin", "Accept", "Content-Type", "Location"));
        corsConfiguration.setAllowedOrigins(
                List.of(
                        "http://localhost",
                        "https://localhost",
                        "https://localhost:8100",
                        "https://localhost:4200",
                        "http://localhost:8080",
                        "https://localhost:8443",
                        "https://bravo-ch4mp:8100",
                        "https://bravo-ch4mp:4200",
                        "http://bravo-ch4mp:8080",
                        "https://bravo-ch4mp:8443"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        return corsConfiguration;
    }

    protected ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorize(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        return registry.antMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll().anyRequest().authenticated();
    }
}

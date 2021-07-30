package com.c4_soft.commons.security;

import java.nio.charset.Charset;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;

import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdAuthenticationToken;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdBuilder;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

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
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(corsPath)
                        .allowedOrigins("http://localhost", "https://localhost", "https://bravo-ch4mp:8100", "https://bravo-ch4mp:4200")
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
    static class KeycloakAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

        @Value("${com.c4-soft.security.oauth2.client-id}")
        String clientId;

        @Override
        @NonNull
        public Flux<GrantedAuthority> convert(Jwt jwt) {
            final var roles =
                    Optional.ofNullable((JSONObject) jwt.getClaims().get("resource_access"))
                            .flatMap(resourceAccess -> Optional.ofNullable((JSONObject) resourceAccess.get(clientId)))
                            .flatMap(clientResourceAccess -> Optional.ofNullable((JSONArray) clientResourceAccess.get("roles")))
                            .orElse(new JSONArray());

            return Flux.fromStream(roles.stream().map(Object::toString).map(SimpleGrantedAuthority::new));
        }

    }

    @Component
    static class ReactiveKeycloakOidcIdAuthenticationConverter implements Converter<Jwt, Mono<OidcIdAuthenticationToken>> {

        private final KeycloakAuthoritiesConverter authoritiesConverter;

        @Autowired
        public ReactiveKeycloakOidcIdAuthenticationConverter(KeycloakAuthoritiesConverter authoritiesConverter) {
            this.authoritiesConverter = authoritiesConverter;
        }

        @Override
        public Mono<OidcIdAuthenticationToken> convert(Jwt jwt) {
            final var token = new OidcIdBuilder(jwt.getClaims()).build();
            return authoritiesConverter.convert(jwt).collectList().map(authorities -> new OidcIdAuthenticationToken(token, authorities));
        }
    }

    static class CommonServerAccessDeniedHandler implements ServerAccessDeniedHandler {

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
            return exchange.getPrincipal().flatMap(principal -> {
                final var response = exchange.getResponse();
                response.setStatusCode(principal instanceof AnonymousAuthenticationToken ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN);
                response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
                var dataBufferFactory = response.bufferFactory();
                var buffer = dataBufferFactory.wrap(ex.getMessage().getBytes(Charset.defaultCharset()));
                return response.writeWith(Mono.just(buffer)).doOnError(error -> DataBufferUtils.release(buffer));
            });
        }

    }
}

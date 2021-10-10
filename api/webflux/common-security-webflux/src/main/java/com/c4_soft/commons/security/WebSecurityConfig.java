package com.c4_soft.commons.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.c4_soft.springaddons.security.oauth2.ReactiveJwt2GrantedAuthoritiesConverter;
import com.c4_soft.springaddons.security.oauth2.oidc.ReactiveJwt2OidcAuthenticationConverter;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Profile("!disable-security")
public class WebSecurityConfig {

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwt2GrantedAuthoritiesConverter authoritiesConverter) {

		http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(new ReactiveJwt2OidcAuthenticationConverter(authoritiesConverter));

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
}

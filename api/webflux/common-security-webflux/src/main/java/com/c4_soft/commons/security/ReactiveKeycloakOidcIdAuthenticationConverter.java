package com.c4_soft.commons.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdAuthenticationToken;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdBuilder;

import reactor.core.publisher.Mono;

@Component
public class ReactiveKeycloakOidcIdAuthenticationConverter implements Converter<Jwt, Mono<OidcIdAuthenticationToken>> {

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
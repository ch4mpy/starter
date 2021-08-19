package com.c4_soft.commons.security;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.ReactiveJwt2GrantedAuthoritiesConverter;
import com.nimbusds.jose.shaded.json.JSONArray;

import reactor.core.publisher.Flux;
import reactor.util.annotation.NonNull;

@Component
@Profile({ "!keycloak" })
public class Auth0Jwt2GrantedAuthoritiesConverter implements ReactiveJwt2GrantedAuthoritiesConverter {

	@Override
	@NonNull
	public Flux<GrantedAuthority> convert(Jwt jwt) {
		final var roles = Optional.ofNullable((JSONArray) jwt.getClaims().get("https://manage.auth0.com/roles")).orElse(new JSONArray());

		return Flux.fromStream(roles.stream().map(Object::toString).map(SimpleGrantedAuthority::new));
	}

}
package com.c4_soft.commons.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.ReactiveJwt2GrantedAuthoritiesConverter;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

import reactor.core.publisher.Flux;
import reactor.util.annotation.NonNull;

@Component
@Profile({ "keycloak" })
public class KeycloakJwt2GrantedAuthoritiesConverter implements ReactiveJwt2GrantedAuthoritiesConverter {

	@Value("${com.c4-soft.security.keycloak.client-id}")
	String clientId;

	@Override
	@NonNull
	public Flux<GrantedAuthority> convert(Jwt jwt) {
		final var roles =
				Optional
						.ofNullable((JSONObject) jwt.getClaims().get("resource_access"))
						.flatMap(resourceAccess -> Optional.ofNullable((JSONObject) resourceAccess.get(clientId)))
						.flatMap(clientResourceAccess -> Optional.ofNullable((JSONArray) clientResourceAccess.get("roles")))
						.orElse(new JSONArray());

		return Flux.fromStream(roles.stream().map(Object::toString).map(SimpleGrantedAuthority::new));
	}

}
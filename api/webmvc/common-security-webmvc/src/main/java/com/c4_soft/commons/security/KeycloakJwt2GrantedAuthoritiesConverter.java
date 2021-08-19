package com.c4_soft.commons.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.SynchronizedJwt2GrantedAuthoritiesConverter;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

@Component
@Profile({ "keycloak" })
public class KeycloakJwt2GrantedAuthoritiesConverter implements SynchronizedJwt2GrantedAuthoritiesConverter {

	@Value("${com.c4-soft.security.keycloak.client-id}")
	String clientId;

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		final var roles =
				Optional
						.ofNullable((JSONObject) jwt.getClaims().get("resource_access"))
						.flatMap(resourceAccess -> Optional.ofNullable((JSONObject) resourceAccess.get(clientId)))
						.flatMap(clientResourceAccess -> Optional.ofNullable((JSONArray) clientResourceAccess.get("roles")))
						.orElse(new JSONArray());

		return roles.stream().map(Object::toString).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

}
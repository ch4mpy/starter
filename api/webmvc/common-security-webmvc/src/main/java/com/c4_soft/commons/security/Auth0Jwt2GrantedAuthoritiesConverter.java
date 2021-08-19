package com.c4_soft.commons.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.SynchronizedJwt2GrantedAuthoritiesConverter;
import com.nimbusds.jose.shaded.json.JSONArray;

@Component
@Profile({ "!keycloak" })
public class Auth0Jwt2GrantedAuthoritiesConverter implements SynchronizedJwt2GrantedAuthoritiesConverter {

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		final var roles = Optional.ofNullable((JSONArray) jwt.getClaims().get("https://manage.auth0.com/roles")).orElse(new JSONArray());

		return roles.stream().map(Object::toString).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

}
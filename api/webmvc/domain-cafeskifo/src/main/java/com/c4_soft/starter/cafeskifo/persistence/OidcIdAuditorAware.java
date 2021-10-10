package com.c4_soft.starter.cafeskifo.persistence;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.oidc.OidcAuthentication;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcToken;

@Component
public class OidcIdAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional
				.ofNullable((OidcAuthentication) SecurityContextHolder.getContext().getAuthentication())
				.filter(OidcAuthentication::isAuthenticated)
				.map(OidcAuthentication::getPrincipal)
				.map(OidcToken::getSubject);
	}

}

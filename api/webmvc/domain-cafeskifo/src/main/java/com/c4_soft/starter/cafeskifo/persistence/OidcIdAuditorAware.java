package com.c4_soft.starter.cafeskifo.persistence;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.c4_soft.springaddons.security.oauth2.oidc.OidcId;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcIdAuthenticationToken;

@Component
@TypeHint(types = EnversRevisionRepositoryFactoryBean.class)
public class OidcIdAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional
				.ofNullable((OidcIdAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
				.filter(OidcIdAuthenticationToken::isAuthenticated)
				.map(OidcIdAuthenticationToken::getPrincipal)
				.map(OidcId::getSubject);
	}

}

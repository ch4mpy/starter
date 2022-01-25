package com.c4_soft.starter.proxies.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.c4_soft.springaddons.security.oauth2.SynchronizedJwt2OidcTokenConverter;
import com.c4_soft.springaddons.security.oauth2.config.ServletSecurityBeans;
import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcToken;

@Configuration
public class ServletSecurityBeansOverrides extends ServletSecurityBeans {
	ServletSecurityBeansOverrides(
			@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
			SpringAddonsSecurityProperties securityProperties) {
		super(issuerUri, securityProperties);
	}

	// token converter override
	@Override
	@Bean
	public SynchronizedJwt2OidcTokenConverter<OidcToken> tokenConverter() {
		return (var jwt) -> new CustomOidcToken(jwt.getClaims());
	}
}

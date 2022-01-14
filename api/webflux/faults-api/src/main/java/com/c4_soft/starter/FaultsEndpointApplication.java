package com.c4_soft.starter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import com.c4_soft.commons.web.CommonExceptionHandlers;
import com.c4_soft.lifix.common.storage.FileSystemStorageService;
import com.c4_soft.springaddons.security.oauth2.ReactiveJwt2AuthenticationConverter;
import com.c4_soft.springaddons.security.oauth2.config.OidcReactiveApiSecurityConfig;
import com.c4_soft.springaddons.security.oauth2.config.ReactiveSecurityBeans;
import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;
import com.c4_soft.starter.lifix.persistence.R2dbcConfig;

@SpringBootApplication(
		scanBasePackageClasses = { FaultsEndpointApplication.class, FileSystemStorageService.class, CommonExceptionHandlers.class, R2dbcConfig.class })

public class FaultsEndpointApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(FaultsEndpointApplication.class).web(WebApplicationType.REACTIVE).run(args);
	}

	@EnableWebFluxSecurity
	@EnableReactiveMethodSecurity
	@Import({ SpringAddonsSecurityProperties.class, ReactiveSecurityBeans.class })
	public static class WebSecurityConfig extends OidcReactiveApiSecurityConfig {
		public WebSecurityConfig(
				ReactiveJwt2AuthenticationConverter<? extends AbstractAuthenticationToken> authenticationConverter,
				SpringAddonsSecurityProperties securityProperties) {
			super(authenticationConverter, securityProperties);
		}
	}
}

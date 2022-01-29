package com.c4_soft.starter.households;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.springaddons.security.oauth2.SynchronizedJwt2AuthenticationConverter;
import com.c4_soft.springaddons.security.oauth2.config.OidcServletApiSecurityConfig;
import com.c4_soft.springaddons.security.oauth2.config.ServletSecurityBeans;
import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;
import com.c4_soft.starter.households.web.HouseholdsController;
import com.c4_soft.starter.trashbins.domain.Household;
import com.c4_soft.starter.trashbins.domain.HouseholdType;
import com.c4_soft.starter.trashbins.domain.Taxpayer;
import com.c4_soft.starter.trashbins.persistence.HouseholdRepo;
import com.c4_soft.starter.trashbins.persistence.HouseholdTypeRepo;

@SpringBootApplication(scanBasePackageClasses = { HouseholdsEndpointApplication.class, HouseholdsController.class, CommonResponseEntityExceptionHandler.class })
@EnableJpaRepositories(basePackageClasses = { HouseholdRepo.class, HouseholdTypeRepo.class })
@EntityScan(basePackageClasses = { Taxpayer.class, Household.class, HouseholdType.class })
@EnableTransactionManagement
public class HouseholdsEndpointApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(HouseholdsEndpointApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@Import({ SpringAddonsSecurityProperties.class, ServletSecurityBeans.class })
	public static class WebSecurityConfig extends OidcServletApiSecurityConfig {
		public WebSecurityConfig(
				SynchronizedJwt2AuthenticationConverter<? extends AbstractAuthenticationToken> authenticationConverter,
				SpringAddonsSecurityProperties securityProperties,
				@Value("${server.ssl.enabled:false}") boolean isSslEnabled) {
			super(authenticationConverter, securityProperties, isSslEnabled);
		}
	}

}

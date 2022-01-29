package com.c4_soft.starter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.springaddons.security.oauth2.SynchronizedJwt2AuthenticationConverter;
import com.c4_soft.springaddons.security.oauth2.config.OidcServletApiSecurityConfig;
import com.c4_soft.springaddons.security.oauth2.config.ServletSecurityBeans;
import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;
import com.c4_soft.starter.cafeskifo.domain.Order;
import com.c4_soft.starter.cafeskifo.persistence.UnsecuredOrderRepository;

@SpringBootApplication(scanBasePackageClasses = { OrdersEndpointApplication.class, CommonResponseEntityExceptionHandler.class })
@EntityScan(basePackageClasses = { Order.class })
@EnableEnversRepositories(basePackageClasses = { UnsecuredOrderRepository.class })
@EnableTransactionManagement
public class OrdersEndpointApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(OrdersEndpointApplication.class).web(WebApplicationType.SERVLET).run(args);
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

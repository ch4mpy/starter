package com.c4_soft.commons.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!disable-security")
public class CorsConfig {
	@Autowired
	SecurityProperties securityProperties;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				for (final var p : securityProperties.getCors().getPath()) {
					registry
							.addMapping(p)
							.allowedOrigins(securityProperties.getCors().getAllowedOrigins())
							.allowedMethods("*")
							.exposedHeaders("Origin", "Accept", "Content-Type", "Location");
				}
			}

		};
	}
}
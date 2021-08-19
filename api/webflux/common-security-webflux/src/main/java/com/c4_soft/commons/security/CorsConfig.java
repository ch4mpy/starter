package com.c4_soft.commons.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@Profile("!disable-security")
public class CorsConfig {
	@Value("${com.c4-soft.security.cors-path}")
	String corsPath;

	@Bean
	public CorsConfigurationSource getCorsConfiguration() {
		final var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(corsPath, corsConfiguration());
		return source;
	}

	private CorsConfiguration corsConfiguration() {
		final var corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedHeaders(List.of("Origin", "Accept", "Content-Type", "Location"));
		corsConfiguration
				.setAllowedOrigins(
						List
								.of(
										"http://localhost",
										"https://localhost",
										"https://localhost:8100",
										"https://localhost:4200",
										"http://localhost:8080",
										"https://localhost:8443",
										"https://bravo-ch4mp:8100",
										"https://bravo-ch4mp:4200",
										"http://bravo-ch4mp:8080",
										"https://bravo-ch4mp:8443"));
		corsConfiguration.setAllowedMethods(List.of("*"));
		return corsConfiguration;
	}
}
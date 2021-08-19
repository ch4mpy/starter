package com.c4_soft.commons.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!disable-security")
public class CorsConfig {
	@Value("${com.c4-soft.security.cors-path}")
	String corsPath;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
						.addMapping(corsPath)
						.allowedOrigins(
								"http://localhost",
								"https://localhost",
								"https://localhost:8100",
								"https://localhost:4200",
								"http://localhost:8080",
								"https://localhost:8443",
								"https://bravo-ch4mp:8100",
								"https://bravo-ch4mp:4200",
								"http://bravo-ch4mp:8080",
								"https://bravo-ch4mp:8443")
						.allowedMethods("*")
						.exposedHeaders("Origin", "Accept", "Content-Type", "Location");
			}

		};
	}
}
package com.c4_soft.commons.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.c4-soft.starter")
public class SecurityProperties {
	private KeycloakProperties keycloak;
	private CorsProperties cors;

	@Data
	public static class KeycloakProperties {
		private String clientId;
	}

	@Data
	public static class CorsProperties {
		private String[] path;
		private String[] allowedOrigins;
	}

}
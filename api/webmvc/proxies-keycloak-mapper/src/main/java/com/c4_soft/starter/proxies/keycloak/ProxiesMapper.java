package com.c4_soft.starter.proxies.keycloak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.c4_soft.starter.proxies.web.dto.UserProxiesDto;

public class ProxiesMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {
	private static final String PROVIDER_ID = "c4-soft.com";
	private static final String PROXIES_SERVICE_BASE_URI = "proxies-service.base-uri";
	private static Logger logger = Logger.getLogger(ProxiesMapper.class);

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
	static {
		ProviderConfigProperty property;
		property = new ProviderConfigProperty();
		property.setName(PROXIES_SERVICE_BASE_URI);
		property.setLabel("Proxies service base URI");
		property.setHelpText("Base URI for REST service to fetch proxies from");
		property.setType(ProviderConfigProperty.STRING_TYPE);
		property.setDefaultValue("https://localhost:4204");
		configProperties.add(property);
	}

	private final Map<String, WebClient> webClientByBaseUri = new HashMap<>();

	@Override
	public String getDisplayCategory() {
		return TOKEN_MAPPER_CATEGORY;
	}

	@Override
	public String getDisplayType() {
		return "User proxies mapper";
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getHelpText() {
		return "Adds a \"proxies\" private claim containing a map of authorizations the user has to act on behalf of other users (one collection of grant IDs per user subject)";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return configProperties;
	}

	@Override
	public AccessToken transformAccessToken(
			AccessToken token,
			ProtocolMapperModel mappingModel,
			KeycloakSession keycloakSession,
			UserSessionModel userSession,
			ClientSessionContext clientSessionCtx) {
		Map<String, Collection<Long>> grantsByProxiedUserSubject;
		try {
			grantsByProxiedUserSubject =
					getWebClient(mappingModel)
							.get()
							.uri("/users/{userSubject}/proxies", token.getSubject())
							.retrieve()
							.bodyToMono(UserProxiesDto.class)
							.map(UserProxiesDto::getGrantsByProxiedUserSubject)
							.block();
		} catch (final WebClientResponseException e) {
			grantsByProxiedUserSubject = null;
			logger.warn("Failed to fetch user proxies", e);
		}

		token.getOtherClaims().put("proxies", grantsByProxiedUserSubject);
		setClaim(token, mappingModel, userSession, keycloakSession, clientSessionCtx);
		return token;
	}

	public static ProtocolMapperModel create() {
		final var mapper = new ProtocolMapperModel();
		mapper.setProtocolMapper(PROVIDER_ID);
		mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
		final Map<String, String> config = new HashMap<>();
		config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
		config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
		config.put(OIDCAttributeMapperHelper.INCLUDE_IN_USERINFO, "true");
		mapper.setConfig(config);
		return mapper;
	}

	private WebClient getWebClient(ProtocolMapperModel mappingModel) {
		final var baseUri = mappingModel.getConfig().get(PROXIES_SERVICE_BASE_URI);
		return webClientByBaseUri.computeIfAbsent(baseUri, (String k) -> WebClient.builder().baseUrl(baseUri).build());
	}
}

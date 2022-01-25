package com.c4_soft.starter.proxies.web.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement
@Data
@NoArgsConstructor
public class UserProxiesDto implements Serializable {
	private static final long serialVersionUID = -8098134003304215148L;

	@NotNull
	private Map<String, Collection<Long>> grantsByProxiedUserSubject;

	public UserProxiesDto(Collection<ProxyDto> proxies) {
		this.grantsByProxiedUserSubject = proxies.stream().collect(Collectors.toMap(ProxyDto::getProxiedUserSubject, ProxyDto::getGrantIds));
	}
}

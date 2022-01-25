package com.c4_soft.starter.proxies.web.dto;

import java.io.Serializable;
import java.util.Collection;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProxyDto implements Serializable {
	private static final long serialVersionUID = -1189418955241458485L;

	@NotNull
	@NotEmpty
	private String proxiedUserSubject;

	@NotNull
	private Collection<Long> grantIds;

}

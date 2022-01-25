package com.c4_soft.starter.proxies.web.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrantDto implements Serializable {
	private static final long serialVersionUID = 6354784951854479047L;

	@NotNull
	private Long id;

	@NotNull
	@NotEmpty
	private String label;
}

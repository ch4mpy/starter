package com.c4_soft.starter.web.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement
public class TaxpayerDto implements Serializable {
	private static final long serialVersionUID = -9190240608413674081L;

	private long id;
	private String name;
}

package com.c4_soft.starter.households.web.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement
@Relation(collectionRelation = "taxpayers")
public class TaxpayerDto implements Serializable {
	private static final long serialVersionUID = -9190240608413674081L;

	private long id;
	private String name;
}

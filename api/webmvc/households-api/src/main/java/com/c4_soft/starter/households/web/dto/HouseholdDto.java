package com.c4_soft.starter.households.web.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.server.core.Relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement
@Relation(collectionRelation = "households")
@AllArgsConstructor
@Builder
public class HouseholdDto implements Serializable {
	private static final long serialVersionUID = -5043590484600129827L;

	private long id;
	private String label;
	private String type;
	private TaxpayerDto taxpayer;
}

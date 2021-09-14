package com.c4_soft.starter.web.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement
@Relation(collectionRelation = "householdTypes")
public class HouseholdTypeDto implements Serializable {
	private static final long serialVersionUID = 7245913349921278512L;

	private long id;
	private String label;

}

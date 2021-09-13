package com.c4_soft.starter.web.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement
public class HouseholdTypeDto implements Serializable {
	private static final long serialVersionUID = 7245913349921278512L;

	private long id;
	private String label;

}

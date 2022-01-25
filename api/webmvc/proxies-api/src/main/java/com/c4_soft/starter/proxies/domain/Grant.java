package com.c4_soft.starter.proxies.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_grant")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grant {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String label;
}

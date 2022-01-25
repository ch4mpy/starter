package com.c4_soft.starter.proxies.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_proxy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proxy {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private User proxiedUser;

	@NotNull
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Grant> grants;

}

package com.c4_soft.starter.cafeskifo.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "orders")
@Audited
@Data
@AllArgsConstructor
public class Order {
	@Id
	@GeneratedValue
	private Long id;

	@CreatedBy
	@LastModifiedBy
	@Column(nullable = false)
	private final String userSubject;

	@Column(nullable = false)
	private String drink;

	@Column(name = "tbl")
	private String table;

	@CreatedDate
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private final Date timestamp;

	@Version
	Long version;

	public Order(String userSubject, String drink, String table) {
		this(null, userSubject, drink, table, new Date(), null);
	}

	public Order(String userSubject, String drink) {
		this(userSubject, drink, null);
	}

	protected Order() {
		this(null, null, null);
	}
}

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
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "orders")
@Audited
@Data
@AllArgsConstructor
@Builder
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
	private final Date timestamp = new Date();

	@Version
	Long version;
}

package com.c4_soft.starter.web;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OrderCreationRequestDto {
	@NotEmpty
	private String drink;

	private String table;
}

package com.c4_soft.starter.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OrderResponseDto {
	public final Long id;
	public final String drink;
	public final String owner;
	public final String table;
	public final long createdOn;

	public OrderResponseDto(Long id, String drink, String owner, long createdOn) {
		this(id, drink, owner, null, createdOn);
	}

}

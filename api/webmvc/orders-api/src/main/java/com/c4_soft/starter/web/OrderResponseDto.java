package com.c4_soft.starter.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {
	public Long id;
	public String drink;
	public String owner;
	public String table;
	public long createdOn;

}

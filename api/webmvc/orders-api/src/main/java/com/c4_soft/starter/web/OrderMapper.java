package com.c4_soft.starter.web;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import com.c4_soft.starter.cafeskifo.domain.Order;

@Mapper(componentModel = ComponentModel.SPRING)
public interface OrderMapper {

	@Mapping(target = "createdOn", source = "timestamp.time")
	@Mapping(target = "owner", source = "userSubject")
	OrderResponseDto toDto(Order domain);

}

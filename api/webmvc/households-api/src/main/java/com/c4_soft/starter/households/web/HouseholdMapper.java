package com.c4_soft.starter.households.web;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import com.c4_soft.starter.households.web.dto.HouseholdDto;
import com.c4_soft.starter.households.web.dto.HouseholdTypeDto;
import com.c4_soft.starter.trashbins.domain.Household;
import com.c4_soft.starter.trashbins.domain.HouseholdType;

@Mapper(componentModel = ComponentModel.SPRING)
public interface HouseholdMapper {

	HouseholdTypeDto toDto(HouseholdType domain);

	HouseholdDto toDto(Household domain);

	default String map(HouseholdType type) {
		return type.getLabel();
	}
}

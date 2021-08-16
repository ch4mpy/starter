package com.c4_soft.starter.web;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.commons.web.ResourceNotFoundException;
import com.c4_soft.starter.persistence.household.HouseholdRepo;
import com.c4_soft.starter.persistence.household.HouseholdTypeRepo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/households")
@RequiredArgsConstructor
public class HouseholdController {

	private final HouseholdTypeRepo householdTypeRepo;

	private final HouseholdRepo householdRepo;

	private final ModelMapper modelMapper = new ModelMapper();

	@GetMapping("/types")
	public
			Collection<
					HouseholdTypeDto>
			getAllTypes() {
		final var householdTypes = householdTypeRepo.findAll();
		return StreamSupport
				.stream(householdTypes.spliterator(), false)
				.map(entity -> modelMapper.map(entity, HouseholdTypeDto.class))
				.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('CITIZEN_VIEW')")
	public HouseholdDto getById(@PathVariable long id) {
		final var household = householdRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No household with ID: " + id));
		return modelMapper.map(household, HouseholdDto.class);
	}

	/**
	 * @param  pageable
	 * @param  householdLabel
	 * @param  taxpayerNameOrId
	 * @param  householdType
	 * @return                  a page of matching Households
	 */
	@GetMapping()
	@PreAuthorize("hasAuthority('CITIZEN_VIEW')")
	public
			Page<
					HouseholdDto>
			getPage(
					Pageable pageable,
					@RequestParam(required = false, defaultValue = "") String householdLabel,
					@RequestParam(required = false, defaultValue = "") String taxpayerNameOrId,
					@RequestParam(required = false, defaultValue = "") String householdTypeLabel) {

		final var householdType = householdTypeRepo.findByLabelIgnoreCase(householdTypeLabel).orElse(null);
		final var page = householdRepo.findAll(HouseholdRepo.searchSpec(householdLabel, taxpayerNameOrId, householdType), pageable);
		return page.map(household -> modelMapper.map(household, HouseholdDto.class));

	}
}

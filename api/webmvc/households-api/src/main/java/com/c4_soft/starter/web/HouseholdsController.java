package com.c4_soft.starter.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.commons.web.ResourceNotFoundException;
import com.c4_soft.starter.trashbins.domain.Household;
import com.c4_soft.starter.trashbins.persistence.HouseholdRepo;
import com.c4_soft.starter.trashbins.persistence.HouseholdTypeRepo;
import com.c4_soft.starter.web.dto.HouseholdDto;
import com.c4_soft.starter.web.dto.HouseholdTypeDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/households", produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
@RequiredArgsConstructor
@Transactional
public class HouseholdsController {
	private final HouseholdTypeRepo householdTypeRepo;

	private final HouseholdRepo householdRepo;

	private final ModelMapper modelMapper = new ModelMapper();

	private final PagedResourcesAssembler<HouseholdDto> pagedHouseholdsAssembler;

	@GetMapping(path = "/types", produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public CollectionModel<HouseholdTypeDto> getAllTypes() {
		final var householdTypes = householdTypeRepo.findAll();
		final var dtos =
				StreamSupport
						.stream(householdTypes.spliterator(), false)
						.map(entity -> modelMapper.map(entity, HouseholdTypeDto.class))
						.collect(Collectors.toList());
		return CollectionModel.of(dtos).add(linkTo(methodOn(this.getClass()).getAllTypes()).withSelfRel());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('CITIZEN_VIEW')")
	public EntityModel<HouseholdDto> getById(@PathVariable @NotNull Long id) {
		final var household = householdRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No household with ID: " + id));
		return EntityModel.of(modelMapper.map(household, HouseholdDto.class));
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
	public PagedModel<EntityModel<HouseholdDto>> getPage(
			Pageable pageable,
			@RequestParam(required = false, defaultValue = "") String householdLabel,
			@RequestParam(required = false, defaultValue = "") String taxpayerNameOrId,
			@RequestParam(required = false, defaultValue = "") String householdTypeLabel) {

		final var householdType = householdTypeRepo.findByLabelIgnoreCase(householdTypeLabel).orElse(null);
		final var households = householdRepo.findAll(HouseholdRepo.searchSpec(householdLabel, taxpayerNameOrId, householdType), pageable);

		return pagedHouseholdsAssembler
				.toModel(
						households.map(household -> modelMapper.map(household, HouseholdDto.class)),
						linkTo(methodOn(HouseholdsController.class).getPage(pageable, householdLabel, taxpayerNameOrId, householdTypeLabel)).withSelfRel());

	}

	@Component
	static class HouseholdAssmebler extends RepresentationModelAssemblerSupport<Household, EntityModel<HouseholdDto>> {

		private final ModelMapper modelMapper = new ModelMapper();

		@SuppressWarnings("unchecked")
		public HouseholdAssmebler() {
			super(HouseholdsController.class, (Class<EntityModel<HouseholdDto>>) EntityModel.of(new HouseholdDto()).getClass());
		}

		@Override
		public EntityModel<HouseholdDto> toModel(Household entity) {
			return EntityModel
					.of(modelMapper.map(entity, HouseholdDto.class))
					.add(linkTo(methodOn(HouseholdsController.class).getById(entity.getId())).withSelfRel())
					.add(linkTo(methodOn(HouseholdsController.class).getPage(null, null, null, null)).withRel("page"));
		}

	}
}

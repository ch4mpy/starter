package com.c4_soft.starter.web;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.starter.persistence.HouseholdRepo;
import com.c4_soft.starter.persistence.HouseholdTypeRepo;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequestMapping("/starter")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdTypeRepo householdTypeRepo;

    private final HouseholdRepo householdRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/types")
    @PreAuthorize("isAuthenticated()")
    public Collection<HouseholdTypeDto> getAllTypes() {
        val householdTypes = householdTypeRepo.findAll();
        return StreamSupport.stream(householdTypes.spliterator(), false)
                .map(entity -> modelMapper.map(entity, HouseholdTypeDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CITIZEN_VIEW')")
    public HouseholdDto getById(@PathVariable long id) {
        val household = householdRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(household, HouseholdDto.class);
    }

    /**
     *
     * @param pageable
     * @param householdLabel
     * @param taxpayerNameOrId
     * @param householdType
     * @return a page of matching Households
     */
    @GetMapping()
    @PreAuthorize("hasAuthority('CITIZEN_VIEW')")
    public Page<HouseholdDto> getPage(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String householdLabel,
            @RequestParam(required = false, defaultValue = "") String taxpayerNameOrId,
            @RequestParam(required = false, defaultValue = "") String householdTypeLabel) {

        val householdType = householdTypeRepo.findByLabelIgnoreCase(householdTypeLabel).orElse(null);
        val page = householdRepo.findAll(HouseholdRepo.searchSpec(householdLabel, taxpayerNameOrId, householdType), pageable);
        return page.map(household -> modelMapper.map(household, HouseholdDto.class));

    }
}

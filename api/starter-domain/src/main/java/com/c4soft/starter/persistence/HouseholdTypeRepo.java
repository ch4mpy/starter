package com.c4soft.starter.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.c4soft.starter.domain.HouseholdType;

public interface HouseholdTypeRepo extends CrudRepository<HouseholdType, Long> {

    Optional<HouseholdType> findByLabelIgnoreCase(String label);
}

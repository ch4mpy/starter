package com.c4_soft.starter.persistence.household;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.domain.household.HouseholdType;

@Repository
public interface HouseholdTypeRepo extends CrudRepository<HouseholdType, Long> {

    Optional<HouseholdType> findByLabelIgnoreCase(String label);
}

package com.c4_soft.starter.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.domain.HouseholdType;

@Repository
public interface HouseholdTypeRepo extends CrudRepository<HouseholdType, Long> {

    Optional<HouseholdType> findByLabelIgnoreCase(String label);
}

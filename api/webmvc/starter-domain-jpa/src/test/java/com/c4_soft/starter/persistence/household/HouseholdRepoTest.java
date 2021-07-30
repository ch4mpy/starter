package com.c4_soft.starter.persistence.household;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.c4_soft.starter.domain.household.HouseholdType;

@SpringBootTest
class HouseholdRepoTest {

    @Autowired
    // @see src/test/resources/import.sql for test fixtures
    HouseholdRepo repo;

    @Test
    void whenAllFilterCriteriasAreEmptyThenSearchSpecIsNull() {
        assertNull(HouseholdRepo.searchSpec(null, null, null));
    }

    @Test
    void whenHouseholdLabelIsProvidedThenCaseInsensitiveSearchIsPerformed() {
        final var actual = repo.findAll(HouseholdRepo.searchSpec("pat", null, null));
        assertEquals(1, actual.size());
        assertEquals("Famille Pat Redway", actual.get(0).getLabel());
    }

    @Test
    void whenTaxpayerIdIsProvidedThenSearchIsPerformed() {
        final var actual = repo.findAll(HouseholdRepo.searchSpec(null, "4", null));
        assertEquals(1, actual.size());
        assertEquals("Lara Masset", actual.get(0).getLabel());
    }

    @Test
    void whenTaxpayerNameIsProvidedThenCaseInsensitiveSearchIsPerformed() {
        final var actual = repo.findAll(HouseholdRepo.searchSpec(null, "honnet", null));
        assertEquals(2, actual.size());
        assertEquals("Camille Honnette", actual.get(0).getTaxpayer().getName());
        assertEquals("Camille Honnette", actual.get(1).getTaxpayer().getName());
    }

    @Test
    void whenTaxpayerIdIsProvidedThenCaseInsensitiveSearchIsPerformed() {
        final var actual = repo.findAll(HouseholdRepo.searchSpec(null, null, new HouseholdType(2L, "appartement")));
        assertEquals(3, actual.size());
    }

    @Test
    void whenHouseholdLabelAndTaxpayerNameAndTaxpayerIdAreProvidedThenOnlyHousheholdsMatchingAllCriteriaAreReturned() {
        assertEquals(0, repo.findAll(HouseholdRepo.searchSpec("pat", "honnet", new HouseholdType(2L, "appartement"))).size());

        assertEquals(1, repo.findAll(HouseholdRepo.searchSpec("assoc", "menfroi", new HouseholdType(3L, "association"))).size());
    }

}
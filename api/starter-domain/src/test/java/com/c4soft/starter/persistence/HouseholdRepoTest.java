package com.c4soft.starter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.c4soft.starter.domain.HouseholdType;

import lombok.val;

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
        val actual = repo.findAll(HouseholdRepo.searchSpec("pat", null, null));
        assertEquals(1, actual.size());
        assertEquals("Famille Pat Redway", actual.get(0).getLabel());
    }

    @Test
    void whenTaxpayerIdIsProvidedThenSearchIsPerformed() {
        val actual = repo.findAll(HouseholdRepo.searchSpec(null, "4", null));
        assertEquals(1, actual.size());
        assertEquals("Lara Masset", actual.get(0).getLabel());
    }

    @Test
    void whenTaxpayerNameIsProvidedThenCaseInsensitiveSearchIsPerformed() {
        val actual = repo.findAll(HouseholdRepo.searchSpec(null, "honnet", null));
        assertEquals(2, actual.size());
        assertEquals("Camille Honnette", actual.get(0).getTaxpayer().getName());
        assertEquals("Camille Honnette", actual.get(1).getTaxpayer().getName());
    }

    @Test
    void whenTaxpayerIdIsProvidedThenCaseInsensitiveSearchIsPerformed() {
        val actual = repo.findAll(HouseholdRepo.searchSpec(null, null, new HouseholdType(2L, "appartement")));
        assertEquals(3, actual.size());
    }

    @Test
    void whenHouseholdLabelAndTaxpayerNameAndTaxpayerIdAreProvidedThenOnlyHousheholdsMatchingAllCriteriaAreReturned() {
        assertEquals(0, repo.findAll(HouseholdRepo.searchSpec("pat", "honnet", new HouseholdType(2L, "appartement"))).size());

        assertEquals(1, repo.findAll(HouseholdRepo.searchSpec("assoc", "menfroi", new HouseholdType(3L, "association"))).size());
    }

}

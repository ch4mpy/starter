package com.c4_soft.starter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.PlatformTransactionManager;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockOidcAuth;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.JwtTestConf;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.MockMvcSupport;
import com.c4_soft.springaddons.test.support.web.SerializationHelper;
import com.c4_soft.starter.households.web.HouseholdMapper;
import com.c4_soft.starter.households.web.HouseholdsController;
import com.c4_soft.starter.households.web.dto.HouseholdDto;
import com.c4_soft.starter.trashbins.domain.Household;
import com.c4_soft.starter.trashbins.domain.HouseholdType;
import com.c4_soft.starter.trashbins.domain.Taxpayer;
import com.c4_soft.starter.trashbins.persistence.HouseholdRepo;
import com.c4_soft.starter.trashbins.persistence.HouseholdTypeRepo;

@WebMvcTest(HouseholdsController.class)
@Import({ MockMvcSupport.class, SerializationHelper.class, JwtTestConf.class })
class HouseholdsControllerTest {

	@MockBean
	HouseholdTypeRepo householdTypeRepo;

	@MockBean
	HouseholdRepo householdRepo;

	@MockBean
	PlatformTransactionManager transactionManager;

	@MockBean
	HouseholdMapper mapper;

	@Autowired
	private MockMvcSupport mockMvc;

	@Test
	@WithMockOidcAuth("CITIZEN_VIEW")
	void whenGetHouseholdsPageWithNoFilterArgumentThenOk() throws Exception {
		when(householdRepo.findAll((Specification<Household>) any(), any(Pageable.class))).thenReturn(Page.empty());
		mockMvc.get("/households").andExpect(status().isOk());
	}

	@Test
	@WithMockOidcAuth("CITIZEN_VIEW")
	void whenGetHouseholdsPageWithNonEmptyHouseholdLabelThenOk() throws Exception {
		when(householdRepo.findAll((Specification<Household>) any(), any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/households").param("householdLabel", "machin").secure(true)).andExpect(status().isOk());
	}

	@Test
	@WithMockOidcAuth(authorities = { "CITIZEN_VIEW" })
	void whenGetHouseholdsPageWithNonEmptyTaxpayerNameThenOk() throws Exception {
		when(householdRepo.findAll((Specification<Household>) any(), any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/households").param("taxpayerNameOrId", "truc").secure(true)).andExpect(status().isOk());
	}

	@Test
	@WithMockOidcAuth("CITIZEN_VIEW")
	void whenGetHouseholdsPageWithNonEmptyTaxpayerIdThenOk() throws Exception {
		when(householdRepo.findAll((Specification<Household>) any(), any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/households").param("taxpayerNameOrId", "42").secure(true)).andExpect(status().isOk());
	}

	@Test
	@WithMockOidcAuth("CITIZEN_VIEW")
	void whenGetExistingHouseholdThenOk() throws Exception {
		final var household = new Household(42L, "Machin", new HouseholdType(), new Taxpayer());
		when(householdRepo.findById(42L)).thenReturn(Optional.of(household));
		when(mapper.toDto(household)).thenReturn(HouseholdDto.builder().id(household.getId()).build());

		mockMvc.get("/households/42").andExpect(status().isOk());
	}

	@Test
	@WithMockOidcAuth("CITIZEN_VIEW")
	void whenGetInexistantHouseholdThenNotFound() throws Exception {
		when(householdRepo.findById(51L)).thenReturn(Optional.empty());

		mockMvc.get("/households/51").andExpect(status().isNotFound());
	}

}

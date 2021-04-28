package com.c4soft.starter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockOidcId;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.MockMvcSupport;
import com.c4_soft.springaddons.test.support.web.SerializationHelper;
import com.c4soft.starter.domain.Household;
import com.c4soft.starter.domain.HouseholdType;
import com.c4soft.starter.domain.Taxpayer;
import com.c4soft.starter.persistence.HouseholdRepo;
import com.c4soft.starter.persistence.HouseholdTypeRepo;
import com.c4soft.commons.security.WebSecurityConfig;

@WebMvcTest(HouseholdController.class)
@Import({ MockMvcSupport.class, WebSecurityConfig.class, SerializationHelper.class })
class HouseholdControllerTest {

    @MockBean
    HouseholdTypeRepo householdTypeRepo;

    @MockBean
    HouseholdRepo householdRepo;

    @Autowired
    private MockMvcSupport mockMvc;

    private final RequestPostProcessor sslPostProcessor = (MockHttpServletRequest request) -> {
        request.setSecure(true);
        return request;
    };

    @Test
    @WithMockOidcId("CITIZEN_VIEW")
    void whenGetHouseholdsPageWithNoFilterArgumentThenOk() throws Exception {
        when(householdRepo.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.with(sslPostProcessor).get("/starter").andExpect(status().isOk());
    }

    @Test
    @WithMockOidcId("CITIZEN_VIEW")
    void whenGetHouseholdsPageWithNonEmptyHouseholdLabelThenOk() throws Exception {
        when(householdRepo.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.with(sslPostProcessor).get("/starter", "householdLabel", "machin").andExpect(status().isOk());
    }

    @Test
    @WithMockOidcId(authorities = { "CITIZEN_VIEW" })
    void whenGetHouseholdsPageWithNonEmptyTaxpayerNameThenOk() throws Exception {
        when(householdRepo.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.with(sslPostProcessor).get("/starter", "taxpayerNameOrId", "truc").andExpect(status().isOk());
    }

    @Test
    @WithMockOidcId("CITIZEN_VIEW")
    void whenGetHouseholdsPageWithNonEmptyTaxpayerIdThenOk() throws Exception {
        when(householdRepo.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.with(sslPostProcessor).get("/starter", "taxpayerNameOrId", "42").andExpect(status().isOk());
    }

    @Test
    @WithMockOidcId("CITIZEN_VIEW")
    void whenGetExistingHouseholdThenOk() throws Exception {
        when(householdRepo.findById(42L)).thenReturn(Optional.of(new Household(42L, "Machin", new HouseholdType(), new Taxpayer())));

        mockMvc.with(sslPostProcessor).get("/starter/42").andExpect(status().isOk());
    }

    @Test
    @WithMockOidcId("CITIZEN_VIEW")
    void whenGetInexistantHouseholdThenNotFound() throws Exception {
        when(householdRepo.findById(51L)).thenReturn(Optional.empty());

        mockMvc.with(sslPostProcessor).get("/starter/51").andExpect(status().isNotFound());
    }

}

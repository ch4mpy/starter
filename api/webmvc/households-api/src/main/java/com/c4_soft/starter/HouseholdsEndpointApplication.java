package com.c4_soft.starter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.ProxyBits;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.c4_soft.commons.security.WebSecurityConfig;
import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.starter.trashbins.domain.Household;
import com.c4_soft.starter.trashbins.persistence.HouseholdRepo;

@SpringBootApplication(scanBasePackageClasses = { HouseholdsEndpointApplication.class, WebSecurityConfig.class, CommonResponseEntityExceptionHandler.class })
@EntityScan(basePackageClasses = { Household.class })
@EnableJpaRepositories(basePackageClasses = { HouseholdRepo.class })
@EnableTransactionManagement
@AotProxyHint(targetClass = com.c4_soft.starter.web.HouseholdsController.class, proxyFeatures = ProxyBits.IS_STATIC)
public class HouseholdsEndpointApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(HouseholdsEndpointApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

}

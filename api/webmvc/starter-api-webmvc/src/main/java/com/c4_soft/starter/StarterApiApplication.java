package com.c4_soft.starter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.ProxyBits;

import com.c4_soft.commons.security.WebSecurityConfig;
import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.starter.domain.household.Household;
import com.c4_soft.starter.persistence.household.HouseholdRepo;

@SpringBootApplication(scanBasePackageClasses = { StarterApiApplication.class, WebSecurityConfig.class, CommonResponseEntityExceptionHandler.class })
@EntityScan(basePackageClasses = { Household.class })
@EnableJpaRepositories(basePackageClasses = { HouseholdRepo.class })
@AotProxyHint(targetClass=com.c4_soft.starter.web.HouseholdController.class, proxyFeatures = ProxyBits.IS_STATIC)
public class StarterApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(StarterApiApplication.class).web(WebApplicationType.SERVLET).run(args);
    }

}

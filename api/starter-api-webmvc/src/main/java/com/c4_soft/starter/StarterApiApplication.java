package com.c4_soft.starter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.c4_soft.commons.security.WebSecurityConfig;
import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.starter.domain.Household;
import com.c4_soft.starter.persistence.HouseholdRepo;

@SpringBootApplication(scanBasePackageClasses = { StarterApiApplication.class, WebSecurityConfig.class, CommonResponseEntityExceptionHandler.class })
@EntityScan(basePackageClasses = { Household.class })
@EnableJpaRepositories(basePackageClasses = { HouseholdRepo.class })
public class StarterApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(StarterApiApplication.class).web(WebApplicationType.SERVLET).run(args);
    }

}

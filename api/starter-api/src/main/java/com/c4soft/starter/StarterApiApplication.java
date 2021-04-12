package com.c4soft.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.c4soft.starter.domain.Household;
import com.c4soft.starter.persistence.HouseholdRepo;
import com.c4soft.commons.security.WebSecurityConfig;
import com.c4soft.commons.web.CommonResponseEntityExceptionHandler;

@SpringBootApplication(scanBasePackageClasses = { StarterApiApplication.class, CommonResponseEntityExceptionHandler.class, WebSecurityConfig.class })
@EntityScan(basePackageClasses = { Household.class })
@EnableJpaRepositories(basePackageClasses = { HouseholdRepo.class })
public class StarterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarterApiApplication.class, args);
    }

}

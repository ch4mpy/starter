package com.c4_soft.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.c4_soft.commons.security.WebSecurityConfig;
import com.c4_soft.commons.web.CommonExceptionHandlers;
import com.c4_soft.lifix.common.storage.FileSystemStorageService;

@SpringBootApplication(scanBasePackageClasses = {
        FaultsEndpointsApplication.class,
        FileSystemStorageService.class,
        WebSecurityConfig.class,
        CommonExceptionHandlers.class })
public class FaultsEndpointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaultsEndpointsApplication.class, args);
    }

}

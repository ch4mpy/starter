package com.c4_soft.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.AotProxyHints;
import org.springframework.nativex.hint.ProxyBits;

import com.c4_soft.commons.security.WebSecurityConfig;
import com.c4_soft.commons.web.CommonExceptionHandlers;
import com.c4_soft.lifix.common.storage.FileSystemStorageService;
import com.c4_soft.starter.lifix.persistence.R2dbcConfig;
import com.c4_soft.starter.web.FaultsController;

@SpringBootApplication(scanBasePackageClasses = {
		FaultsEndpointApplication.class,
		FileSystemStorageService.class,
		WebSecurityConfig.class,
		CommonExceptionHandlers.class,
		R2dbcConfig.class })
@AotProxyHints({ @AotProxyHint(targetClass = FaultsController.class, proxyFeatures = ProxyBits.IS_STATIC) })
public class FaultsEndpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaultsEndpointApplication.class, args);
	}

}

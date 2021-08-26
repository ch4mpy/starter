package com.c4_soft.starter;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.ProxyBits;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.c4_soft.commons.security.WebSecurityConfig;
import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.starter.cafeskifo.domain.Order;
import com.c4_soft.starter.cafeskifo.persistence.SecuredOrderRepo;

@SpringBootApplication(scanBasePackageClasses = { OrdersEndpointApplication.class, WebSecurityConfig.class, CommonResponseEntityExceptionHandler.class })
@EntityScan(basePackageClasses = { Order.class })
@EnableTransactionManagement
@EnableEnversRepositories(basePackageClasses = { SecuredOrderRepo.class })
@AotProxyHint(targetClass = com.c4_soft.starter.web.OrdersController.class, proxyFeatures = ProxyBits.IS_STATIC)
public class OrdersEndpointApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(OrdersEndpointApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

}

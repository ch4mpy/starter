package com.c4_soft.starter.proxies;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.c4_soft.commons.web.CommonResponseEntityExceptionHandler;
import com.c4_soft.starter.proxies.domain.User;
import com.c4_soft.starter.proxies.jpa.UserRepository;
import com.c4_soft.starter.proxies.web.GrantsController;

@SpringBootApplication(scanBasePackageClasses = { ProxiesApiApplication.class, GrantsController.class, CommonResponseEntityExceptionHandler.class })
@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
@EntityScan(basePackageClasses = { User.class })
@EnableTransactionManagement
public class ProxiesApiApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProxiesApiApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

}

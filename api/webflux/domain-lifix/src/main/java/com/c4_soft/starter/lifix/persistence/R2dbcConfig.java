package com.c4_soft.starter.lifix.persistence;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import com.c4_soft.starter.lifix.persistence.intervention.FaultAttachmentRepo.FaulAttachmenttWritingConverter;
import com.c4_soft.starter.lifix.persistence.intervention.FaultAttachmentRepo.FaultAttachmentReadingConverter;
import com.c4_soft.starter.lifix.persistence.intervention.FaultRepo.FaultReadingConverter;
import com.c4_soft.starter.lifix.persistence.intervention.FaultRepo.FaultWritingConverter;
import com.c4_soft.starter.lifix.persistence.user.LifixUserRepo.LifixUserReadingConverter;
import com.c4_soft.starter.lifix.persistence.user.LifixUserRepo.LifixUserWritingConverter;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {

	@Value("spring.r2dbc.url")
	private String r2dbcUrl;

	@Override
	public ConnectionFactory connectionFactory() {
		return ConnectionFactories.get(r2dbcUrl);
	}

	@Override
	protected List<Object> getCustomConverters() {
		return List
				.of(
						new FaultReadingConverter(),
						new FaultWritingConverter(),
						new FaultAttachmentReadingConverter(),
						new FaulAttachmenttWritingConverter(),
						new LifixUserReadingConverter(),
						new LifixUserWritingConverter());
	}
}
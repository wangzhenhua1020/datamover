package org.automation.datamover.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "datamover.thread.scheduler")
	public ThreadPoolTaskScheduler threadPoolExecutorFactoryBean() {
		return new ThreadPoolTaskScheduler();
	}

}

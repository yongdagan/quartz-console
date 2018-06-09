package org.quartz.console.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

	@Autowired
	@Qualifier("quartzDataSource")
	private DataSource quartzDataSource;
	
	@Autowired
	private AutowiringBeanJobFactory jobFactory;

	@Bean("quartzSchedulerFactory")
	public SchedulerFactoryBean quartzSchedulerFactory() throws IOException {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setQuartzProperties(quartzProperties());
		schedulerFactoryBean.setAutoStartup(true);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setStartupDelay(15);
		
//		schedulerFactoryBean.setTriggers(triggers);
		schedulerFactoryBean.setDataSource(quartzDataSource);
		schedulerFactoryBean.setJobFactory(jobFactory);
		return schedulerFactoryBean;
	}

	@Bean("quartzProperties")
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean("quartzScheduler")
	public Scheduler quartzScheduler(@Qualifier("quartzSchedulerFactory") SchedulerFactoryBean quartzSchedulerFactory) {
		return quartzSchedulerFactory.getScheduler();
	}

}

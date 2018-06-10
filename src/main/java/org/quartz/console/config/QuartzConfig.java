package org.quartz.console.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.console.job.TaskScannerJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
	
	@Value("${quartz.config.start-delay:15}")
	private Integer startDelay;
	@Value("${quartz.trigger.scanner.interval:10000}")
	private Long scannerInterval;

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
		schedulerFactoryBean.setStartupDelay(startDelay);
		
		schedulerFactoryBean.setTriggers(taskScannerJobTrigger().getObject());
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
	public Scheduler quartzScheduler() throws IOException {
		return quartzSchedulerFactory().getScheduler();
	}
	
	@Bean("taskScannerJob")
	public JobDetailFactoryBean taskScannerJob() {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(TaskScannerJob.class);
		factoryBean.setName("task-scanner");
		factoryBean.setGroup("quartz-console");
		factoryBean.setDurability(true);
		return factoryBean;
	}
	
	@Bean("taskScannerJobTrigger")
	public SimpleTriggerFactoryBean taskScannerJobTrigger() {
		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
		factoryBean.setJobDetail(taskScannerJob().getObject());
		factoryBean.setGroup("quartz-console");
		factoryBean.setName("task-scanner-trigger");
		factoryBean.setRepeatInterval(scannerInterval);
		return factoryBean;
	}
	
}

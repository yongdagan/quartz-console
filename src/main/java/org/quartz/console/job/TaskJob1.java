package org.quartz.console.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.console.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class TaskJob1 implements Job {

	private static final Logger logger = LoggerFactory.getLogger(TaskJob1.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String taskData = context.getJobDetail().getJobDataMap().getString(Constant.TASK_DATA_KEY);
		logger.info("task data:{}", taskData);
	}

}

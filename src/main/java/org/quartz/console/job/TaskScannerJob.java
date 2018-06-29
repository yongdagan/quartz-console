package org.quartz.console.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.console.bean.Task;
import org.quartz.console.common.BeanFactory;
import org.quartz.console.constant.Constant;
import org.quartz.console.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class TaskScannerJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(TaskScannerJob.class);

	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private BeanFactory beanFactory;
	@Autowired
	@Qualifier("quartzScheduler")
	private Scheduler quartzScheduler;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String, Object> params = new HashMap<>();
		params.put("refresh", Constant.REFRESH_Y);
		List<Task> list = taskMapper.selectList(params);
		if (list == null || list.isEmpty()) {
			logger.info("refresh list is empty");
			return;
		}
		logger.info("refresh list length={}", list.size());

		for (Task task : list) {
			try {
				JobKey jobKey = JobKey.jobKey(task.getName(), task.getOwner());
				TriggerKey triggerKey = TriggerKey.triggerKey(task.getName(), task.getOwner());
				JobDetail currentJob = quartzScheduler.getJobDetail(jobKey);

				Job job = (Job) beanFactory.getBean(task.getTargetJob());
				if (currentJob == null && Constant.STATUS_VALID.equals(task.getStatus())) {
					// 新增调度
					this.addJob(jobKey, triggerKey, task, job);
				} else if (currentJob != null && Constant.STATUS_VALID.equals(task.getStatus())) {
					// 更新调度
					this.updateJob(jobKey, triggerKey, task, job);
				} else if (currentJob != null && Constant.STATUS_INVALID.equals(task.getStatus())) {
					// 删除调度
					this.deleteJob(jobKey, triggerKey, task);
				}
				// 刷新完成
				taskMapper.refresh(task);
			} catch (SchedulerException e) {
				logger.error("refresh job error", e);
			}

		}
	}

	/**
	 * 新增任务调度
	 * 
	 * @param jobKey
	 * @param task
	 * @param job
	 * @throws SchedulerException
	 */
	private void addJob(JobKey jobKey, TriggerKey triggerKey, Task task, Job job) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(jobKey)
				.usingJobData(Constant.TASK_DATA_KEY, task.getData()).build();
		Trigger trigger;
		if (Constant.TRIGGER_TYPE_CRON.equals(task.getTriggerType())) {
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(CronScheduleBuilder.cronSchedule(task.getCornExpression())).build();
		} else {
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(task.getRepeatSeconds())).build();
		}
		quartzScheduler.scheduleJob(jobDetail, trigger);
	}
	
	/**
	 * 更新任务调度
	 * @param jobKey
	 * @param triggerKey
	 * @param task
	 * @param job
	 * @throws SchedulerException
	 */
	private void updateJob(JobKey jobKey, TriggerKey triggerKey, Task task, Job job) throws SchedulerException {
		// 更新已有JOB
		JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(jobKey)
				.usingJobData(Constant.TASK_DATA_KEY, task.getData()).storeDurably().build();
		quartzScheduler.addJob(jobDetail, true);
		Trigger trigger;
		if (Constant.TRIGGER_TYPE_CRON.equals(task.getTriggerType())) {
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(CronScheduleBuilder.cronSchedule(task.getCornExpression())).build();
		} else {
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(task.getRepeatSeconds())).build();
		}
		// 替换trigger重新调度job
		quartzScheduler.rescheduleJob(triggerKey, trigger);
	}

	/**
	 * 删除任务调度
	 * 
	 * @param jobKey
	 * @param triggerKey
	 * @param task
	 * @throws SchedulerException
	 */
	private void deleteJob(JobKey jobKey, TriggerKey triggerKey, Task task) throws SchedulerException {
		quartzScheduler.pauseTrigger(triggerKey);
		quartzScheduler.unscheduleJob(triggerKey);
		quartzScheduler.deleteJob(jobKey);
	}

}

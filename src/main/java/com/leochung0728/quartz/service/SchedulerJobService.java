package com.leochung0728.quartz.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.leochung0728.quartz.component.JobScheduleCreator;
import com.leochung0728.quartz.entity.SchedulerJobInfo;
import com.leochung0728.quartz.job.AbstractStatefulJob;
import com.leochung0728.quartz.job.AbstractStatefulJob.RegisteredClass;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class SchedulerJobService {

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private JobScheduleCreator scheduleCreator;

	public SchedulerMetaData getMetaData() throws SchedulerException {
		SchedulerMetaData metaData = scheduler.getMetaData();
		return metaData;
	}

	public List<SchedulerJobInfo> getAllJobList() throws SchedulerException {
		List<SchedulerJobInfo> jobs = new ArrayList<SchedulerJobInfo>();
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);
				List<Date> fireTimes = TriggerUtils.computeFireTimes((OperableTrigger) trigger, null, 5);
				TriggerState state = scheduler.getTriggerState(trigger.getKey());
				
				String cronExpression = "";
				if (trigger instanceof CronTrigger) {
    				CronTrigger cronTrigger = (CronTrigger) trigger;
    				cronExpression = cronTrigger.getCronExpression();
    			}
				
				RegisteredClass registeredClass = AbstractStatefulJob.getRegisteredClass(jobKey.getGroup());
				if (registeredClass == null) continue;
				
				SchedulerJobInfo job = new SchedulerJobInfo();
				job.setJobGroup(jobKey.getGroup());
				job.setJobName(jobKey.getName());
				job.setName(registeredClass.name());
				job.setCronExpression(cronExpression);
				job.setJobStatus(state.name());
				job.setDesc(jobDetail.getDescription());
				job.setDataMap(jobDetail.getJobDataMap());
				job.setPreviousFirstTime(trigger.getPreviousFireTime());
				job.setNextFireTime(trigger.getNextFireTime());
				job.setFireTimes(fireTimes);
				
				log.info("job: {}", job);
				jobs.add(job);
			}
		}
		return jobs;
	}

	public boolean deleteJob(SchedulerJobInfo jobInfo) {
		log.info("Delete job: {}", jobInfo);
		try {
			return scheduler.deleteJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
		} catch (SchedulerException e) {
			log.error("Failed to delete job", e);
			return false;
		}
	}

	public boolean pauseJob(SchedulerJobInfo jobInfo) {
		log.info("Pause job: {}", jobInfo);
		try {
			scheduler.pauseJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to pause job", e);
			return false;
		}
	}

	public boolean resumeJob(SchedulerJobInfo jobInfo) {
		log.info("Resume job: {}", jobInfo);
		try {
			scheduler.resumeJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to resume job", e);
			return false;
		}
	}

	public boolean triggerJob(SchedulerJobInfo jobInfo) {
		log.info("Trigger job: {}", jobInfo);
		try {
			scheduler.triggerJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to trigger job - {}", jobInfo.getJobName(), e);
			return false;
		}
	}

	public Date addJob(SchedulerJobInfo jobInfo) throws Exception {
		log.info("addJob job: {}", jobInfo);
		
		RegisteredClass registeredClass = AbstractStatefulJob.getRegisteredClass(jobInfo.getJobGroup());
		Assert.notNull(registeredClass, String.format("Job is not exist [%s]", jobInfo.getJobGroup()));
		
		String name = UUID.randomUUID().toString();
		JobDetail jobDetail = JobBuilder.newJob(registeredClass.getClazz())
				.withIdentity(name, jobInfo.getJobGroup())
				.withDescription(jobInfo.getDesc())
				.setJobData(jobInfo.getDataMap())
				.build();
		
		CronTrigger trigger = scheduleCreator.createCronTrigger(name, jobInfo.getJobGroup(),
				new Date(), jobInfo.getCronExpression(), CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
		return scheduler.scheduleJob(jobDetail, trigger);
	}
	
	public void updateJob(SchedulerJobInfo jobInfo) throws Exception {
		RegisteredClass registeredClass = AbstractStatefulJob.getRegisteredClass(jobInfo.getJobGroup());
		Assert.notNull(registeredClass, String.format("Job is not exist [%s]", jobInfo.getJobGroup()));
		
		JobDetail jobDetail = JobBuilder.newJob(registeredClass.getClazz())
				.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
				.withDescription(jobInfo.getDesc())
				.setJobData(jobInfo.getDataMap())
				.storeDurably(true)
				.build();
		
		CronTrigger trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), jobInfo.getJobGroup(),
				new Date(), jobInfo.getCronExpression(), CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
		scheduler.rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName(), jobInfo.getJobGroup()), trigger);
		scheduler.addJob(jobDetail, true);
	}

}
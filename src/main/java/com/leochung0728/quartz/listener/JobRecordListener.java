package com.leochung0728.quartz.listener;

import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.Trigger.TriggerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leochung0728.quartz.dao.JobHistDao;
import com.leochung0728.quartz.table.JobHist;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobRecordListener implements JobListener {

	@Autowired
	JobHistDao jobHistDao;

	public static final String LISTENER_NAME = "jobRecordListener";

	public static final String JOBHIST_ID = "jobHistId";

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		JobKey jobKey = context.getJobDetail().getKey();
		Trigger trigger = context.getTrigger();
		TriggerKey triggerKey = trigger.getKey();
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		TriggerState state = TriggerState.BLOCKED;

		String cronExpression = "";
		if (trigger instanceof CronTrigger) {
			CronTrigger cronTrigger = (CronTrigger) trigger;
			cronExpression = cronTrigger.getCronExpression();
		}

		JobHist entity = new JobHist(jobKey.getGroup(), jobKey.getName(), triggerKey.getGroup(), triggerKey.getName(), cronExpression, dataMap, state.name(), null, new Date(), null);
		entity = jobHistDao.save(entity);
		log.debug("jobToBeExecuted jobHistId = {}:", entity.getId());
		context.getJobDetail().getJobDataMap().put(JOBHIST_ID, entity.getId());
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Long jobHistId = (Long) context.getJobDetail().getJobDataMap().get(JOBHIST_ID);
		JobHist entity = jobHistDao.findById(jobHistId).get();
		TriggerState state = TriggerState.COMPLETE;

		entity.setEndTime(new Date());
		if (jobException != null) {
			entity.setMessage(jobException.getMessage());
			entity.setJobStatus(TriggerState.ERROR.name());
		} else {
			entity.setJobStatus(state.name());
		}
		entity = jobHistDao.save(entity);
	}

}

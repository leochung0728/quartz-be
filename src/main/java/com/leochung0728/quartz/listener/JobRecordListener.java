package com.leochung0728.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobRecordListener implements JobListener {

	public static final String LISTENER_NAME = "jobRecordListener";

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		String jobName = context.getJobDetail().getKey().toString();
		log.info("jobToBeExecuted");
		log.info("Job : {} is going to start...", jobName);
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		log.info("jobWasExecuted");

		String jobName = context.getJobDetail().getKey().toString();
		log.info("Job : {} is finished...", jobName);

		if (jobException != null) {
			log.info("Exception thrown by: " + jobName + " Exception: " + jobException.getMessage());
		}

	}

}

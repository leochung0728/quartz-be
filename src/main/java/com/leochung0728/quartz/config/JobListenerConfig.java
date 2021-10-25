package com.leochung0728.quartz.config;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.leochung0728.quartz.listener.JobRecordListener;

@Configuration
public class JobListenerConfig {

	@Autowired
	private Scheduler scheduler;

	@PostConstruct
	public void addListeners() throws SchedulerException {
		scheduler.getListenerManager().addJobListener(new JobRecordListener());
	}

}
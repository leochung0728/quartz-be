package com.leochung0728.quartz.job;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestJob extends AbstractStatefulJob {
	@Override
	public void executeInternal(JobExecutionContext context) {
		log.info("TestJob Start");
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap dataMap = jobDetail.getJobDataMap();

		String group = jobDetail.getKey().getGroup();
		String name = jobDetail.getKey().getName();
		log.info("(group, name) => ({}, {})", group, name);

		String p1 = StringUtils.defaultString(dataMap.getString(JOB_DETAIL_PROPERTIES[0]), "");
		String p2 = StringUtils.defaultString(dataMap.getString(JOB_DETAIL_PROPERTIES[1]), "");
		String p3 = StringUtils.defaultString(dataMap.getString(JOB_DETAIL_PROPERTIES[2]), "");
		String p4 = StringUtils.defaultString(dataMap.getString(JOB_DETAIL_PROPERTIES[3]), "");

		log.info("p1 = {}, p2 = {}, p3 = {}, p4 = {}", p1, p2, p3, p4);
		log.info("Job End ! {}", new Date().toString());

	}

}

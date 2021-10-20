package com.leochung0728.quartz.entity;

import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class SchedulerJobInfo {
	@ToString.Include
	private String jobGroup;
	@ToString.Include
	private String jobName;
	private String cronExpression;
	private String jobStatus;
	private String desc;
	private JobDataMap dataMap;
	private Date previousFirstTime;
	private Date nextFireTime;
	private List<Date> fireTimes;
}
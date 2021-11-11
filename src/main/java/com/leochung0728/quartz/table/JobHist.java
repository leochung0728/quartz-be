package com.leochung0728.quartz.table;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.quartz.JobDataMap;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leochung0728.quartz.util.ObjectMapperUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_hist")
@EntityListeners(AuditingEntityListener.class)
public class JobHist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String jobGroup;
	private String jobName;
	private String triggerGroup;
	private String triggerName;
	private String cronExpression;
	private String dataMap;
	private String jobStatus;
	private String message;
	private Date startTime;
	private Date endTime;
	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;

	public JobHist(String jobGroup, String jobName, String triggerGroup, String triggerName, String cronExpression, JobDataMap dataMap, String jobStatus,
			String message, Date startTime, Date endTime) {
		this.jobGroup = jobGroup;
		this.jobName = jobName;
		this.triggerGroup = triggerGroup;
		this.triggerName = triggerName;
		this.cronExpression = cronExpression;
		try {
			this.dataMap = ObjectMapperUtils.getJsonStr(dataMap);
		} catch (JsonProcessingException e) {
			this.dataMap = "";
			e.printStackTrace();
		}
		this.jobStatus = jobStatus;
		this.message = message;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}

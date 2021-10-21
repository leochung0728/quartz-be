package com.leochung0728.quartz.parser.web.medicalExpensesRefundCap;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leochung0728.quartz.job.AbstractStatefulJob;

public class WebMedicalExpensesRefundCapJob extends AbstractStatefulJob {
	private static final Logger LOG = LoggerFactory.getLogger(WebMedicalExpensesRefundCapJob.class);
	
	@Override
	public void executeInternal(JobExecutionContext context) {
		JobDetail jobDetail = context.getJobDetail();
		
		LOG.info("Start @{} {}", new Date(), jobDetail.getKey().toString());

		try {
			WebParser parser = new WebParser();
			List<Map<String, Object>> data = parser.getTableDate();
			
			System.out.println(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("End @{} {}", new Date(), jobDetail.getKey().toString());
	}
	
}

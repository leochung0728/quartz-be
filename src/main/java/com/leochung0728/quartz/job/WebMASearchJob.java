package com.leochung0728.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.leochung0728.quartz.dao.WebListEntryMASearchDao;
import com.leochung0728.quartz.parser.web.masearch.WebParser;
import com.leochung0728.quartz.table.WebListEntryMASearch;

public class WebMASearchJob extends AbstractStatefulJob {
	private static final Logger LOG = LoggerFactory.getLogger(WebMASearchJob.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Autowired
	WebListEntryMASearchDao webListEntryMASearchDao;
	
	@Override
	public void executeInternal(JobExecutionContext context) {
		Date startAt = new Date();
		
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		
		LOG.info("Start @{} {}.{}", SDF.format(startAt), trigger.getKey().getGroup(), trigger.getKey().getName());
		try {
			String name = dataMap.getString(JOB_DETAIL_PROPERTIES[0]);
			String kind = dataMap.getString(JOB_DETAIL_PROPERTIES[1]);
			String city = dataMap.getString(JOB_DETAIL_PROPERTIES[2]);
			LOG.info("Input parameter [name= {}, kind= {}, city= {}]", name, kind, city);
			
			WebParser parser = new WebParser(name, kind, city);
			
			LOG.info("Start search");
			boolean isLoginSucc = parser.search(10);
			LOG.info("End search");
			
			LOG.info("isLoginSucc: {}", isLoginSucc);
			if (!isLoginSucc) {
				throw new Exception("login failed!");
			}
			
			LOG.info("page info => [{} / {}]", parser.getTotalPage(), parser.getCurrentPage());
			
			List<WebListEntryMASearch> datas = parser.getListData();
			LOG.info("getListData size= {}", datas.size());
			webListEntryMASearchDao.saveAll(datas);
			
			while (parser.hasNextPage()) {
				Integer currentPage = parser.getCurrentPage();
				boolean isSucc = false;
				
				try {
					LOG.info("Start jumpToNextPage");
					isSucc = parser.jumpToNextPage();
					LOG.info("End jumpToNextPage");
				} catch (Exception e) {
					isLoginSucc = parser.search(10);
					if (!isLoginSucc) {
						throw new Exception("login failed!");
					}
					parser.jumpToPage(currentPage + 1);
				}
				
				LOG.info("jumpToNextPage Succ: {}", isSucc);
				LOG.info("page info => [{} / {}]", parser.getTotalPage(), parser.getCurrentPage());
				if (isSucc) {
					long randomSecond = Math.round(Math.random() * 4 - 2);
					Thread.sleep((4 + randomSecond) * 1000);
					datas = parser.getListData();
					LOG.info("getListData size= {}", datas.size());
					webListEntryMASearchDao.saveAll(datas);
				} else {
					LOG.error("Error @{} {}.{}: {}", new Date(), trigger.getKey().getGroup(),
							trigger.getKey().getName(), "jumpToNextPage failed !");
				}
			}
			
		} catch (Exception e) {
			LOG.error("Error : {}", e);
		}
		LOG.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
}

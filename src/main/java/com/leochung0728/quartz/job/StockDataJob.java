package com.leochung0728.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import com.leochung0728.quartz.dao.WebListEntryMASearchDao;
import com.leochung0728.quartz.parser.web.stockData.MarketType;
import com.leochung0728.quartz.parser.web.stockData.WebParser;
import com.leochung0728.quartz.table.WebListEntryMASearch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockDataJob extends AbstractStatefulJob {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	
	@Autowired
	WebParser webParser;
	
//	@Autowired
//	WebListEntryMASearchDao webListEntryMASearchDao;
	
	@Override
	public void executeInternal(JobExecutionContext context) {
		Date startAt = new Date();
		
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		
		log.info("Start @{} {}.{}", SDF.format(startAt), trigger.getKey().getGroup(), trigger.getKey().getName());
		try {
			String marketTypeCode = dataMap.getString(JOB_DETAIL_PROPERTIES[0]);
			String isinCode = dataMap.getString(JOB_DETAIL_PROPERTIES[1]);
			
			MarketType marketType = MarketType.getByCode(marketTypeCode);
			log.info("Input parameter [marketType= {}, isinCode= {}]", marketType, isinCode);
			
			webParser.setSearchParam(marketType, isinCode);
			
			log.info("Start search");
			webParser.search(3);
			log.info("End search");
			
			Document doc = Jsoup.parse(webParser.getPageSource());
			
			
//			List<WebListEntryMASearch> datas = parser.getListData();
//			log.info("getListData size= {}", datas.size());
//			webListEntryMASearchDao.saveAll(datas);
//			
//			while (parser.hasNextPage()) {
//				Integer currentPage = parser.getCurrentPage();
//				boolean isSucc = false;
//				
//				try {
//					log.info("Start jumpToNextPage");
//					isSucc = parser.jumpToNextPage();
//					log.info("End jumpToNextPage");
//				} catch (Exception e) {
//					isLoginSucc = parser.search(10);
//					if (!isLoginSucc) {
//						throw new Exception("login failed!");
//					}
//					parser.jumpToPage(currentPage + 1);
//				}
//				
//				log.info("jumpToNextPage Succ: {}", isSucc);
//				log.info("page info => [{} / {}]", parser.getTotalPage(), parser.getCurrentPage());
//				if (isSucc) {
//					long randomSecond = Math.round(Math.random() * 4 - 2);
//					Thread.sleep((4 + randomSecond) * 1000);
//					datas = parser.getListData();
//					log.info("getListData size= {}", datas.size());
//					webListEntryMASearchDao.saveAll(datas);
//				} else {
//					log.error("Error @{} {}.{}: {}", new Date(), trigger.getKey().getGroup(),
//							trigger.getKey().getName(), "jumpToNextPage failed !");
//				}
//			}
			
		} catch (Exception e) {
			log.error("Error : {}", e);
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
}

package com.leochung0728.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import com.leochung0728.quartz.parser.web.stockData.MarketType;
import com.leochung0728.quartz.parser.web.stockData.WebParser;
import com.leochung0728.quartz.service.StockService;
import com.leochung0728.quartz.table.Stock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockDataJob extends AbstractStatefulJob {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	
	@Autowired
	WebParser StockDataWebParser;
	
	@Autowired
	StockService stockService;
	
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
			
			StockDataWebParser.setSearchParam(marketType, isinCode);
			
			StockDataWebParser.getWebDriver();
			
			log.info("Start search");
			StockDataWebParser.search(3);
			log.info("End search");
			
			log.info("Start parse");
			List<Stock> stocks = StockDataWebParser.parsePageSource();
			log.info("parse stokes size: {}", stocks.size());
			log.info("End parse");
			
			log.info("Start save");
			stockService.saveAll(stocks);
			log.info("End save");
			
		} catch (Exception e) {
			log.error("Error : {}", e);
		}  finally {
			StockDataWebParser.closeWebDriver();
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
}

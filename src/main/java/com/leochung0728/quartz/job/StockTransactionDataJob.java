package com.leochung0728.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import com.leochung0728.quartz.entity.Vo;
import com.leochung0728.quartz.parser.web.stockTransactionData.WebParser;
import com.leochung0728.quartz.service.StockService;
import com.leochung0728.quartz.service.StockTransationService;
import com.leochung0728.quartz.table.Stock;
import com.leochung0728.quartz.table.StockTransaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockTransactionDataJob extends AbstractStatefulJob {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Autowired
	WebParser StockTransactionWebParser;
	
	@Autowired
	StockService stockService;
	
	@Autowired
	StockTransationService stockTransationService;
	
	@Override
	public void executeInternal(JobExecutionContext context) {
		Date startAt = new Date();
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		
		log.info("Start @{} {}.{}", SDF.format(startAt), trigger.getKey().getGroup(), trigger.getKey().getName());
		try {
			String startDateStr = dataMap.getString(JOB_DETAIL_PROPERTIES[0]);
			String endDateStr = dataMap.getString(JOB_DETAIL_PROPERTIES[1]);
			
			
			List<Stock> stocks = stockService.findByErrCountLessThanEqual(5);
			Stock stock;
			for (int idx = 0; idx <= stocks.size(); idx++) {
				stock = stocks.get(idx);
				
				log.info("[{}/{}]", idx + 1, stocks.size());
				log.info("Start stock: {}", stock.getStockCode());
				
				StockTransactionWebParser.setSearchParam(stock, startDateStr, endDateStr);
				
				log.info("Start search");
				StockTransactionWebParser.search(3);
				log.info("End search");
				
				log.info("Start parse");
				Vo<List<StockTransaction>> vo = StockTransactionWebParser.parseData();
				if (!vo.isSucc()) {
					log.warn("parseData fail:", vo.getMsg());
					stock.setErrMsg(vo.getMsg());
					stockService.increaseErrCount(stock, 5);
					continue;
				}
				List<StockTransaction> stockTransactions = vo.getData();
				log.info("parse stokes size: {}", stockTransactions.size());
				log.info("End parse");
				
				log.info("Start save");
				stockTransationService.saveAll(stockTransactions);
				log.info("End save");
			}
		} catch (Exception e) {
			log.error("Error : {}", e);
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
}

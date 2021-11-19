package com.leochung0728.quartz.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanFactory;
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
	BeanFactory beanFactory;
	
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

			List<Stock> stocks = stockService.findByErrCountLessThanEqual(0);
			List<Future<Vo<List<StockTransaction>>>> futures = new ArrayList<>();
			ExecutorService executorService = new ThreadPoolExecutor(0, 20, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>());

			for (Stock stock : stocks) {
				Future<Vo<List<StockTransaction>>> future = executorService.submit(new Worker(stock, startDateStr, endDateStr));
				futures.add(future);
			}
			
//			for (Future<Vo<List<StockTransaction>>> f : futures) {
//				f.get(0, null)
//			}

		} catch (Exception e) {
			log.error("Error : {}", e);
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
	
	class Worker implements Callable<Vo<List<StockTransaction>>> {
		Stock stock;
		String startDateStr;
		String endDateStr;
		
		public Worker(Stock stock, String startDateStr, String endDateStr) {
			this.stock = stock;
			this.startDateStr = startDateStr;
			this.endDateStr = endDateStr;
		}

		@Override
		public Vo<List<StockTransaction>> call() throws InterruptedException {
			log.info("Start stock: {}", stock.getStockCode());
			WebParser StockTransactionWebParser = (WebParser) beanFactory.getBean("StockTransactionWebParser");
			List<StockTransaction> stockTransactions = new ArrayList<StockTransaction>();
			try {
				StockTransactionWebParser.setSearchParam(stock, startDateStr, endDateStr);
				log.info("[{}] Start search", stock.getStockCode());
				StockTransactionWebParser.search(3);
				log.info("[{}] End search", stock.getStockCode());
				
				log.info("[{}] Start parse", stock.getStockCode());
				Vo<List<StockTransaction>> vo = StockTransactionWebParser.parseData();
				if (!vo.isSucc()) {
					log.warn("[{}] parseData fail: {}", stock.getStockCode(), vo.getMsg());
					return Vo.failure(vo.getMsg());
				}
				stockTransactions = vo.getData();
				log.info("[{}] parse stokes size: {}", stock.getStockCode(), stockTransactions.size());
				log.info("[{}] End parse", stock.getStockCode());
			}catch (Exception e) {
				log.error("Error : {}", e);
				return Vo.failure(e.getMessage());
			}
			log.info("End stock: {}", stock.getStockCode());
			return Vo.success(stockTransactions);
			
		}
	}
}




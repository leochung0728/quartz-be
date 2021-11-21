package com.leochung0728.quartz.job;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.collections.CollectionUtils;
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

			List<Stock> stocks = stockService.findByErrCountLessThanEqual(3);
			List<Future<Vo<Stock>>> futures = new ArrayList<>();
			ExecutorService executorService = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<>());

			ExecutorCompletionService<Vo<Stock>> completionService = new ExecutorCompletionService<>(executorService);

			for (Stock stock : stocks) {
				futures.add(completionService.submit(new Worker(stock, startDateStr, endDateStr)));
			}

			executorService.shutdown();
			int count = 0;
			for (int i = 0; i < futures.size(); i++) {
				count++;
				Vo<Stock> vo = completionService.take().get();
				log.info("[{}/{}] stock[{}] complete {}", count, futures.size(), vo.getData().getStockCode(), vo.getMsg());
				if (vo.isSucc() && vo.getData() != null) {
					if (CollectionUtils.isEmpty(vo.getData().getStockTransactions())) {
						continue;
					}
					stockService.resetErrCount(vo.getData(), 5);
					log.info("[{}]Start save, size = {}", vo.getData().getStockCode(), vo.getData().getStockTransactions().size());
					stockTransationService.saveAll(new ArrayList<>(vo.getData().getStockTransactions()));
					log.info("[{}]End save", vo.getData().getStockCode());
				} else if (!vo.isSucc()) {
					stockService.increaseErrCount(vo.getData(), 5);
				}
			}
		} catch (Exception e) {
			log.error("Error : ", e);
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
	
	class Worker implements Callable<Vo<Stock>> {
		Stock stock;
		String startDateStr;
		String endDateStr;
		
		public Worker(Stock stock, String startDateStr, String endDateStr) {
			this.stock = stock;
			this.startDateStr = startDateStr;
			this.endDateStr = endDateStr;
		}

		@Override
		public Vo<Stock> call() {
			log.info("Start stock: {}", stock.getStockCode());
			WebParser StockTransactionWebParser = (WebParser) beanFactory.getBean("StockTransactionWebParser");
			Set<StockTransaction> stockTransactions = new HashSet<>();
			stock.setStockTransactions(stockTransactions);
			try {
				StockTransactionWebParser.setSearchParam(stock, startDateStr, endDateStr);
				StockTransactionWebParser.search(3);
				log.info("[{}] End search", stock.getStockCode());
				
				log.info("[{}] Start parse", stock.getStockCode());
				Vo<List<StockTransaction>> vo = StockTransactionWebParser.parseData();
				if (!vo.isSucc()) {
					log.warn("[{}] parseData fail: {}", stock.getStockCode(), vo.getMsg());
					stock.setErrMsg(vo.getMsg());
					return Vo.failure(stock, vo.getMsg());
				}
				stockTransactions.addAll(vo.getData());
				log.info("[{}] parse stokes size: {}", stock.getStockCode(), stockTransactions.size());
				log.info("[{}] End parse", stock.getStockCode());
			}catch (Exception e) {
				log.error("Error : ", e);
				return Vo.failure(stock, e.getMessage());
			}
			log.info("End stock: {}", stock.getStockCode());
			return Vo.success(stock);
			
		}
	}
}




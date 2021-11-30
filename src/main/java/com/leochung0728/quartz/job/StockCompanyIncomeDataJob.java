package com.leochung0728.quartz.job;

import com.leochung0728.quartz.entity.Vo;
import com.leochung0728.quartz.parser.web.stockCompanyIncomeData.WebParser;
import com.leochung0728.quartz.service.StockCompanyIncomeService;
import com.leochung0728.quartz.table.StockCompanyIncome;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class StockCompanyIncomeDataJob extends AbstractStatefulJob {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM");
	
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	StockCompanyIncomeService stockCompanyIncomeService;
	
	@Override
	public void executeInternal(JobExecutionContext context) {
		Date startAt = new Date();
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		
		log.info("Start @{} {}.{}", SDF.format(startAt), trigger.getKey().getGroup(), trigger.getKey().getName());
		try {
			String startYearMonthStr = dataMap.getString(JOB_DETAIL_PROPERTIES[0]);
			String endYearMonthStr = dataMap.getString(JOB_DETAIL_PROPERTIES[1]);

			YearMonth startYearMonth = StringUtils.isNotBlank(startYearMonthStr) ? YearMonth.parse(startYearMonthStr, DTF) : YearMonth.now();
			YearMonth endYearMonth = StringUtils.isNotBlank(endYearMonthStr) ? YearMonth.parse(endYearMonthStr, DTF) : YearMonth.now();

			List<Future<Vo<List<StockCompanyIncome>>>> futures = new ArrayList<>();
			ExecutorService executorService = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<>());

			ExecutorCompletionService<Vo<List<StockCompanyIncome>>> completionService = new ExecutorCompletionService<>(executorService);

			for (YearMonth ym = startYearMonth; !ym.isAfter(endYearMonth); ym = ym.plusMonths(1)) {
				futures.add(completionService.submit(new Worker(ym.getYear(), ym.getMonthValue())));
			}
			executorService.shutdown();
			int count = 0;
			for (int i = 0; i < futures.size(); i++) {
				count++;
				Vo<List<StockCompanyIncome>> vo = completionService.take().get();
				log.info("[{}/{}] complete", count, futures.size());
				if (vo.isSucc() && CollectionUtils.isNotEmpty(vo.getData())) {
					log.info("Start save, size = {}", vo.getData().size());
					stockCompanyIncomeService.saveAll(vo.getData());
					log.info("End save");
				}
			}
		} catch (Exception e) {
			log.error("Error: ", e);
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
	
	class Worker implements Callable<Vo<List<StockCompanyIncome>>> {
		int year;
		int month;
		
		public Worker(int year, int month) {
			this.year = year;
			this.month = month;
		}

		@Override
		public Vo<List<StockCompanyIncome>> call() {
			log.info("Start year: {}, month: {}", this.year, this.month);
			WebParser parser = (WebParser) beanFactory.getBean("StockCompanyIncomeWebParser");
			List<StockCompanyIncome> result = new ArrayList<>();
			try {
				parser.setSearchParam(this.year, this.month);
				List<String> searchUrls = parser.getSearchUrl();

				for (String url : searchUrls) {
					log.info("[{}/{}] url: {}", this.year, this.month, url);
					Document document = parser.search(url);
					List<StockCompanyIncome> data = parser.parseData(document);
					log.info("[{}/{}] data size: {}", this.year, this.month, data.size());
					result.addAll(data);
				}
				log.info("[{}/{}] result size: {}", this.year, this.month, result.size());
			}catch (Exception e) {
				log.error("Error : ", e);
				return Vo.failure(result, e.getMessage());
			}
			log.info("End year: {}, month: {}", this.year, this.month);
			return Vo.success(result);
		}
	}
}




package com.leochung0728.quartz.job;

import com.leochung0728.quartz.entity.Vo;
import com.leochung0728.quartz.parser.web.stockCompanySeasonIncomeData.WebParser;
import com.leochung0728.quartz.service.StockCompanySeasonIncomeService;
import com.leochung0728.quartz.table.StockCompanySeasonIncome;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.nodes.Document;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StockCompanySeasonIncomeDataJob extends AbstractStatefulJob {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final Pattern PATTERN = Pattern.compile("^(\\d{4})/([1-4])$");

	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	StockCompanySeasonIncomeService service;
	
	@Override
	public void executeInternal(JobExecutionContext context) {
		Date startAt = new Date();
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		
		log.info("Start @{} {}.{}", SDF.format(startAt), trigger.getKey().getGroup(), trigger.getKey().getName());
		try {
			String startYearSeasonStr = dataMap.getString(JOB_DETAIL_PROPERTIES[0]);
			String endYearSeasonStr = dataMap.getString(JOB_DETAIL_PROPERTIES[1]);

			int startYear;
			int startSeason;
			int endYear;
			int endSeason;

			Matcher matcher = PATTERN.matcher(startYearSeasonStr);
			if (!matcher.find()) {
				throw new Exception(String.format("參數不符合格式 [param01= %s, pattern= %s]", startYearSeasonStr, PATTERN.pattern()));
			} else {
				startYear = Integer.parseInt(matcher.group(1));
				startSeason = Integer.parseInt(matcher.group(2));
			}

			matcher = PATTERN.matcher(endYearSeasonStr);
			if (!matcher.find()) {
				YearMonth now = YearMonth.now();
				endYear = now.getYear();
				endSeason = now.getMonthValue() / 3;
			} else {
				endYear = Integer.parseInt(matcher.group(1));
				endSeason = Integer.parseInt(matcher.group(2));
			}

			List<Future<Vo<List<StockCompanySeasonIncome>>>> futures = new ArrayList<>();
			ExecutorService executorService = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<>());

			ExecutorCompletionService<Vo<List<StockCompanySeasonIncome>>> completionService = new ExecutorCompletionService<>(executorService);

			for (int y = startYear; y <= endYear; y++) {
				for (int s = startSeason; s <= 4; s++) {
					if (y == endYear && s > endSeason) break;
					futures.add(completionService.submit(new Worker(y, s)));
				}
			}
			executorService.shutdown();
			int count = 0;
			for (int i = 0; i < futures.size(); i++) {
				count++;
				Vo<List<StockCompanySeasonIncome>> vo = completionService.take().get();
				log.info("[{}/{}] complete", count, futures.size());
				if (vo.isSucc() && CollectionUtils.isNotEmpty(vo.getData())) {
					log.info("Start save, size = {}", vo.getData().size());
					service.saveAll(vo.getData());
					log.info("End save");
				}
			}
		} catch (Exception e) {
			log.error("Error: ", e);
		}
		log.info("End @{} {}.{}", SDF.format(new Date()), trigger.getKey().getGroup(), trigger.getKey().getName());
	}
	
	class Worker implements Callable<Vo<List<StockCompanySeasonIncome>>> {
		int year;
		int season;
		
		public Worker(int year, int season) {
			this.year = year;
			this.season = season;
		}

		@Override
		public Vo<List<StockCompanySeasonIncome>> call() {
			log.info("Start year: {}, season: {}", this.year, this.season);
			WebParser parser = (WebParser) beanFactory.getBean("StockCompanySeasonIncomeWebParser");
			List<StockCompanySeasonIncome> result = new ArrayList<>();
			try {
				parser.setSearchParam(this.year, this.season);
				List<String> searchUrls = parser.getSearchUrl();

				for (String url : searchUrls) {
					log.info("[{}/{}] url: {}", this.year, this.season, url);
					Document document = parser.search(url);
					List<StockCompanySeasonIncome> data = parser.parseData(document);
					log.info("[{}/{}] data size: {}", this.year, this.season, data.size());
					result.addAll(data);
				}
				log.info("[{}/{}] result size: {}", this.year, this.season, result.size());
			}catch (Exception e) {
				log.error("Error : ", e);
				return Vo.failure(result, e.getMessage());
			}
			log.info("End year: {}, month: {}", this.year, this.season);
			return Vo.success(result);
		}
	}
}




package com.leochung0728.quartz.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public abstract class AbstractStatefulJob extends QuartzJobBean {
	public static final String[] JOB_DETAIL_PROPERTIES = { "p1", "p2", "p3", "p4" };
	static Map<String, RegisteredClass> classNameMap = new HashMap<>();

	public enum RegisteredClass {
		TestJob(TestJob.class, "測試作業"),
		WebMedicalExpensesRefundCapJob(WebMedicalExpensesRefundCapJob.class, "醫療費用核退上限"),
		WebMASearchJob(WebMASearchJob.class, "醫事查詢系統"),
		StockDataJob(StockDataJob.class, "股市資料爬蟲"),
		StockTransactionDataJob(StockTransactionDataJob.class, "股市交易資料爬蟲"),
		StockCompanyIncomeDataJob(StockCompanyIncomeDataJob.class, "股市公司收益爬蟲"),
		StockCompanySeasonIncomeDataJob(StockCompanySeasonIncomeDataJob.class, "股市公司季收益爬蟲");

		private final Class<? extends AbstractStatefulJob> clazz;
		private final String jobName;

		RegisteredClass(Class<? extends AbstractStatefulJob> clazz, String jobName) {
			this.clazz = clazz;
			this.jobName = jobName;
		}

		public Class<? extends AbstractStatefulJob> getClazz() {
			return clazz;
		}
		
		public String getJobName() {
			return jobName;
		}
	}

	static {
		for (RegisteredClass registeredClass : RegisteredClass.values()) {
			classNameMap.put(registeredClass.getClazz().getName(), registeredClass);
		}
	}

	public static RegisteredClass getRegisteredClass(String className) {
		return classNameMap.get(className);
	}

}
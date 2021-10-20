package com.leochung0728.quartz.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public abstract class AbstractStatefulJob extends QuartzJobBean {
	public static final String[] JOB_DETAIL_PROPERTIES = { "p1", "p2", "p3", "p4" };
	static Map<String, RegisteredClass> classNameMap = new HashMap<>();

	public static enum RegisteredClass {
		測試作業(TestJob.class);
//		醫療費用核退上限(WebMedicalExpensesRefundCapJob.class),
//		醫事查詢系統(WebMASearchJob.class);

		private Class<? extends AbstractStatefulJob> clazz;

		private RegisteredClass(Class<? extends AbstractStatefulJob> clazz) {
			this.clazz = clazz;
		}

		public Class<? extends AbstractStatefulJob> getClazz() {
			return clazz;
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
package com.leochung0728.quartz.parser.web.medicalExpensesRefundCap;

import org.apache.commons.lang3.StringUtils;


public enum Column {
	年月("年月", "interval"),
	門診("門診", "outpatient"),
	急診("急診", "emergency"),
	住院("住院", "hospital"),
	起始年月(null, "start_yearmonth"),
	結束年月(null, "end_yearmonth");

	private String webColName;
	private String dataColName;

	Column(String webColName, String dataColName) {
		this.webColName = webColName;
		this.dataColName = dataColName;
	}

	public String getWebColName() {
		return webColName;
	}

	public String getDataColName() {
		return dataColName;
	}

	public static Column[] getWebCols() {
		return new Column[] { 年月, 門診, 急診, 住院 };
	}
	
	public static Column getByWebColName(String webColName) {
		if (StringUtils.isBlank(webColName)) { return null; }
		for (Column col : Column.getWebCols()) {
			if (webColName.equals(col.getWebColName())) {
				return col;
			}
		}
		return null;
	}
}

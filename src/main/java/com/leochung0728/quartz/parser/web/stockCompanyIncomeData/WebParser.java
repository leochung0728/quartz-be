package com.leochung0728.quartz.parser.web.stockCompanyIncomeData;

import com.leochung0728.quartz.table.StockCompanyIncome;
import com.leochung0728.quartz.util.NumberUtils;
import com.leochung0728.quartz.util.RequestUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Getter
@Component("StockCompanyIncomeWebParser")
@Scope("prototype")
public class WebParser {
	static final String BASE_URL_V1 = "https://mops.twse.com.tw/nas/t21/{type}/t21sc03_{minguoYear}_{month}.html";
	static final String BASE_URL_V2 = "https://mops.twse.com.tw/nas/t21/{type}/t21sc03_{minguoYear}_{month}_{region}.html";

	static final String TABLE_SELECTOR_V1 = "table table";
	static final String TABLE_SELECTOR_V2 = "table table table";

	// search params
	private int year;
	private int month;

	private boolean isV1;
	private String baseUrl;
	private String tableSelector;

	public void setSearchParam(int year, int month) {
		this.year = year;
		this.month = month;
		this.init();
	}

	private void init() {
		int minguoYear = this.year - 1911;
		this.isV1 = minguoYear < 102;
		this.baseUrl = this.isV1 ? BASE_URL_V1 : BASE_URL_V2;
		this.tableSelector = this.isV1 ? TABLE_SELECTOR_V1 : TABLE_SELECTOR_V2;
	}

	public List<String> getSearchUrl() {
		List<String> searchUrls = new ArrayList<>();

		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(this.baseUrl);
		factory.setEncodingMode(EncodingMode.TEMPLATE_AND_VALUES);
		UriBuilder uriBuilder = factory.builder();

		List<Map<String, Object>> paramMaps = new ArrayList<>();

		int minguoYear = this.year - 1911;

		for (String type : new String[]{"sii", "otc"}) {
			if (this.isV1) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("type", type);
				paramMap.put("minguoYear", minguoYear);
				paramMap.put("month", month);
				paramMaps.add(paramMap);
			} else {
				for (int region : new int[]{0, 1}) {
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("type", type);
					paramMap.put("minguoYear", minguoYear);
					paramMap.put("month", month);
					paramMap.put("region", region);
					paramMaps.add(paramMap);
				}
			}
		}
		for (Map<String, Object> paramMap : paramMaps) {
			searchUrls.add(uriBuilder.build(paramMap).toString());
		}
		return searchUrls;
	}

	public Document search(String url) throws IOException {
		return RequestUtils.getDocument(url, url, Method.GET);
	}

	public Document search(String url, Integer maxTryTimes) {
		maxTryTimes = maxTryTimes == null ? 1 : maxTryTimes;
		int tryTimes = 0;

		Document document = null;
		while (tryTimes < maxTryTimes) {
			try {
				document = this.search(url);
				break;
			} catch (Exception e) {
				tryTimes++;
			}
		}
		return document;
	}

	public List<StockCompanyIncome> parseData(Document document) {
		List<StockCompanyIncome> stockCompanyIncomes = new ArrayList<>();

		Elements tables = document.select(this.tableSelector);

		for (Element table : tables) {
			Elements trs = table.select("tr");
			if (trs.size() <= 3) { // 2列標題，1列合計
				continue;
			}
			for (int idx = 2; idx < trs.size() - 1; idx++) {
				Element tr = trs.get(idx);
				try {
					Elements tds = tr.select("td");

					if (tds.size() < 10) { // 至少10行
						continue;
					}
					String cell1 = StringUtils.trim(StringEscapeUtils.unescapeHtml4(tds.get(0).text()));
//					String cell2 = StringUtils.trim(StringEscapeUtils.unescapeHtml4(tds.get(1).text()));
					String cell3 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(2).text()));
					String cell4 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(3).text()));
					String cell5 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(4).text()));
					String cell6 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(5).text()));
					String cell7 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(6).text()));
					String cell8 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(7).text()));
					String cell9 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(8).text()));
					String cell10 = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(9).text()));

					String stockCode = cell1;
//					String companyName = cell2;
					Double income = NumberUtils.parseDouble(cell3);
					Double lastMonthIncome = NumberUtils.parseDouble(cell4);
					Double lastYearIncome = NumberUtils.parseDouble(cell5);
					Double lastMonthIncreaseRatio = NumberUtils.parseDouble(cell6);
					Double lastYearIncreaseRatio = NumberUtils.parseDouble(cell7);
					Double cumulativeIncome = NumberUtils.parseDouble(cell8);
					Double lastYearCumulativeIncome = NumberUtils.parseDouble(cell9);
					Double lastYearCumulativeIncreaseRatio = NumberUtils.parseDouble(cell10);

					String remark = null;
					if (tds.size() >= 11 && !this.isV1()) {
						remark = StringUtils.trim(tds.get(10).text());
					}

					StockCompanyIncome companyIncome = new StockCompanyIncome(stockCode, this.year, this.month, income, lastMonthIncome, lastYearIncome, lastMonthIncreaseRatio, lastYearIncreaseRatio, cumulativeIncome, lastYearCumulativeIncome, lastYearCumulativeIncreaseRatio, remark);
					stockCompanyIncomes.add(companyIncome);
				} catch (Exception e) {
					log.error(String.format("[%s, %s] Parse error : tr = %s", this.year, this.month, tr), e);
				}
			}
		}
		return stockCompanyIncomes;
	}

}

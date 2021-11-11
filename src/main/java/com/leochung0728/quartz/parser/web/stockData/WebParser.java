package com.leochung0728.quartz.parser.web.stockData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.util.UriBuilder;

import com.leochung0728.quartz.table.Stock;
import com.leochung0728.quartz.table.StockIndustryType;
import com.leochung0728.quartz.table.StockIssueType;
import com.leochung0728.quartz.table.StockMarketType;

@Slf4j
@Getter
@Component
@Scope("prototype")
public class WebParser {
	@Autowired
	WebDriver webDriver;

	static final String BASE_URL = "https://isin.twse.com.tw/isin/class_main.jsp?owncode=&stockname=&isincode={isinCode}&market={marketType}&issuetype=&industry_code=&Page=1&chklike=Y";

	// search params
	private MarketType marketType = null;
	private String isinCode = null;

	private String pageSource = null;
	private String searchUrl = null;

	public void setSearchParam(MarketType marketType, String isinCode) {
		this.marketType = marketType;
		this.isinCode = isinCode;
	}

	private void setSearchUrl() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
		factory.setEncodingMode(EncodingMode.TEMPLATE_AND_VALUES);
		UriBuilder uriBuilder = factory.builder();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isinCode", this.isinCode);
		paramMap.put("marketType", this.marketType != null ? this.marketType.getCode() : this.marketType);

		this.searchUrl = uriBuilder.build(paramMap).toString();
	}

	public void search() {
		setSearchUrl();
		webDriver.get(searchUrl);
		this.pageSource = webDriver.getPageSource();
		webDriver.close();
	}

	public boolean search(Integer maxTryTimes) {
		maxTryTimes = maxTryTimes == null ? 1 : maxTryTimes;
		int tryTimes = 0;

		boolean isSucc = false;
		while (tryTimes < maxTryTimes) {
			try {
				search();
				isSucc = true;
				break;
			} catch (Exception e) {
				tryTimes++;
			}
		}
		return isSucc;
	}
	
	public List<Stock> parsePageSource() {
		List<Stock> stocks = new ArrayList<>();
		
		Document doc = Jsoup.parse(this.pageSource);
		
		Element tbody = doc.selectFirst("table tbody");
		Elements trs = tbody.select("tr");
		
		if (trs.size() < 2) return stocks;
		trs.remove(0);
		
		for (Element tr : trs) {
			Elements tds = tr.select("td");
			String isinCode = StringUtils.trimToNull(tds.get(1).text());
			String stockCode = StringUtils.trimToNull(tds.get(2).text());
			String stockName = StringUtils.trimToNull(tds.get(3).text());
			String marketTypeName = StringUtils.trimToNull(tds.get(4).text());
			String issueTypeName = StringUtils.trimToNull(tds.get(5).text());
			String industryTypeName = StringUtils.trimToNull(tds.get(6).text());
			Date releaseDate = null;
			try {
				releaseDate = DateUtils.parseDateStrictly(StringUtils.trim(tds.get(7).text()), new String[] { "yyyy/MM/dd" });
			} catch (ParseException e) {
				log.error(String.format("parse date error, isinCode = %s", isinCode), e);
			}
			String cfiCode = StringUtils.trimToNull(tds.get(6).text());
			
			StockMarketType marketType = marketTypeName != null ? new StockMarketType(marketTypeName) : null;
			StockIssueType issueType = issueTypeName != null ? new StockIssueType(issueTypeName) : null;
			StockIndustryType industryType = industryTypeName != null ? new StockIndustryType(industryTypeName) : null;
			Stock stock = new Stock(isinCode, stockCode, stockName, marketType, issueType, industryType, releaseDate, cfiCode);
			
			stocks.add(stock);
		}
		return stocks;
	}

}

package com.leochung0728.quartz.parser.web.stockData;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
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
	
	public void parsePageSource() {
		Document doc = Jsoup.parse(this.pageSource);
		
		Element tbody = doc.selectFirst("table tbody");
		Elements trs = tbody.select("tr");
		
		if (trs.size() < 2) return;
		trs.remove(0);
		
		for (Element tr : trs) {
			Elements tds = tr.select("td");
			String isinCode = StringUtils.trim(tds.get(1).text());
			String stockCode = StringUtils.trim(tds.get(2).text());
			String stockName = StringUtils.trim(tds.get(3).text());
			String marketTypeName = StringUtils.trim(tds.get(4).text());
			String issueTypeName = StringUtils.trim(tds.get(5).text());
			String industryTypeName = StringUtils.trim(tds.get(6).text());
			Date releaseDate = null;
			try {
				releaseDate = DateUtils.parseDateStrictly(StringUtils.trim(tds.get(7).text()), new String[] { "yyyy/MM/dd" });
			} catch (ParseException e) {
				log.error(String.format("parse date error, isinCode = %s", isinCode), e);
			}
			String cfiCode = StringUtils.trim(tds.get(6).text());
		}
		
	}

}

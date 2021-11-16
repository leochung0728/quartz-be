package com.leochung0728.quartz.parser.web.stockTransactionData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

import lombok.Getter;

import org.springframework.web.util.UriBuilder;

import com.leochung0728.quartz.entity.Vo;
import com.leochung0728.quartz.parser.web.stockData.MarketType;
import com.leochung0728.quartz.table.Stock;
import com.leochung0728.quartz.table.StockTransaction;
import com.leochung0728.quartz.util.RequestUtils;

@Getter
@Component("StockTransactionWebParser")
@Scope("prototype")
public class WebParser {
	static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/{stockCode}.{type}?period1={startDateLong}&period2={endDateLong}&interval=1d&events=history&crumb=hP2rOschxO0";

	// search params
	private Stock stock = null;
	private String type = null;
	private Long startDateLone = null;
	private Long endDateLone = null;

	private String searchUrl = null;
	private String jsonStr = null;

	public void setSearchParam(Stock stock, String startDateStr, String endDateStr) throws ParseException {
		Assert.isTrue(stock != null, "stock is null");
		
		this.stock = stock;
		this.type = MarketType.上市.name().equals(stock.getMarketType().getName()) ? "TW" : "TWO";
		
		if (StringUtils.isNotBlank(startDateStr)) {
			this.startDateLone = DateUtils.parseDateStrictly(startDateStr, new String[] { "yyyy/MM/dd" }).getTime() / 1000;
		} else {
			this.startDateLone = 0L;
		}
		
		if (StringUtils.isNotBlank(endDateStr)) {
			this.endDateLone = DateUtils.parseDateStrictly(endDateStr, new String[] { "yyyy/MM/dd" }).getTime() / 1000;
		} else {
			this.endDateLone = new Date().getTime() / 1000;
		}
	}

	private void setSearchUrl() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
		factory.setEncodingMode(EncodingMode.TEMPLATE_AND_VALUES);
		UriBuilder uriBuilder = factory.builder();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stockCode", this.stock.getStockCode());
		paramMap.put("type", this.type);
		paramMap.put("startDateLong", String.valueOf(this.startDateLone));
		paramMap.put("endDateLong", String.valueOf(this.endDateLone));

		this.searchUrl = uriBuilder.build(paramMap).toString();
	}

	public void search() throws IOException {
		setSearchUrl();
		this.jsonStr = RequestUtils.getResponse(searchUrl, searchUrl, Method.GET).body();
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
	
	public Vo<List<StockTransaction>> parseData() {
		Vo<List<StockTransaction>> vo = Vo.failure();
		List<StockTransaction> StockTransactions = new ArrayList<>();
		
		JSONObject jsonObj = new JSONObject(this.jsonStr);
		boolean notHasError = jsonObj.getJSONObject("chart").isNull("error");
		if (!notHasError) {
			vo.setMsg(jsonObj.getJSONObject("chart").getJSONObject("error").toString());
			return vo;
		}
		
		JSONObject result = jsonObj.getJSONObject("chart").getJSONArray("result").getJSONObject(0);
		JSONArray timestampArr = result.getJSONArray("timestamp");
		JSONObject indicators = result.getJSONObject("indicators");
		JSONArray openArr = indicators.getJSONArray("quote").getJSONObject(0).getJSONArray("open");
		JSONArray highArr = indicators.getJSONArray("quote").getJSONObject(0).getJSONArray("high");
		JSONArray volumeArr = indicators.getJSONArray("quote").getJSONObject(0).getJSONArray("volume");
		JSONArray closeArr = indicators.getJSONArray("quote").getJSONObject(0).getJSONArray("close");
		JSONArray lowArr = indicators.getJSONArray("quote").getJSONObject(0).getJSONArray("low");
		JSONArray adjcloseArr = indicators.getJSONArray("adjclose").getJSONObject(0).getJSONArray("adjclose");
		
		for (int idx = 0; idx < timestampArr.length(); idx++) {
			long millis = timestampArr.getLong(idx) * 1000;
			Date date = new Date(millis);
			
			Double open = null;
			Double high = null;
			Long volume = null;
			Double close = null;
			Double low = null;
			Double adjclose = null;
			
			if (!openArr.isNull(idx)) {
				open = openArr.getBigDecimal(idx).divide(new BigDecimal(Double.toString(1)), 2, RoundingMode.HALF_UP).doubleValue();
				high = highArr.getBigDecimal(idx).divide(new BigDecimal(Double.toString(1)), 2, RoundingMode.HALF_UP).doubleValue();
				volume = volumeArr.getLong(idx);
				close = closeArr.getBigDecimal(idx).divide(new BigDecimal(Double.toString(1)), 2, RoundingMode.HALF_UP).doubleValue();
				low = lowArr.getBigDecimal(idx).divide(new BigDecimal(Double.toString(1)), 2, RoundingMode.HALF_UP).doubleValue();
				adjclose = adjcloseArr.getBigDecimal(idx).divide(new BigDecimal(Double.toString(1)), 2, RoundingMode.HALF_UP).doubleValue();
			}
			StockTransactions.add(new StockTransaction(this.stock.getIsinCode(), date, open, high, low, close, adjclose, volume));
		}
		return Vo.success(StockTransactions, null);
	}

}

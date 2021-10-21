package com.leochung0728.quartz.parser.web.medicalExpensesRefundCap;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.leochung0728.quartz.util.RequestUtils;


public class WebParser {
	static final String FEED_BASE = "https://www.nhi.gov.tw/Content_List.aspx?n=34935D25C6F4E5E5&topn=5FE8C9FEAE863B46&upn=E0ECF9FB05661DE1";

	public WebParser() {
	}

	private Document getDocument(String url) throws IOException {
		return RequestUtils.getDocument(url, FEED_BASE, Method.GET);
	}

	public List<Map<String, Object>> getTableDate() throws Exception {
		List<Map<String, Object>> result = new ArrayList<>();
		
		Document document = getDocument(FEED_BASE);
		Elements tables = document.select(".group.page-content table");
		for (Element table : tables) {
			Map<String, Integer> colNameDict = new HashMap<>();
			Elements trs = table.select("tr");
			for (int rowIdx = 0; rowIdx < trs.size(); rowIdx++) {
				Element tr = trs.get(rowIdx);
				Map<String, String> data = new HashMap<>();
				if (rowIdx == 0) { // title
					Elements ths = tr.select("th");
					for (int colIdx = 0; colIdx < ths.size(); colIdx++) {
						Element th = ths.get(colIdx);
						String title = th.text();
						for (Column col : Column.getWebCols()) {
							if (title.contains(col.getWebColName())) {
								colNameDict.put(col.getWebColName(), colIdx);
								break;
							}
						}
					}
					continue;

				} else { // content
					Elements tds = tr.select("td");
					for (Map.Entry<String, Integer> e : colNameDict.entrySet()) {
						data.put(e.getKey(), tds.get(e.getValue()).text());
					}
					Map<String, Object> parseDate = parseData(data);
					result.add(parseDate);
				}
			}
		}
		return result;
	}

	private Map<String, Object> parseData(Map<String, String> data) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();

		final String[] numericalCol = new String[] { Column.門診.getWebColName(), Column.急診.getWebColName(),
				Column.住院.getWebColName() };
		for (Map.Entry<String, String> e : data.entrySet()) {
			String key = e.getKey();
			String val = e.getValue();

			if (Arrays.asList(numericalCol).contains(key)) {
				Column col = Column.getByWebColName(key);
				result.put(col.getDataColName(), parseCurrency(val, Locale.US).toString());

			} else if (key.equals(Column.年月.getWebColName())) {
				String[] dates = val.split("~");
				if (dates.length != 2) {
					throw new Exception("YEAR_MONTH format error");
				}
				for (int i = 0; i < dates.length; i++) {
					TemporalAccessor temporalAccessor = this.parseMinguoDate(dates[i], "yyy.MM");
					int year = temporalAccessor.get(ChronoField.YEAR) + 1911;
					int month = temporalAccessor.get(ChronoField.MONTH_OF_YEAR);

					SimpleDateFormat sdf_in = new SimpleDateFormat("yyyyM");
					SimpleDateFormat sdf_out = new SimpleDateFormat("yyyyMM");
					Date date = sdf_in.parse(String.valueOf(year) + String.valueOf(month));

					if (i == 0) {
						result.put(Column.起始年月.getDataColName(), sdf_out.format(date));
					} else if (i == 1) {
						result.put(Column.結束年月.getDataColName(), sdf_out.format(date));
					}
				}
			}
		}
		return result;
	}

	public static BigDecimal parseCurrency(final String amount, final Locale locale) throws ParseException {
		final NumberFormat format = NumberFormat.getNumberInstance(locale);
		if (format instanceof DecimalFormat) {
			((DecimalFormat) format).setParseBigDecimal(true);
		}
		return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]", ""));
	}

	public TemporalAccessor parseMinguoDate(String dateStr, String pattern) {
		Chronology chrono = MinguoChronology.INSTANCE;
		DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient().appendPattern(pattern).toFormatter()
				.withChronology(chrono).withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
		return df.parse(dateStr);
	}

}

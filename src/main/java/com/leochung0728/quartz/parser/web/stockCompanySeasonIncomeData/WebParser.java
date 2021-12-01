package com.leochung0728.quartz.parser.web.stockCompanySeasonIncomeData;

import com.leochung0728.quartz.table.StockCompanySeasonIncome;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Component("StockCompanySeasonIncomeWebParser")
@Scope("prototype")
public class WebParser {
    // before 102
    //	https://mops.twse.com.tw/mops/web/ajax_t51sb08?encodeURIComponent=1&step=1&firstin=1&off=1&isQuery=Y&TYPEK=sii&year=90&season=01
    // after 102
    //	https://mops.twse.com.tw/mops/web/ajax_t163sb04?encodeURIComponent=1&step=1&firstin=1&off=1&isQuery=Y&TYPEK=sii&year=110&season=01

    static final String BASE_URL_V1 = "https://mops.twse.com.tw/mops/web/ajax_t51sb08?encodeURIComponent=1&step=1&firstin=1&off=1&isQuery=Y&TYPEK={type}&year={minguoYear}&season={season}";
    static final String BASE_URL_V2 = "https://mops.twse.com.tw/mops/web/ajax_t163sb04?encodeURIComponent=1&step=1&firstin=1&off=1&isQuery=Y&TYPEK={type}&year={minguoYear}&season={season}";

    static final String TABLE_SELECTOR_V1 = "table table";
    static final String TABLE_SELECTOR_V2 = "table table table";

    // search params
    private int year;
    private int season;

    private boolean isV1;
    private String baseUrl;

    public void setSearchParam(int year, int season) {
        this.year = year;
        this.season = season;
        this.init();
    }

    private void init() {
        int minguoYear = this.year - 1911;
        this.isV1 = minguoYear < 102;
        this.baseUrl = this.isV1 ? BASE_URL_V1 : BASE_URL_V2;
    }

    public List<String> getSearchUrl() {
        List<String> searchUrls = new ArrayList<>();

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(this.baseUrl);
        factory.setEncodingMode(EncodingMode.TEMPLATE_AND_VALUES);
        UriBuilder uriBuilder = factory.builder();

        List<Map<String, Object>> paramMaps = new ArrayList<>();

        int minguoYear = this.year - 1911;

        for (String type : new String[]{"sii", "otc"}) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("type", type);
            paramMap.put("minguoYear", minguoYear);
            paramMap.put("season", this.season);
            paramMaps.add(paramMap);
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

    public List<StockCompanySeasonIncome> parseData(Document document) {
        return this.isV1 ? this.parseDataV1(document) : this.parseDataV2(document);
    }

    private List<StockCompanySeasonIncome> parseDataV1(Document document) {
        List<StockCompanySeasonIncome> result = new ArrayList<>();

        Elements tables = document.select("table.hasBorder");

        for (Element table : tables) {
            Elements trs = table.select("tr");

            Integer epsIdx = null;
            for (Element tr : trs) {
                if (tr.hasClass("tblHead")) {
                    epsIdx = null;
                    Elements ths = tr.select("th");
                    for (int idx = 0; idx < ths.size(); idx++) {
                        Element th = ths.get(idx);
                        if (th.text().contains("每股稅後盈餘")) {
                            epsIdx = idx;
                            break;
                        }
                    }
                } else {
                    if (epsIdx == null) break;

                    Elements tds = tr.select("td");
                    if (tds.size() < epsIdx + 1) break;

                    try {
                        String stockCode = StringUtils.trim(StringEscapeUtils.unescapeHtml4(tds.get(0).text()));
                        String epsCell = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(epsIdx).text()));
                        Double esp = epsCell != null ? new DecimalFormat().parse(epsCell).doubleValue() : null;

                        StockCompanySeasonIncome entity = new StockCompanySeasonIncome(stockCode, this.year, this.season, esp);
                        log.debug("entity: {}", entity);
                        result.add(entity);

                    } catch (Exception e) {
                        log.error("Parse error: tr = " + tr, e);
                    }
                }
            }
        }
        return result;
    }

    private List<StockCompanySeasonIncome> parseDataV2(Document document) {
        List<StockCompanySeasonIncome> result = new ArrayList<>();

        Elements tables = document.select("table.hasBorder");

        for (Element table : tables) {
            Elements trs = table.select("tr");

            Integer epsIdx = null;
            for (Element tr : trs) {
                if (tr.hasClass("tblHead")) {
                    epsIdx = null;
                    Elements ths = tr.select("th");
                    for (int idx = 0; idx < ths.size(); idx++) {
                        Element th = ths.get(idx);
                        if (th.text().contains("基本每股盈餘")) {
                            epsIdx = idx;
                            break;
                        }
                    }
                } else {
                    if (epsIdx == null) break;

                    Elements tds = tr.select("td");
                    if (tds.size() < epsIdx + 1) break;

                    try {
                        String stockCode = StringUtils.trim(StringEscapeUtils.unescapeHtml4(tds.get(0).text()));
                        String epsCell = StringUtils.trimToNull(StringEscapeUtils.unescapeHtml4(tds.get(epsIdx).text()));
                        Double esp = epsCell != null ? new DecimalFormat().parse(epsCell).doubleValue() : null;

                        StockCompanySeasonIncome entity = new StockCompanySeasonIncome(stockCode, this.year, this.season, esp);
                        log.debug("entity: {}", entity);
                        result.add(entity);

                    } catch (Exception e) {
                        log.error("Parse error: tr = " + tr, e);
                    }
                }
            }
        }
        return result;
    }

}

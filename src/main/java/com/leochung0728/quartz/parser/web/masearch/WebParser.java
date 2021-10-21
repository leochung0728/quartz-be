package com.leochung0728.quartz.parser.web.masearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.Assert;

import com.leochung0728.quartz.table.WebListEntryMASearch;
import com.leochung0728.quartz.util.ObjectMapperUtils;
import com.leochung0728.quartz.util.RequestUtils;
import com.leochung0728.quartz.util.Tess4jUtils;

public class WebParser {
	static final String BASE_URL = "https://ma.mohw.gov.tw/masearch/";
	static final String IMAGE_URL = "https://ma.mohw.gov.tw/ValidateCode.aspx";
	static final String IMAGE_PATH = "src/main/resources/image/temp";
	
	private String name = "";
	private String kind = "";
	private String city = "";
	
	private Map<String, String> cookies = null;
	private Document document = null;
	private File image = null;
	private Map<String, String> kindDict = new HashMap<String, String>();
	private Map<String, String> cityDict = new HashMap<String, String>();
	private Integer currentPage = null;
	private Integer totalPage = null;
	
	public WebParser(String name, String kind, String city) {
		this.name = name;
		this.kind = kind;
		this.city = city;
		check();
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public Integer getTotalPage() {
		return totalPage;
	}
	
	
	public void check() {
		this.checkImagePath();
	}
	
	public void checkImagePath() {
		File directory = new File(IMAGE_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}
	
	public void start() throws IOException {
		init();
	}
	
	public void init() throws IOException {
		Response response = RequestUtils.getResponse(BASE_URL, BASE_URL, Method.GET);
		document = response.parse();
		cookies = response.cookies();
	}
	
	public boolean search(Integer maxTryTimes) throws Exception {
		maxTryTimes = maxTryTimes == null ? 1 :  maxTryTimes;
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
	
	public void search() throws Exception {
		try {
			init();
			setSelectDict();
			setVerifyImage();
			String validateCode = parseCaptcha();
			Map<String, String> payload = getSearchPayload(validateCode);
			Connection connection = RequestUtils.getConnection(BASE_URL, BASE_URL, Method.POST).data(payload).cookies(cookies).timeout(10 * 1000);
			Response response = connection.execute();
			document = response.parse();
			if (!checkIsLogin()) {
				throw new Exception("Login Failed!");
			}
			setPageInfo();
		} finally {
			if (image != null) {
				FileUtils.forceDeleteOnExit(image);
			}
		}
	}
	
	public void setSelectDict() {
		Element element = document.selectFirst("#ctl00_ContentPlaceHolder1_ddlAREA_CODE");
		Assert.notNull(element, "[縣市]選單為Null");
		Elements options = element.select("option");
		cityDict.clear();
		for (Element option : options) {
			cityDict.put(option.text(), option.attr("value"));
		}
		
		element = document.selectFirst("#ctl00_ContentPlaceHolder1_ddlBAS_KIND");
		Assert.notNull(element, "[機構類別]選單為Null");
		options = element.select("option");
		kindDict.clear();
		for (Element option : options) {
			kindDict.put(option.text(), option.attr("value"));
		}
	}
	
	public void setVerifyImage() throws IOException {
		Response imageResponse = RequestUtils.getConnection(IMAGE_URL, BASE_URL, Method.GET)
				.cookies(cookies)
				.ignoreContentType(true)
				.execute();
		
		Assert.isTrue(imageResponse.contentType().startsWith("image/Jpeg"),
				String.format("contentType not is image/Jpeg, contentType=[%s]", imageResponse.contentType()));
		
		File directory = new File(IMAGE_PATH);
		File image = File.createTempFile("tmp_", ".jpg", directory);
		saveImage(imageResponse.bodyAsBytes(), image);
		this.image = image;
	}
	
	
	
	public Map<String, String> getInitPayload() {
		String viewStateGenerator = document.selectFirst("#__VIEWSTATEGENERATOR").attr("value");
		String viewStateEncrypted = document.selectFirst("#__VIEWSTATEENCRYPTED").attr("value");
		String eventValidation = document.selectFirst("#__EVENTVALIDATION").attr("value");
		String viewState = document.selectFirst("#__VIEWSTATE").attr("value");
		
		Map<String, String> payload = new HashMap<>();
		payload.put("__eo_obj_states", "");
		payload.put("__eo_sc", "");
		payload.put("eo_version", "12.0.10.2");
		payload.put("eo_style_keys", "/wFk");
		
		payload.put("ctl00$ContentPlaceHolder1$txtBAS_NAME", "");
		payload.put("ctl00$ContentPlaceHolder1$ddlBAS_KIND", "");
		payload.put("ctl00$ContentPlaceHolder1$ddlAREA_CODE", "");
		payload.put("ctl00$ContentPlaceHolder1$ddlZIP_CODE", "");
		payload.put("ctl00$ContentPlaceHolder1$ddlBasDep", "");
		payload.put("ctl00$ContentPlaceHolder1$TextBox1", "");
		
		payload.put("ctl00$ContentPlaceHolder1$txtBAS_NAME", StringUtils.defaultString(name, ""));
		payload.put("ctl00$ContentPlaceHolder1$ddlBAS_KIND", StringUtils.defaultString(kindDict.get(kind), ""));
		payload.put("ctl00$ContentPlaceHolder1$ddlAREA_CODE", StringUtils.defaultString(cityDict.get(city), ""));
		
		payload.put("__EVENTTARGET", "");
		payload.put("__EVENTARGUMENT", "");
		
		payload.put("__VIEWSTATEGENERATOR", viewStateGenerator);
		payload.put("__VIEWSTATEENCRYPTED", viewStateEncrypted);
		payload.put("__EVENTVALIDATION", eventValidation);
		payload.put("__VIEWSTATE", viewState);
		return payload;
	}
	
	private Map<String, String> getSearchPayload(String validateCode) {
		Map<String, String> payload = getInitPayload();
		payload.put("ctl00$ContentPlaceHolder1$TextBox1", validateCode);
		payload.put("ctl00$ContentPlaceHolder1$btnSearch", "查詢");
		return payload;
	}
	
	private Map<String, String> getChangePagePayload(int page) {
		Map<String, String> payload = getInitPayload();
		payload.put("ctl00$ContentPlaceHolder1$NetPager1$txtPage", String.valueOf(page));
		payload.put("ctl00$ContentPlaceHolder1$NetPager1$btGo", "Go");
		return payload;
	}
	
	public boolean jumpToPage(int page) throws IOException {
		Map<String, String> payload = getChangePagePayload(page);
		Connection connection = RequestUtils.getConnection(BASE_URL, BASE_URL, Method.POST).data(payload).cookies(cookies).timeout(10 * 1000);
		Response response = connection.execute();
		document = response.parse();
		setPageInfo();
		return currentPage == page;
	}
	
	public boolean jumpToNextPage() throws IOException {
		int nextPage = currentPage + 1;
		return jumpToPage(nextPage);
	}

	private String parseCaptcha() {
		String code = Tess4jUtils.readChar(image);
		return RegExUtils.removeAll(code, "\\s");
	}
	
	public boolean checkIsLogin() {
		return !StringUtils.contains(document.toString(), "<script>alert('驗證碼錯誤!')</script>");
	}
	
	public void setPageInfo() {
		String currentPageStr = document.selectFirst("span#ctl00_ContentPlaceHolder1_NetPager1_lblCurrentIndex").text();
		String totalPageStr = document.select("span#ctl00_ContentPlaceHolder1_NetPager1_lblPageCount").text();
		currentPage = Integer.parseInt(currentPageStr.replaceAll("[^\\d.,]", ""));
		totalPage = Integer.parseInt(totalPageStr.replaceAll("[^\\d.,]", ""));
	}
	
	public List<WebListEntryMASearch> getListData() throws Exception {
		List<WebListEntryMASearch> result = new ArrayList<>();
		
		Element table = document.selectFirst("table#ctl00_ContentPlaceHolder1_gviewMain");
		Elements trs = table.select("tbody > tr");
		
		if (trs.size() <= 1) {
			return result;
		}
		for (int index = 1; index < trs.size(); index++) {
			Elements tds = trs.get(index).select("td");
			if (tds.size() < 4) {
				throw new Exception("td size less than 4 !");
			}
			String name = tds.get(0).text();
			String link = tds.get(1).selectFirst("a").attr("href");
			String city = tds.get(2).text();
			String district = tds.get(3).text();
			result.add(new WebListEntryMASearch(link, name, city, district));
		}
		return result;
	}
	
	
	private void saveImage(byte[] byteData, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(byteData);
		out.close();
	}
	
	public boolean hasNextPage() throws Exception {
		if (currentPage == null || totalPage == null) {
			throw new Exception("page parameter is null");
		}
		return currentPage < totalPage;
	}
	
	public static void main(String[] args) throws Exception {
		WebParser parser = new WebParser("", "醫療機構", "臺北市");
		
		boolean isLoginSucc = parser.search(3);
		System.out.println(isLoginSucc);
		
		if (!isLoginSucc) {
			System.out.println("login failed");
		}
		
		System.out.println(parser.currentPage);
		List<WebListEntryMASearch> data = parser.getListData();
		System.out.println(ObjectMapperUtils.getJsonStr(data, true));
		
		parser.jumpToPage(2);
		System.out.println(parser.currentPage);
		data = parser.getListData();
		System.out.println(ObjectMapperUtils.getJsonStr(data, true));
	}
	
}

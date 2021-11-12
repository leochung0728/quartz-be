package com.leochung0728.quartz.util;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.Connection.Response;

public class RequestUtils {
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36";
	
	public static Connection getConnection(String url, String referer, Method method) {
		Connection connection = SSLHelper.getConnection(url)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
			.header("User-Agent", USER_AGENT)
			.header("Accept-Language", "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4")
			.header("Progma", "no-cache")
			.ignoreContentType(true)
			.ignoreHttpErrors(true);
		
		if (referer != null) {
			connection.referrer(referer);
		}
		
		if (method != null) {
			connection.method(method);
		}
		return connection;
	}
	
	public static Response getResponse(String url, String referer, Method method) throws IOException {
		Connection connection = getConnection(url, referer, method);
		return connection.execute();
	}
	
	public static Document getDocument(String url, String referer, Method method) throws IOException {
		Response response = getResponse(url, referer, method);
		return response.parse();
	}
}

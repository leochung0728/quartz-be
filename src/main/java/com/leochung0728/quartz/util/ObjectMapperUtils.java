package com.leochung0728.quartz.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {

	public static <T> T convertValue(Object obj, Class<T> toValueType) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(obj, toValueType);
	}
	
	public static String getJsonStr(Object obj) throws JsonProcessingException {
		return getJsonStr(obj, false);
	}
	
	public static String getJsonStr(Object obj, boolean pretty) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		if (pretty) {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		}
		return mapper.writeValueAsString(obj);
	}
}

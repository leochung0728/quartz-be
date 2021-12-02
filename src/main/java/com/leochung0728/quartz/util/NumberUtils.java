package com.leochung0728.quartz.util;

public class NumberUtils {

	public static boolean isNumeric(String numStr) {
		try {
			Double.parseDouble(numStr);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

package com.leochung0728.quartz.util;

import java.text.DecimalFormat;

public class NumberUtils {

	public static boolean isNumeric(String numStr) {
		try {
			new DecimalFormat().parse(numStr).doubleValue();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Double parseDouble(String numStr) {
		try {
			return new DecimalFormat().parse(numStr).doubleValue();
		} catch (Exception e) {
			return null;
		}
	}
}

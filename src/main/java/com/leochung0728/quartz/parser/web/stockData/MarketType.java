package com.leochung0728.quartz.parser.web.stockData;

public enum MarketType {
	上市("1"),
	上櫃("2");
	
	private String code;
	
	private MarketType(String code) {
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public static MarketType getByCode(String code) {
		for (MarketType type : MarketType.values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		return null;
	}
}

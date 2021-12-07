package com.leochung0728.quartz.stock.strategy;

public class ESPStrategy implements Strategy {

	Object data;

	public ESPStrategy (Object data) {
		this.data = data;

	}

	@Override
	public boolean isMeet() {
		return false;
	}
}

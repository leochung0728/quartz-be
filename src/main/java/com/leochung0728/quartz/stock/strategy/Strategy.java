package com.leochung0728.quartz.stock.strategy;

public interface Strategy {
    String name = "strategy_name";

    public boolean isMeet(Object data);
}

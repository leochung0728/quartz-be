package com.leochung0728.quartz.stock.strategy;

import java.time.Period;

public class EPSStrategy implements Strategy {

    public EPSStrategy(Object data, Period scope) {
    }

    @Override
    public boolean isMeet(Object data) {
        return false;
    }
}

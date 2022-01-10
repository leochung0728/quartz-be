package com.leochung0728.quartz.stock.core.cost;

import com.leochung0728.quartz.stock.core.num.Num;

import java.io.Serializable;

public interface CostModel extends Serializable {

    /**
     * @param position   the position
     * @param finalIndex final index of consideration for open positions
     * @return Calculates the trading cost of a single position
     */
    Num calculate(Position position, int finalIndex);

    /**
     * @param position the position
     * @return Calculates the trading cost of a single position
     */
    Num calculate(Position position);

    /**
     * @param price  the price per asset
     * @param amount number of traded assets
     * @return Calculates the trading cost for a certain traded amount
     */
    Num calculate(Num price, Num amount);

    boolean equals(CostModel model);
}
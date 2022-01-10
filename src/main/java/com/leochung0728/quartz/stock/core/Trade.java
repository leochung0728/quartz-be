package com.leochung0728.quartz.stock.core;

import java.io.Serializable;
import java.util.Objects;

public class Trade implements Serializable {

    /**
     * The type of an {@link org.ta4j.core.Trade trade}.
     *
     * A BUY corresponds to a <i>BID</i> trade. A SELL corresponds to an <i>ASK</i>
     * trade.
     */
    public enum TradeType {

        BUY {
            @Override
            public TradeType complementType() {
                return SELL;
            }
        },
        SELL {
            @Override
            public TradeType complementType() {
                return BUY;
            }
        };

        /**
         * @return the complementary trade type
         */
        public abstract TradeType complementType();
    }

    /**
     * Type of the trade
     */
    private TradeType type;

    /**
     * The index the trade was executed
     */
    private int index;

    /**
     * the trade price per asset
     */
    private Num pricePerAsset;

    /**
     * The net price for the trade, net transaction costs
     */
    private Num netPrice;

    /**
     * the trade amount
     */
    private Num amount;

    /**
     * The cost for executing the trade
     */
    private Num cost;

    /**
     * The cost model for trade execution
     */
    private CostModel costModel;

    /**
     * Constructor.
     *
     * @param index  the index the trade is executed
     * @param series the bar series
     * @param type   the trade type
     */
    protected Trade(int index, org.ta4j.core.BarSeries series, TradeType type) {
        this(index, series, type, series.numOf(1));
    }

    /**
     * Constructor.
     *
     * @param index  the index the trade is executed
     * @param series the bar series
     * @param type   the trade type
     * @param amount the trade amount
     */
    protected Trade(int index, org.ta4j.core.BarSeries series, TradeType type, Num amount) {
        this(index, series, type, amount, new ZeroCostModel());
    }

    /**
     * Constructor.
     *
     * @param index                the index the trade is executed
     * @param series               the bar series
     * @param type                 the trade type
     * @param amount               the trade amount
     * @param transactionCostModel the cost model for trade execution cost
     */
    protected Trade(int index, org.ta4j.core.BarSeries series, TradeType type, Num amount, CostModel transactionCostModel) {
        this.type = type;
        this.index = index;
        this.amount = amount;
        setPricesAndCost(series.getBar(index).getClosePrice(), amount, transactionCostModel);
    }

    /**
     * Constructor.
     *
     * @param index         the index the trade is executed
     * @param type          the trade type
     * @param pricePerAsset the trade price per asset
     */
    protected Trade(int index, TradeType type, Num pricePerAsset) {
        this(index, type, pricePerAsset, pricePerAsset.numOf(1));
    }

    /**
     * Constructor.
     *
     * @param index         the index the trade is executed
     * @param type          the trade type
     * @param pricePerAsset the trade price per asset
     * @param amount        the trade amount
     */
    protected Trade(int index, TradeType type, Num pricePerAsset, Num amount) {
        this(index, type, pricePerAsset, amount, new ZeroCostModel());
    }

    /**
     * Constructor.
     *
     * @param index                the index the trade is executed
     * @param type                 the trade type
     * @param pricePerAsset        the trade price per asset
     * @param amount               the trade amount
     * @param transactionCostModel the cost model for trade execution
     */
    protected Trade(int index, TradeType type, Num pricePerAsset, Num amount, CostModel transactionCostModel) {
        this.type = type;
        this.index = index;
        this.amount = amount;

        setPricesAndCost(pricePerAsset, amount, transactionCostModel);
    }

    /**
     * @return the trade type (BUY or SELL)
     */
    public TradeType getType() {
        return type;
    }

    /**
     * @return the costs of the trade
     */
    public Num getCost() {
        return cost;
    }

    /**
     * @return the index the trade is executed
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the trade price per asset
     */
    public Num getPricePerAsset() {
        return pricePerAsset;
    }

    /**
     * @return the trade price per asset, or, if <code>NaN</code>, the close price
     *         from the supplied {@link org.ta4j.core.BarSeries}.
     */
    public Num getPricePerAsset(org.ta4j.core.BarSeries barSeries) {
        if (pricePerAsset.isNaN()) {
            return barSeries.getBar(index).getClosePrice();
        }
        return pricePerAsset;
    }

    /**
     * @return the trade price per asset, net transaction costs
     */
    public Num getNetPrice() {
        return netPrice;
    }

    /**
     * @return the trade amount
     */
    public Num getAmount() {
        return amount;
    }

    /**
     * @return the cost model for trade execution
     */
    public CostModel getCostModel() {
        return costModel;
    }

    /**
     * Sets the raw and net prices of the trade
     *
     * @param pricePerAsset        the raw price of the asset
     * @param amount               the amount of assets ordered
     * @param transactionCostModel the cost model for trade execution
     */
    private void setPricesAndCost(Num pricePerAsset, Num amount, CostModel transactionCostModel) {
        this.costModel = transactionCostModel;
        this.pricePerAsset = pricePerAsset;
        this.cost = transactionCostModel.calculate(this.pricePerAsset, amount);

        Num costPerAsset = cost.dividedBy(amount);
        // add transaction costs to the pricePerAsset at the trade
        if (type.equals(TradeType.BUY)) {
            this.netPrice = this.pricePerAsset.plus(costPerAsset);
        } else {
            this.netPrice = this.pricePerAsset.minus(costPerAsset);
        }
    }

    /**
     * @return true if this is a BUY trade, false otherwise
     */
    public boolean isBuy() {
        return type == TradeType.BUY;
    }

    /**
     * @return true if this is a SELL trade, false otherwise
     */
    public boolean isSell() {
        return type == TradeType.SELL;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, index, pricePerAsset, amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final org.ta4j.core.Trade other = (org.ta4j.core.Trade) obj;
        return Objects.equals(type, other.type) && Objects.equals(index, other.index)
                && Objects.equals(pricePerAsset, other.pricePerAsset) && Objects.equals(amount, other.amount);
    }

    @Override
    public String toString() {
        return "Trade{" + "type=" + type + ", index=" + index + ", price=" + pricePerAsset + ", amount=" + amount + '}';
    }

    /**
     * @param index  the index the trade is executed
     * @param series the bar series
     * @return a BUY trade
     */
    public static org.ta4j.core.Trade buyAt(int index, org.ta4j.core.BarSeries series) {
        return new org.ta4j.core.Trade(index, series, TradeType.BUY);
    }

    /**
     * @param index                the index the trade is executed
     * @param price                the trade price
     * @param amount               the trade amount
     * @param transactionCostModel the cost model for trade execution
     * @return a BUY trade
     */
    public static org.ta4j.core.Trade buyAt(int index, Num price, Num amount, CostModel transactionCostModel) {
        return new org.ta4j.core.Trade(index, TradeType.BUY, price, amount, transactionCostModel);
    }

    /**
     * @param index  the index the trade is executed
     * @param price  the trade price
     * @param amount the trade amount
     * @return a BUY trade
     */
    public static org.ta4j.core.Trade buyAt(int index, Num price, Num amount) {
        return new org.ta4j.core.Trade(index, TradeType.BUY, price, amount);
    }

    /**
     * @param index  the index the trade is executed
     * @param series the bar series
     * @param amount the trade amount
     * @return a BUY trade
     */
    public static org.ta4j.core.Trade buyAt(int index, org.ta4j.core.BarSeries series, Num amount) {
        return new org.ta4j.core.Trade(index, series, TradeType.BUY, amount);
    }

    /**
     * @param index                the index the trade is executed
     * @param series               the bar series
     * @param amount               the trade amount
     * @param transactionCostModel the cost model for trade execution
     * @return a BUY trade
     */
    public static org.ta4j.core.Trade buyAt(int index, org.ta4j.core.BarSeries series, Num amount, CostModel transactionCostModel) {
        return new org.ta4j.core.Trade(index, series, TradeType.BUY, amount, transactionCostModel);
    }

    /**
     * @param index  the index the trade is executed
     * @param series the bar series
     * @return a SELL trade
     */
    public static org.ta4j.core.Trade sellAt(int index, org.ta4j.core.BarSeries series) {
        return new org.ta4j.core.Trade(index, series, TradeType.SELL);
    }

    /**
     * @param index  the index the trade is executed
     * @param price  the trade price
     * @param amount the trade amount
     * @return a SELL trade
     */
    public static org.ta4j.core.Trade sellAt(int index, Num price, Num amount) {
        return new org.ta4j.core.Trade(index, TradeType.SELL, price, amount);
    }

    /**
     * @param index                the index the trade is executed
     * @param price                the trade price
     * @param amount               the trade amount
     * @param transactionCostModel the cost model for trade execution
     * @return a SELL trade
     */
    public static org.ta4j.core.Trade sellAt(int index, Num price, Num amount, CostModel transactionCostModel) {
        return new org.ta4j.core.Trade(index, TradeType.SELL, price, amount, transactionCostModel);
    }

    /**
     * @param index  the index the trade is executed
     * @param series the bar series
     * @param amount the trade amount
     * @return a SELL trade
     */
    public static org.ta4j.core.Trade sellAt(int index, org.ta4j.core.BarSeries series, Num amount) {
        return new org.ta4j.core.Trade(index, series, TradeType.SELL, amount);
    }

    /**
     * @param index                the index the trade is executed
     * @param series               the bar series
     * @param amount               the trade amount
     * @param transactionCostModel the cost model for trade execution
     * @return a SELL trade
     */
    public static org.ta4j.core.Trade sellAt(int index, BarSeries series, Num amount, CostModel transactionCostModel) {
        return new org.ta4j.core.Trade(index, series, TradeType.SELL, amount, transactionCostModel);
    }

    /**
     * @return the value of a trade (without transaction cost)
     */
    public Num getValue() {
        return pricePerAsset.multipliedBy(amount);
    }
}

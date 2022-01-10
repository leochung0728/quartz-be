package com.leochung0728.quartz.stock.core.rule;


import com.leochung0728.quartz.stock.core.Rule;

public class AndRule extends AbstractRule {

    private final Rule rule1;
    private final Rule rule2;

    /**
     * Constructor
     *
     * @param rule1 a trading rule
     * @param rule2 another trading rule
     */
    public AndRule(Rule rule1, Rule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        final boolean satisfied = rule1.isSatisfied(index, tradingRecord) && rule2.isSatisfied(index, tradingRecord);
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }

    public Rule getRule1() {
        return rule1;
    }

    public Rule getRule2() {
        return rule2;
    }
}

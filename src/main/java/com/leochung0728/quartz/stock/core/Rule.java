package com.leochung0728.quartz.stock.core;


import org.ta4j.core.Strategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.rules.AndRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OrRule;
import org.ta4j.core.rules.XorRule;

/**
 * A rule for strategy building.
 *
 * A trading rule may be composed of a combination of other rules.
 *
 * A {@link Strategy trading strategy} is a pair of complementary (entry and
 * exit) rules.
 */
public interface Rule {

    /**
     * @param rule another trading rule
     * @return a rule which is the AND combination of this rule with the provided
     *         one
     */
    default org.ta4j.core.Rule and(org.ta4j.core.Rule rule) {
        return new AndRule(this, rule);
    }

    /**
     * @param rule another trading rule
     * @return a rule which is the OR combination of this rule with the provided one
     */
    default org.ta4j.core.Rule or(org.ta4j.core.Rule rule) {
        return new OrRule(this, rule);
    }

    /**
     * @param rule another trading rule
     * @return a rule which is the XOR combination of this rule with the provided
     *         one
     */
    default org.ta4j.core.Rule xor(org.ta4j.core.Rule rule) {
        return new XorRule(this, rule);
    }

    /**
     * @return a rule which is the logical negation of this rule
     */
    default org.ta4j.core.Rule negation() {
        return new NotRule(this);
    }

    /**
     * @param index the bar index
     * @return true if this rule is satisfied for the provided index, false
     *         otherwise
     */
    default boolean isSatisfied(int index) {
        return isSatisfied(index, null);
    }

    /**
     * @param index         the bar index
     * @param tradingRecord the potentially needed trading history
     * @return true if this rule is satisfied for the provided index, false
     *         otherwise
     */
    boolean isSatisfied(int index, TradingRecord tradingRecord);
}


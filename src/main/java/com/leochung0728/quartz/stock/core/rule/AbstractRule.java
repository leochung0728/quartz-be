package com.leochung0728.quartz.stock.core.rule;

import com.leochung0728.quartz.stock.core.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRule implements Rule {

    /** The logger */
    protected final transient Logger log = LoggerFactory.getLogger(getClass());

    /** The class name */
    private final String className = getClass().getSimpleName();

    /**
     * Traces the isSatisfied() method calls.
     *
     * @param index       the bar index
     * @param isSatisfied true if the rule is satisfied, false otherwise
     */
    protected void traceIsSatisfied(int index, boolean isSatisfied) {
        log.trace("{}#isSatisfied({}): {}", className, index, isSatisfied);
    }
}

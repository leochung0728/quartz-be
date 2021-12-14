package com.leochung0728.quartz.stock.strategy;

import com.leochung0728.quartz.enums.SeasonMonth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EPSMeanCompareStrategy implements Strategy {

    Table epsData;
    LocalDateTime localDateTime;
    int preIntervalSeason;
    CompareOperator operator;
    int compareNum;

    public EPSMeanCompareStrategy(Table epsData, LocalDateTime localDateTime, CompareOperator operator, int compareNum) {
        this(epsData, localDateTime, 1, operator, compareNum);
    }

    public EPSMeanCompareStrategy(Table epsData, LocalDateTime localDateTime, int preIntervalSeason, CompareOperator operator, int compareNum) {
        this.epsData = epsData;
        this.localDateTime = localDateTime;
        this.preIntervalSeason = preIntervalSeason;
        this.operator = operator;
        this.compareNum = compareNum;
    }

    @Override
    public boolean isMeet() {
        boolean result = false;
        Table filter = this.filter();
        Double meanEps = Double.NaN;
        if (filter.rowCount() != 0) {
            meanEps = (double) filter.summarize("eps", mean).apply().get(0, 0);
            result = switch (this.operator) {
                case GREATER -> meanEps > this.compareNum;
                case GREATER_EQUAL -> meanEps >= this.compareNum;
                case LESS -> meanEps < this.compareNum;
                case LESS_EQUAL -> meanEps <= this.compareNum;
                default -> false;
            };
        }
        log.debug("meanEps = {}, compareNum = {}, operator = {}", meanEps, this.compareNum, this.operator);
        return result;
    }

    private Table filter() {
        Table filter = this.epsData.emptyCopy();
        List<LocalDateTime> preDateTime = new ArrayList<>();
        for (int i = 1; i <= this.preIntervalSeason; i++) {
            preDateTime.add(this.localDateTime.plusMonths(-3L * i));
        }
        if (CollectionUtils.isEmpty(preDateTime)) return filter;

        for (int i = preDateTime.size() - 1; i >= 0; i--) {
            LocalDateTime ld = preDateTime.get(i);
            int year = ld.getYear();
            int season = SeasonMonth.getByMonth(ld.getMonthValue()).getSeason();

            Table temp = this.epsData.where(this.epsData.intColumn("year").isEqualTo(year)
                    .and(this.epsData.intColumn("season").isEqualTo(season)));
            filter.append(temp);
        }
        log.debug("filter = {}", filter.print());
        return filter;
    }

    public enum CompareOperator {
        GREATER_EQUAL, GREATER, LESS_EQUAL, LESS;
    }

}

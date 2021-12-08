package com.leochung0728.quartz.stock.strategy;

import com.leochung0728.quartz.enums.SeasonMonth;
import org.apache.commons.collections.CollectionUtils;
import tech.tablesaw.api.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EPSStrategy implements Strategy {

    Table epsData;
    LocalDateTime localDateTime;
    int preIntervalSeason;

    public EPSStrategy(Table epsData, LocalDateTime localDateTime) {
        this(epsData, localDateTime, 1);
    }

    public EPSStrategy(Table epsData, LocalDateTime localDateTime, int preIntervalSeason) {
        this.epsData = epsData;
        this.localDateTime = localDateTime;
        this.preIntervalSeason = preIntervalSeason;
    }

    @Override
    public boolean isMeet() {


        return false;
    }

    public Table filter() {
        Table filter = this.epsData.emptyCopy();
        List<LocalDateTime> preDateTime = new ArrayList<>();
        for (int i = 1; i <= this.preIntervalSeason; i++) {
            preDateTime.add(this.localDateTime.plusMonths(-3 * i));
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
        return filter;
    }
}

package com.leochung0728.quartz.stock.strategy;

import com.leochung0728.quartz.service.StockCompanySeasonIncomeService;
import com.leochung0728.quartz.service.StockDataService;
import com.leochung0728.quartz.table.StockCompanySeasonIncome;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class EPSStrategyTest {

    @Autowired
    StockDataService dataService;

    @Test
    void isMeet() {
    }

    @Test
    void filter() {
        Table epsData = dataService.getStockEPSData("1101");
        log.info(epsData.print(5));

        EPSStrategy epsStrategy = new EPSStrategy(epsData, LocalDateTime.now(ZoneId.systemDefault()), 5);
        log.info(epsStrategy.filter().print());
    }
}
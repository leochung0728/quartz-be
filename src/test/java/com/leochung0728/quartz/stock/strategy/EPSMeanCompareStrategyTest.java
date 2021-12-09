package com.leochung0728.quartz.stock.strategy;

import com.leochung0728.quartz.service.StockDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static tech.tablesaw.aggregate.AggregateFunctions.*;
import tech.tablesaw.api.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class EPSMeanCompareStrategyTest {

    @Autowired
    StockDataService dataService;

    @Test
    void isMeet() {
        Table epsData = dataService.getStockEPSData("1101");
        EPSMeanCompareStrategy epsMeanCompareStrategy = new EPSMeanCompareStrategy(epsData, LocalDateTime.now(ZoneId.systemDefault()), 5, EPSMeanCompareStrategy.CompareOperator.GREATER, 3);
        log.info("isMeet = {}", epsMeanCompareStrategy.isMeet());
    }
}
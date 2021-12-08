package com.leochung0728.quartz.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.tablesaw.api.Table;

import java.time.LocalDate;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class StockDataServiceTest {
    @Autowired
    StockDataService dataService;

    @Test
    void getStockTransactionData() {
        Table table = this.dataService.getStockTransactionData("1101", LocalDate.of(2021, 01, 01), LocalDate.now());
        log.info(table.print(5));
    }
}
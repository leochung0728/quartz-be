package com.leochung0728.quartz.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.tablesaw.api.Table;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class StockDataServiceTest {
    @Autowired
    StockDataService dataService;

    @Test
    void getStockData() {
        Table table = this.dataService.getStockData("1101", LocalDate.of(2021, 01, 01), LocalDate.now());
        table.print(5);
    }
}
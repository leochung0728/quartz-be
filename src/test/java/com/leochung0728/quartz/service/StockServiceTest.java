package com.leochung0728.quartz.service;

import com.leochung0728.quartz.table.Stock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class StockServiceTest {
    @Autowired StockService service;

    @Test
    void findByStockCode() {
        Stock stock = service.findByStockCode("1101");
        System.out.println(stock);
    }

    @Test
    void findByErrCountLessThanEqual() {
        List<Stock> stock = service.findByErrCountLessThanEqual(1);
        log.info(stock.toString());
    }
}
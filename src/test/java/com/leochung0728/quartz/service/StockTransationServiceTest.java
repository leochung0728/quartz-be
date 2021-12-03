package com.leochung0728.quartz.service;

import com.leochung0728.quartz.dao.StockTransationDao;
import com.leochung0728.quartz.table.StockTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class StockTransationServiceTest {
    @Autowired
    StockTransationDao dao;

    @Test
    public void testSave() {
        StockTransaction entity = new StockTransaction("TW0003511002", new Date(), null, null, null, null, null, null);
        dao.save(entity);
    }
}
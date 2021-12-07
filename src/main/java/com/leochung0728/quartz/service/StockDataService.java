package com.leochung0728.quartz.service;

import com.leochung0728.quartz.table.Stock;
import com.leochung0728.quartz.table.StockTransaction;
import com.leochung0728.quartz.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tech.tablesaw.api.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Validated
@Transactional
@Service
public class StockDataService {
    @Autowired
    StockService stockService;

    @Autowired
    StockTransationService transationService;

    public Table getStockData(
            @NotBlank(message = "stockCode should not be blank") String stockCode,
            @NotNull(message = "startDate should not be null") LocalDate startDate,
            @NotNull(message = "endDate should not be null") LocalDate endDate) {
        Stock stock = stockService.findByStockCode(stockCode);
        Assert.notNull(stock, String.format("stock code [%s] is not exist", stockCode));
        List<StockTransaction> transactionData = transationService.findByIsinCodeAndDateBetween(stock.getIsinCode(),
                startDate, endDate, Sort.by("date").ascending());
        log.info("[{}] 交易資料筆數 = {}", stockCode, transactionData.size());

        Table table = Table.create(stockCode);
        List<LocalDateTime> date = new ArrayList<>();
        List<Double> open = new ArrayList<>();
        List<Double> high = new ArrayList<>();
        List<Double> low = new ArrayList<>();
        List<Double> close = new ArrayList<>();
        List<Double> adjClose = new ArrayList<>();
        List<Long> volume = new ArrayList<>();

        for (StockTransaction t : transactionData) {
            date.add(t.getDate() != null ? DateUtils.covertToLocalDateTime(t.getDate()) : null);
            open.add(t.getOpen());
            high.add(t.getHigh());
            low.add(t.getLow());
            close.add(t.getClose());
            adjClose.add(t.getAdjColse());
            volume.add(t.getVolume());
        }
        DateTimeColumn col1 = DateTimeColumn.create("date", date);
        DoubleColumn col2 = DoubleColumn.create("open", open);
        DoubleColumn col3 = DoubleColumn.create("high", high);
        DoubleColumn col4 = DoubleColumn.create("low", low);
        DoubleColumn col5 = DoubleColumn.create("close", close);
        DoubleColumn col6 = DoubleColumn.create("adjClose", adjClose);
        LongColumn col7 = LongColumn.create("volume", volume.stream().mapToLong(Long::longValue));

        return table.addColumns(col1, col2, col3, col4, col5, col6, col7);
    }
}

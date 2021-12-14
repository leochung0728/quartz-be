package com.leochung0728.quartz.service;

import com.leochung0728.quartz.dao.StockTransationDao;
import com.leochung0728.quartz.table.StockTransaction;
import com.leochung0728.quartz.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.StopGainRule;
import org.ta4j.core.trading.rules.StopLossRule;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class StockTransationServiceTest {
    @Autowired
    StockTransationDao dao;

    @Autowired
    StockTransationService service;

    @Test
    public void testSave() {
        StockTransaction entity = new StockTransaction("TW0003511002", new Date(2021,12,3), null, null, null, null, null, null);
        dao.save(entity);
    }

    @Test
    public void test() {
        BarSeries series = new BaseBarSeriesBuilder().withName("0050").build();

        List<StockTransaction> transations = service.findByIsinCode("TW0000050004", Sort.by(Sort.Direction.ASC, "date"));

        for (StockTransaction t : transations) {
            try {
                series.addBar(DateUtils.covertToZonedDateTime(t.getDate()), t.getOpen(), t.getHigh(), t.getLow(), t.getClose(), t.getVolume());
            } catch (Exception e) {
                continue;
            }
        }

        // Getting the close price of the ticks
        Num firstClosePrice = series.getBar(0).getClosePrice();
        System.out.println("First close price: " + firstClosePrice.doubleValue());
        // Or within an indicator:
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Here is the same close price:
        System.out.println(firstClosePrice.isEqual(closePrice.getValue(0))); // equal to firstClosePrice

        // Getting the simple moving average (SMA) of the close price over the last 5 ticks
        SMAIndicator shortSma = new SMAIndicator(closePrice, 5);
        // Here is the 5-ticks-SMA value at the 42nd index
        System.out.println("5-ticks-SMA value at the 42nd index: " + shortSma.getValue(42).doubleValue());

        // Getting a longer SMA (e.g. over the 30 last ticks)
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);

        Rule buyingRule = new CrossedUpIndicatorRule(shortSma, longSma)
                .or(new CrossedDownIndicatorRule(closePrice, 800d));

        Rule sellingRule = new CrossedDownIndicatorRule(shortSma, longSma)
                .or(new StopLossRule(closePrice, 3.0))
                .or(new StopGainRule(closePrice, 2.0));
        org.ta4j.core.Strategy strategy = new BaseStrategy(buyingRule, sellingRule);

        // Running our juicy trading strategy...
        BarSeriesManager manager = new BarSeriesManager(series);
        TradingRecord tradingRecord = manager.run(strategy);
        System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());
        System.out.println(tradingRecord.toString());

        // Getting the profitable trades ratio
        AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
        System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
        // Getting the reward-risk ratio
        AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
        System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));

        // Total profit of our strategy
        // vs total profit of a buy-and-hold strategy
        AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
        System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));
    }
}
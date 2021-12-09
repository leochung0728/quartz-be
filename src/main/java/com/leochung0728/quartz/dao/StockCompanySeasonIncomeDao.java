package com.leochung0728.quartz.dao;

import com.leochung0728.quartz.table.Stock;
import com.leochung0728.quartz.table.StockCompanySeasonIncome;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("StockCompanySeasonIncomeDao")
public interface StockCompanySeasonIncomeDao extends JpaRepository<StockCompanySeasonIncome, StockCompanySeasonIncome.CompositeKeys> {
    List<StockCompanySeasonIncome> findByStockCode(String stockCode, Sort sort);
}
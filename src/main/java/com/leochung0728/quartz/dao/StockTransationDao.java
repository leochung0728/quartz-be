package com.leochung0728.quartz.dao;

import com.leochung0728.quartz.table.key.StockTransactionKeys;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.table.StockTransaction;

import java.util.Date;
import java.util.List;

@Repository("StockTransationDao")
public interface StockTransationDao extends JpaRepository<StockTransaction, StockTransactionKeys> {
    List<StockTransaction> findByIsinCodeAndDateBetween(String isinCode, long startDate, long endDate, Sort sort);
    List<StockTransaction> findByIsinCodeAndDateLessThanEqual(String isinCode, long endDate, Sort sort);
    List<StockTransaction> findByIsinCodeAndDateGreaterThanEqual(String isinCode, long startDate, Sort sort);
    List<StockTransaction> findByIsinCode(String isinCode, Sort sort);
}
package com.leochung0728.quartz.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.table.Stock;

@Repository("StockDao")
public interface StockDao extends JpaRepository<Stock, String> {

	List<Stock> findByStockCode(String stockCode);

}
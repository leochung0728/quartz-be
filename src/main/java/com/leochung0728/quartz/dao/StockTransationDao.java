package com.leochung0728.quartz.dao;

import com.leochung0728.quartz.table.key.StockTransactionKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.table.StockTransaction;

@Repository("StockTransationDao")
public interface StockTransationDao extends JpaRepository<StockTransaction, StockTransactionKeys> {

}
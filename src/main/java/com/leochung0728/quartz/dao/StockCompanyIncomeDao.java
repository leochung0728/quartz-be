package com.leochung0728.quartz.dao;

import com.leochung0728.quartz.table.StockCompanyIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("StockCompanyIncomeDao")
public interface StockCompanyIncomeDao extends JpaRepository<StockCompanyIncome, StockCompanyIncome.CompositeKeys> {

}
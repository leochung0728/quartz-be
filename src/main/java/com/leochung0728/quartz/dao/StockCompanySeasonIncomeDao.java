package com.leochung0728.quartz.dao;

import com.leochung0728.quartz.table.StockCompanySeasonIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("StockCompanySeasonIncomeDao")
public interface StockCompanySeasonIncomeDao extends JpaRepository<StockCompanySeasonIncome, StockCompanySeasonIncome.CompositeKeys> {

}
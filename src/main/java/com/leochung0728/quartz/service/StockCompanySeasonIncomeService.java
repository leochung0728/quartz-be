package com.leochung0728.quartz.service;

import com.leochung0728.quartz.dao.StockCompanySeasonIncomeDao;
import com.leochung0728.quartz.table.StockCompanySeasonIncome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class StockCompanySeasonIncomeService {
	@Autowired
	StockCompanySeasonIncomeDao stockCompanySeasonIncomeDao;
	
	public List<StockCompanySeasonIncome> findAll() {
		return stockCompanySeasonIncomeDao.findAll();
	}
	
	@Transactional
	public List<StockCompanySeasonIncome> saveAll(List<StockCompanySeasonIncome> stockCompanySeasonIncome) {
		return stockCompanySeasonIncomeDao.saveAll(stockCompanySeasonIncome);
	}
}

package com.leochung0728.quartz.service;

import com.leochung0728.quartz.dao.StockCompanyIncomeDao;
import com.leochung0728.quartz.dao.StockTransationDao;
import com.leochung0728.quartz.table.StockCompanyIncome;
import com.leochung0728.quartz.table.StockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class StockCompanyIncomeService {
	@Autowired
	StockCompanyIncomeDao stockCompanyIncomeDao;
	
	public List<StockCompanyIncome> findAll() {
		return stockCompanyIncomeDao.findAll();
	}
	
	@Transactional
	public List<StockCompanyIncome> saveAll(List<StockCompanyIncome> stockCompanyIncome) {
		return stockCompanyIncomeDao.saveAll(stockCompanyIncome);
	}
}

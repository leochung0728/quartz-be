package com.leochung0728.quartz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leochung0728.quartz.dao.StockTransationDao;
import com.leochung0728.quartz.table.StockTransaction;

@Transactional(readOnly = true)
@Service
public class StockTransationService {
	@Autowired
	StockTransationDao stockTransationDao;
	
	public List<StockTransaction> findAll() {
		return stockTransationDao.findAll();
	}
	
	@Transactional
	public List<StockTransaction> saveAll(List<StockTransaction> stockTransations) {
		return stockTransationDao.saveAll(stockTransations);
	}
}

package com.leochung0728.quartz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leochung0728.quartz.dao.StockDao;
import com.leochung0728.quartz.table.Stock;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Transactional(readOnly = true)
@Service
public class StockService {
	@Autowired
	StockDao stockDao;
	
	public List<Stock> findAll() {
		return stockDao.findAll();
	}
	
	@Transactional
	public List<Stock> saveAll(List<Stock> stocks) {
		return stockDao.saveAll(stocks);
	}
}

package com.leochung0728.quartz.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leochung0728.quartz.dao.StockDao;
import com.leochung0728.quartz.table.Stock;

@Service
@Transactional(readOnly = true)
public class StockService {
	@Autowired
	StockDao stockDao;
	
	public List<Stock> findAll() {
		return stockDao.findAll();
	}
	
	public List<Stock> findByErrCountLessThanEqual(int errCount) {
		return stockDao.findByErrCountLessThanEqual(errCount);
	}
	
	@Transactional
	public void flush() {
		stockDao.flush();
	}
	
	@Transactional
	public Stock save(Stock stock) {
		return stockDao.save(stock);
	}
	
	@Transactional
	public Stock save(Stock stock, int maxRetry) {
		Stock result = null;
		boolean succ = false;
		int retry = 1;
		while (!succ) {
			try {
				result = stockDao.save(stock);
				succ = true;
			} catch (OptimisticLockingFailureException e) {
				retry += 1;
				if (retry > maxRetry) {
					throw e;
				}
			}
		}
		return result;
	}
	
	@Transactional
	public Stock increaseErrCount(Stock stock, int maxRetry) {
		Stock result = null;
		boolean succ = false;
		int retry = 1;
		while (!succ) {
			try {
				Stock entity = stockDao.findById(stock.getIsinCode()).orElseThrow(EntityNotFoundException::new);
				stock.setErrCount(entity.getErrCount() + 1);
				stock.setVersion(entity.getVersion());
				result = stockDao.save(stock);
				succ = true;
			} catch (OptimisticLockingFailureException e) {
				retry += 1;
				if (retry > maxRetry) {
					throw e;
				}
			}
		}
		return result;
	}
	
	@Transactional
	public List<Stock> saveAll(List<Stock> stocks) {
		return stockDao.saveAll(stocks);
	}
}

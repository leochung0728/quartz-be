package com.leochung0728.quartz.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

	public List<StockTransaction> findByIsinCodeAndDateBetween(@NotNull String isinCode, @NotNull LocalDate startDate, @NotNull LocalDate endDate, Sort sort) {
		sort = sort == null ? Sort.unsorted() : sort;
		return this.stockTransationDao.findByIsinCodeAndDateBetween(isinCode, startDate.toEpochDay(), endDate.toEpochDay(), sort);
	}

	public List<StockTransaction> findByIsinCodeAndDateLessThanEqual(@NotNull String isinCode, @NotNull LocalDate startDate, Sort sort) {
		sort = sort == null ? Sort.unsorted() : sort;
		return this.stockTransationDao.findByIsinCodeAndDateLessThanEqual(isinCode, startDate.toEpochDay(), sort);
	}

	public List<StockTransaction> findByIsinCodeAndDateGreaterThanEqual(@NotNull String isinCode, @NotNull LocalDate endDate, Sort sort) {
		sort = sort == null ? Sort.unsorted() : sort;
		return this.stockTransationDao.findByIsinCodeAndDateGreaterThanEqual(isinCode, endDate.toEpochDay(), sort);
	}

	public List<StockTransaction> findByIsinCode(@NotNull String isinCode, Sort sort) {
		sort = sort == null ? Sort.unsorted() : sort;
		return this.stockTransationDao.findByIsinCode(isinCode, sort);
	}
}

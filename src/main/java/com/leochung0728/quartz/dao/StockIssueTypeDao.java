package com.leochung0728.quartz.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.table.StockIssueType;

@Repository("StockIssueTypeDao")
public interface StockIssueTypeDao extends JpaRepository<StockIssueType, String> {

}
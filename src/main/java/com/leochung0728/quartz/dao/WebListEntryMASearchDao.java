package com.leochung0728.quartz.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.table.WebListEntryMASearch;

@Repository("WebListEntryMASearchDao")
public interface WebListEntryMASearchDao extends JpaRepository<WebListEntryMASearch, String> {
	
	List<WebListEntryMASearch> findByName(String name);
	
}

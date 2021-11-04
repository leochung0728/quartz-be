package com.leochung0728.quartz.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.table.JobHist;


@Repository("JobHistDao")
public interface JobHistDao extends JpaRepository<JobHist, Long> {

	List<JobHist> findByjobGroupAndJobName(String jobGroup, String jobName, Sort sort);

}
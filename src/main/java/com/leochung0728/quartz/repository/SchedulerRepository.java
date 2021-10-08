package com.leochung0728.quartz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leochung0728.quartz.entity.SchedulerJobInfo;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfo, Long> {

	SchedulerJobInfo findByJobName(String jobName);

}

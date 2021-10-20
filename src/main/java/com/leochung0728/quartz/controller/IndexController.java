package com.leochung0728.quartz.controller;

import java.util.ArrayList;
import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.leochung0728.quartz.entity.SchedulerJobInfo;
import com.leochung0728.quartz.service.SchedulerJobService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IndexController {
	@Autowired
	private SchedulerJobService scheduleJobService;

	@GetMapping("/index")
	public String index(Model model) {
		List<SchedulerJobInfo> jobList = new ArrayList<SchedulerJobInfo>();
		try {
			jobList = scheduleJobService.getAllJobList();
		} catch (SchedulerException e) {
			log.error("[index] error", e);
		}
		model.addAttribute("jobs", jobList);
		return "index";
	}

}
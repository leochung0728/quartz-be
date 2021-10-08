package com.leochung0728.quartz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.leochung0728.quartz.entity.SchedulerJobInfo;
import com.leochung0728.quartz.service.SchedulerJobService;

@Controller
public class IndexController {

	@Autowired
	private SchedulerJobService scheduleJobService;

	@GetMapping("/index")
	public String index(Model model) {
		List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
		model.addAttribute("jobs", jobList);
		return "index";
	}

}
package com.ashkan.jira;

import com.ashkan.jira.service.JiraService;
import com.ashkan.jira.service.SprintReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientMain {
	private SprintReport sprintReport;

	@Autowired
	public ClientMain(SprintReport sprintReport) {
		this.sprintReport = sprintReport;
	}

	@PostConstruct
	private void initialize() {
//		jiraService.getDetailsForTicket();
		sprintReport.generateSprintReport(923L);
	}

}

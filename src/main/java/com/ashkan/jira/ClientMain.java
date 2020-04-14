package com.ashkan.jira;

import com.ashkan.jira.service.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientMain {
	JiraService jiraService;

	@Autowired
	public ClientMain(JiraService jiraService) {
		this.jiraService = jiraService;
	}

	@PostConstruct
	private void initialize() {
		jiraService.searchWithJql();
	}

}

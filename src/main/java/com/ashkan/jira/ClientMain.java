package com.ashkan.jira;

import com.ashkan.jira.service.JiraService;
import com.ashkan.jira.service.SprintReport;
import com.ashkan.jira.sheets.SheetsUpdater;

import java.io.IOException;
import java.security.GeneralSecurityException;

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

		final String spreadsheetId = "17guP0zy95DkAGKhEpExeNUyjMNB_BoJnNIy3yeothMU";
		final SheetsUpdater updater = new SheetsUpdater(spreadsheetId);
		try {
			updater.updateSprintReport(sprintReport);
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
	}

}

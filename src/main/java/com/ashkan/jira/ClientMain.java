package com.ashkan.jira;

import com.ashkan.jira.service.SprintReport;
import com.ashkan.jira.sheets.SheetsUpdater;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientMain {
	private SprintReport sprintReport;

	@Value("${google_sheet_id}")
	private String spreadSheetId;

	@Autowired
	public ClientMain(SprintReport sprintReport) {
		this.sprintReport = sprintReport;
	}

	@PostConstruct
	private void initialize() {
		sprintReport.generateSprintReport(923L);

		final SheetsUpdater updater = new SheetsUpdater(spreadSheetId);
		try {
			updater.updateSprintReport(sprintReport);
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
	}

}

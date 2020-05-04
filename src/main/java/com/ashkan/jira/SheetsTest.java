package com.ashkan.jira;

import com.ashkan.jira.service.SprintReport;
import com.ashkan.jira.sheets.SheetsUpdater;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SheetsTest {
	public static void main(String... args) throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		
		final String spreadsheetId = "17guP0zy95DkAGKhEpExeNUyjMNB_BoJnNIy3yeothMU";
		
		SprintReport report = new SprintReport(null);
		report.setCompletedStoryPoints(100);
		report.setIncomplete(20);
		report.setCommitted(110);
		report.setInflated(5);
		report.setInjected(5);
		report.setRemoved(5);

		SheetsUpdater updater = new SheetsUpdater(spreadsheetId);
		updater.updateSprintReport(report);
	}
}
package com.ashkan.jira;

import com.ashkan.jira.service.SprintReport;
import com.ashkan.jira.sheets.SheetsUpdater;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientMain {
	private Map<String, List<Long>> projectToListOfSprintIds = new HashMap<>();
	private SprintReport sprintReport;

	@Value("${google_sheet_id}")
	private String spreadSheetId;

	@Autowired
	public ClientMain(SprintReport sprintReport) {
		this.sprintReport = sprintReport;
	}

	@PostConstruct
	private void initialize() {
		generateAndPostAllSprintReports();
	}

	private void generateAndPostAllSprintReports() {
		setProjectMapValues();
		for (Map.Entry<String, List<Long>> entry : projectToListOfSprintIds.entrySet()) {
			String projectCode = entry.getKey();
			entry.getValue().forEach(sprintId -> {
				sprintReport.generateSprintReport(projectCode, sprintId);

				final SheetsUpdater updater = new SheetsUpdater(spreadSheetId);
				try {
					updater.updateSprintReport(sprintReport);
				} catch (GeneralSecurityException | IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void setProjectMapValues() {
		projectToListOfSprintIds.put("OREF", Arrays.asList(907L, 921L, 922L, 923L, 924L, 925L));
	}

}

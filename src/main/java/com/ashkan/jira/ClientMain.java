package com.ashkan.jira;

import com.ashkan.jira.service.JiraService;
import com.ashkan.jira.service.SprintReport;
import com.ashkan.jira.sheets.SheetsUpdater;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientMain {
	private Map<String, List<Long>> projectToListOfSprintIds = new HashMap<>();
	private Map<String, List<Long>> listOfComponentIds =new HashMap<>();
	private SprintReport sprintReport;
	private JiraService jiraService;

	@Value("${google_sheet_id}")
	private String spreadSheetId;

	@Autowired
	public ClientMain(SprintReport sprintReport, JiraService jiraService) {
		this.sprintReport = sprintReport;
		this.jiraService = jiraService;
	}

	@PostConstruct
	private void initialize() {
//		generateAndPostAllSprintReports();
		jiraService.authenticate();
	}

	private void generateAndPostAllSprintReports() {
		setProjectMapValues();
		for (Map.Entry<String, List<Long>> entry : listOfComponentIds.entrySet()) {
			String projectCode = entry.getKey();
			entry.getValue().forEach(sprintId -> {
				sprintReport.generateSprintReport(projectCode, sprintId);

				final SheetsUpdater updater = new SheetsUpdater(spreadSheetId);
				try {
					updater.updateSprintReport(sprintReport, projectCode);
				} catch (GeneralSecurityException | IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void hardCodeSprintIds() {
		projectToListOfSprintIds.put("OREF", Arrays.asList(907L, 921L, 922L, 923L, 924L, 925L));
		projectToListOfSprintIds.put("OEXP", Arrays.asList(915L, 916L, 917L, 918L, 919L, 920L));
	}

	private void setProjectMapValues() {
		listOfComponentIds.put("DMS & Calcs", jiraService.getSprintIdsForGivenBoardId(231L));
		listOfComponentIds.put("BUX", jiraService.getSprintIdsForGivenBoardId(227L));
		listOfComponentIds.put("VQL & DE", jiraService.getSprintIdsForGivenBoardId(236L));
		listOfComponentIds.put("Sundae", jiraService.getSprintIdsForGivenBoardId(235L));
		listOfComponentIds.put("Office Experience", jiraService.getSprintIdsForGivenBoardId(225L));
		listOfComponentIds.put("Office Refresh", jiraService.getSprintIdsForGivenBoardId(226L));
	}
}

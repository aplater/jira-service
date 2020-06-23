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
		//jiraService.getComponentValues("VDP");
		//jiraService.getBoardIdAndSprintId("VDP", projectToListOfSprintIds);
		generateAndPostAllSprintReports();
//		//jiraService.getSprintDetailsForBoards();
//		jiraService.getSprintDetailsForBoards(225);
	}

	private void generateAndPostAllSprintReports() {
		setProjectMapValues2();
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

	private void setProjectMapValues() {
		projectToListOfSprintIds.put("OREF", Arrays.asList(907L, 921L, 922L, 923L, 924L, 925L));
		projectToListOfSprintIds.put("OEXP", Arrays.asList(915L, 916L, 917L, 918L, 919L, 920L));
	}

	private void setProjectMapValues2() {
		listOfComponentIds.put("DMS & Calcs Board 231", jiraService.getSprintDetailsForBoards(231L));
		listOfComponentIds.put("BUX Board 227", jiraService.getSprintDetailsForBoards(227L));
		listOfComponentIds.put("VQL & DE Board 236", jiraService.getSprintDetailsForBoards(236L));
		listOfComponentIds.put("Sundae Board Board 235", jiraService.getSprintDetailsForBoards(235L));
		listOfComponentIds.put("Office Experience 225", jiraService.getSprintDetailsForBoards(225L));
		listOfComponentIds.put("Office Refresh 226", jiraService.getSprintDetailsForBoards(226L));
	}
}

package com.ashkan.jira.sheets;

import com.ashkan.jira.util.JiraTime;
import lombok.RequiredArgsConstructor;

import com.ashkan.jira.service.SprintReport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ashkan.jira.sheets.HeaderDetector.*;

@RequiredArgsConstructor
public class SheetsUpdater {

	private static final String RANGE = "OREF!A:M";
	private final String spreadsheetId;

	public boolean updateSprintReport(SprintReport report) throws GeneralSecurityException, IOException {
		HeaderDetector detector = new HeaderDetector(spreadsheetId);
		Map<Integer, String> columnIndicesToHeaders = detector.getHeaders(RANGE);

		List<Object> row = buildRow(report, columnIndicesToHeaders);

		return appendRow(row, spreadsheetId, RANGE);
	}

	List<Object> buildRow(SprintReport report, Map<Integer, String> columnIndicesToHeaders) {
		// why a List<Object> you ask? BECAUSE THE GOOGLE SHEETS JAVA API IS BAD, THAT'S WHY
		List<Object> row = new ArrayList<>();

		for (Map.Entry<Integer, String> loc : columnIndicesToHeaders.entrySet()) {
			Integer colIdx = loc.getKey();
			String type = loc.getValue();

			Object value;
			switch (type) {
				case HEADER_SPRINT_NAME:
					value = report.getCurrentSprint().getName();
					break;
				case HEADER_START_DATE:
					value = JiraTime.getFormattedString(report.getCurrentSprint().getStart());
					break;
				case HEADER_END_DATE:
					value = JiraTime.getFormattedString(report.getCurrentSprint().getEnd());
					break;
				case HEADER_COMMITTED:
					value = report.getCommittedStoryPoints();
					break;
				case HEADER_INJECTED:
					value = report.getInjectedStoryPoints();
					break;
				case HEADER_INFLATED:
					value = report.getInflatedStoryPoints();
					break;
				case HEADER_DEFLATED:
					value = report.getDeflatedStoryPoints();
					break;
				case HEADER_REMOVED:
					value = report.getRemoved();
					break;
				case HEADER_COMPLETED:
					value = report.getCompletedStoryPoints();
					break;
				case HEADER_INCOMPLETE:
					value = report.getIncompleteStoryPoints();
					break;
				default:
					throw new RuntimeException("Unknown header: " + type);
			}

			if (row.size() <= colIdx) {
				padList(row, colIdx);
			}

			row.set(colIdx, value);
		}
		return row;
	}

	private void padList(List<Object> row, Integer length) {
		while (row.size() <= length) {
			row.add("");
		}
	}

	public boolean appendRow(List<Object> row, String spreadsheetId, String range) throws GeneralSecurityException,
			IOException {
		ValueRange requestBody = new ValueRange();
		requestBody.setValues(Collections.singletonList(row));

		Sheets.Spreadsheets.Values.Append request = SheetsClient.build().spreadsheets().values().append(spreadsheetId,
				range, requestBody);

		// How the input data should be interpreted.
		request.setValueInputOption("USER_ENTERED");  // vs. "RAW"
		// How the input data should be inserted.
		request.setInsertDataOption("INSERT_ROWS");

		AppendValuesResponse response = request.execute();

		System.out.println(response);

		return true;
	}
}

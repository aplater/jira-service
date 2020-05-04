package com.ashkan.jira.sheets;

import lombok.RequiredArgsConstructor;

import com.ashkan.jira.service.SprintReport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ashkan.jira.sheets.HeaderDetector.HEADER_COMMITED;
import static com.ashkan.jira.sheets.HeaderDetector.HEADER_COMPLETED;
import static com.ashkan.jira.sheets.HeaderDetector.HEADER_INCOMPLETE;
import static com.ashkan.jira.sheets.HeaderDetector.HEADER_INFLATED;
import static com.ashkan.jira.sheets.HeaderDetector.HEADER_INJECTED;
import static com.ashkan.jira.sheets.HeaderDetector.HEADER_REMOVED;

@RequiredArgsConstructor
public class SheetsUpdater {

	private static final String RANGE = "Sheet1!A:V";
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

		for (Map.Entry<Integer, String> loc: columnIndicesToHeaders.entrySet()) {
			Integer colIdx = loc.getKey();
			String type = loc.getValue();

			int value;
			switch (type) {
				case HEADER_COMMITED:   value = report.getCommitted(); break;
				case HEADER_INJECTED:   value = report.getInjected(); break;
				case HEADER_INFLATED:   value = report.getInflated(); break;
				case HEADER_REMOVED:    value = report.getRemoved(); break;
				case HEADER_COMPLETED:  value = report.getCompletedStoryPoints(); break;
				case HEADER_INCOMPLETE: value = report.getIncomplete(); break;
				default: throw new RuntimeException("Unknown header: " + type);
			}

			if (row.size() <= colIdx) {
				padList(row, colIdx);
			}
			row.set(colIdx, Integer.toString(value));
		}
		return row;
	}

	private void padList(List<Object> row, Integer length) {
		while (row.size() <= length) {
			row.add("");
		}
	}

	public boolean appendRow(List<Object> row, String spreadsheetId, String range) throws GeneralSecurityException, IOException {
		ValueRange requestBody = new ValueRange();
		requestBody.setValues(Collections.singletonList(row));

		Sheets.Spreadsheets.Values.Append request = SheetsClient.build().spreadsheets()
				.values()
				.append(spreadsheetId, range, requestBody);

		// How the input data should be interpreted.
		request.setValueInputOption("USER_ENTERED");  // vs. "RAW"
		// How the input data should be inserted.
		request.setInsertDataOption("INSERT_ROWS");

		AppendValuesResponse response = request.execute();

		// TODO: Change code below to process the `response` object:
		System.out.println(response);
		
		return true;
	}
}

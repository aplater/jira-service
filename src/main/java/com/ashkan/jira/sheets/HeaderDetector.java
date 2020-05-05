package com.ashkan.jira.sheets;

import lombok.RequiredArgsConstructor;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class HeaderDetector {
	public static final String HEADER_COMMITTED  = "Committed";
	public static final String HEADER_INJECTED   = "Injected";
	public static final String HEADER_INFLATED   = "Inflated";
	public static final String HEADER_DEFLATED   = "Deflated";
	public static final String HEADER_REMOVED    = "Removed";
	public static final String HEADER_COMPLETED  = "Completed";
	public static final String HEADER_INCOMPLETE = "Incomplete";
	public static final Set<String> ALL_HEADERS = new HashSet<>(Arrays.asList(HEADER_COMMITTED, HEADER_INJECTED, HEADER_INFLATED, HEADER_DEFLATED, HEADER_REMOVED, HEADER_COMPLETED, HEADER_INCOMPLETE));

	private final String spreadsheetId;

	public Map<Integer, String> getHeaders(String range) throws GeneralSecurityException, IOException {
		Sheets service = SheetsClient.build();
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();

		Map<Integer, String> columnNumbersToHeaders = new HashMap<>();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			System.out.println("Found headers");
			List<Object> topRow = values.get(0);
			int pos = 0;
			for (Object cell: topRow) {
				String contents = ((String) cell).trim();
				if (ALL_HEADERS.contains(contents)) {
					columnNumbersToHeaders.put(pos, contents);
				}
				pos++;
			}
		}
		return columnNumbersToHeaders;
	}
}

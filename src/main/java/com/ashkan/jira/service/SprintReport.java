package com.ashkan.jira.service;

import com.ashkan.jira.model.Sprint;
import com.ashkan.jira.util.JiraTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;

@Component
public class SprintReport {
	private int completed = 0;
	private int completedStoryPoints = 0;
	private int injected = 0;
	private int injectedStoryPoints = 0;
	private int committed = 0;
	private int inflated = 0;
	private int removed = 0;
	private int incomplete = 0;
	private int incompleteStoryPoints = 0;

	private final JiraService jiraService;

	@Autowired
	public SprintReport(JiraService jiraService) {
		this.jiraService = jiraService;
	}

	public void generateSprintReport(Long sprintId) {
		JSONObject sprintTicketsJson = jiraService.getIssuesOfSprint(sprintId);

		for (Object issue : sprintTicketsJson.getJSONArray("issues")) {
			JSONObject jsonIssue = (JSONObject) issue;
			Sprint currentSprint = getCurrentSprint(jsonIssue, sprintId);
			if (isIssueComplete(jsonIssue, currentSprint)) {
				completed++;
				completedStoryPoints += getStoryPoints(jsonIssue);
				System.out.println("Completed Ticket No:");
				System.out.println(jsonIssue.getString("key"));
			} else {
				incomplete++;
				incompleteStoryPoints += getStoryPoints(jsonIssue);
				System.out.println("Incomplete Ticket No:");
				System.out.println(jsonIssue.getString("key"));
			}

			if (isIssueInjected(jsonIssue, currentSprint)) {
				injected++;
				injectedStoryPoints += getStoryPoints(jsonIssue);
				System.out.println("Injected Ticket No:");
				System.out.println(jsonIssue.getString("key"));
			}
		}

		printSprintReport();
	}

	private Sprint getCurrentSprint(JSONObject jsonIssue, Long sprintId) {
		JSONObject jsonSprint;
		try {
			jsonSprint = jsonIssue.getJSONObject("fields").getJSONObject("sprint");
			Sprint retrievedSprint = new Sprint(jsonSprint);
			if (!retrievedSprint.isFutureSprint() && retrievedSprint.getId().equals(sprintId)) {
				return retrievedSprint;
			} else {
				return getLatestClosedSprint(jsonIssue, sprintId);
			}
		} catch (Exception exc) {
			// Sprint field is null since sprint is completed, get "last closed sprint":
			return getLatestClosedSprint(jsonIssue, sprintId);
		}
	}

	private Sprint getLatestClosedSprint(JSONObject jsonIssue, Long sprintId) {
		JSONObject jsonSprint = new JSONObject();
		JSONArray closedSprints = jsonIssue.getJSONObject("fields").getJSONArray("closedSprints");
		for (int i = closedSprints.length() - 1; i >= 0; i--) {
			jsonSprint = (JSONObject) closedSprints.get(i);
			try {
				if (jsonSprint.getLong("id") == sprintId) {
					return new Sprint(jsonSprint);
				}
			} catch (Exception exp) {
				System.out.println("jsonSprint is: " + jsonSprint);
			}
		}
		return new Sprint(jsonSprint);
	}

	private int getStoryPoints(JSONObject issue) {
		int storyPoints = 0;
		try {
			storyPoints = issue.getJSONObject("fields").getInt("customfield_10200");
		} catch (JSONException e) {
			// Ticket has no story points (field value is null)
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
		}
		return storyPoints;
	}

	public boolean isIssueComplete(JSONObject issue, Sprint currentSprint) {
//		return issue.getJSONObject("fields").getJSONObject("status").getString("name").equals("Done");
		JSONArray changelog = issue.getJSONObject("changelog").getJSONArray("histories");
		for (Object log : changelog) {
			JSONArray items = ((JSONObject)log).getJSONArray("items");
			Instant logCreatedDate = JiraTime.getInstant(((JSONObject)log).getString("created"));
			for (Object item : items) {
				JSONObject jsonItem = (JSONObject) item;
				try {
					if (jsonItem.getString("field").equals("resolution") &&
							(jsonItem.getString("toString").contains("Done") ||
									jsonItem.getString("toString").contains("Code Complete") ||
									jsonItem.getString("toString").contains("Fixed")) && // TODO: include all possible resolutions
							logCreatedDate.isAfter(currentSprint.getStart()) &&
							logCreatedDate.isBefore(currentSprint.getEnd().plus(Duration.ofDays(2).plusHours(12))) // for the tickets we close over the weekend of 2nd week
					) {
						return true;
					}
				} catch (Exception excp) {
					System.out.println("Failed jsonItem is: " + jsonItem.toString());
				}
			}
		}
		return false;
	}

	public boolean isIssueInjected(JSONObject jsonIssue, Sprint currentSprint) {
		JSONArray changelog = jsonIssue.getJSONObject("changelog").getJSONArray("histories");
		Instant issueCreated = JiraTime.getInstant(jsonIssue.getJSONObject("fields").getString("created"));
		for (Object log : changelog) {
			JSONArray items = ((JSONObject)log).getJSONArray("items");
			Instant logCreatedDate = JiraTime.getInstant(((JSONObject)log).getString("created"));
			for (Object item : items) {
				JSONObject jsonItem = (JSONObject) item;
				try {
					if (issueCreated.compareTo(currentSprint.getStart()) > 0 ||
						(jsonItem.getString("field").equals("Sprint") &&
						jsonItem.getString("to").contains(currentSprint.getId().toString()) &&
						logCreatedDate.isAfter(currentSprint.getStart()) &&
						logCreatedDate.isBefore(currentSprint.getEnd()))) {
						return true;
					}
				} catch (JSONException e) {
					System.out.println("NO:");
					System.out.println(jsonItem.toString());
				}
			}
		}
		return false;
	}

	private void printSprintReport() {
		System.out.println("Completed issues: " + completed);
		System.out.println("Completed story points: " + completedStoryPoints);
		System.out.println("Incomplete issues: " + incomplete);
		System.out.println("Incomplete story points: " + incompleteStoryPoints);
		System.out.println("Injected stories: " + injected);
		System.out.println("Injected story points: " + injectedStoryPoints);
	}
}

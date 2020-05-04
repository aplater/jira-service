package com.ashkan.jira.service;

import com.ashkan.jira.Sprint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

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
				return getLatestClosedSprint(jsonIssue);
			}
		} catch (Exception exc) {
			// Sprint field is null since sprint is completed, get "last closed sprint":
			return getLatestClosedSprint(jsonIssue);
		}
	}

	private Sprint getLatestClosedSprint(JSONObject jsonIssue) {
		JSONArray closedSprints = jsonIssue.getJSONObject("fields").getJSONArray("closedSprints");
		JSONObject jsonSprint = (JSONObject) closedSprints.get(closedSprints.length() - 1);
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
			String logCreatedDate = ((JSONObject)log).getString("created");
			for (Object item : items) {
				JSONObject jsonItem = (JSONObject) item;
				if (jsonItem.getString("field").equals("resolution") &&
					(jsonItem.getString("toString").contains("Done") || jsonItem.getString("toString").contains("Code Complete")) &&
					logCreatedDate.compareTo(currentSprint.getStart()) > 0 &&
					logCreatedDate.compareTo(currentSprint.getEnd()) < 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isIssueInjected(JSONObject jsonIssue, Sprint currentSprint) {
		JSONArray changelog = jsonIssue.getJSONObject("changelog").getJSONArray("histories");
		String issueCreated = jsonIssue.getJSONObject("fields").getString("created");
		for (Object log : changelog) {
			JSONArray items = ((JSONObject)log).getJSONArray("items");
			String createdDate = ((JSONObject)log).getString("created");
			for (Object item : items) {
				JSONObject jsonItem = (JSONObject) item;
				try {
					if (issueCreated.compareTo(currentSprint.getStart()) > 0 ||
						(jsonItem.getString("field").equals("Sprint") &&
						jsonItem.getString("to").contains(currentSprint.getId().toString()) &&
						createdDate.compareTo(currentSprint.getStart()) > 0 &&
						createdDate.compareTo(currentSprint.getEnd()) < 0)) {
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
		System.out.println("Injected stories: " + injected);
		System.out.println("Injected story points: " + injectedStoryPoints);
	}
}

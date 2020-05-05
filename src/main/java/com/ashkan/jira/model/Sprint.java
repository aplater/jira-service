package com.ashkan.jira.model;

import com.ashkan.jira.util.JiraTime;
import lombok.Getter;
import org.json.JSONObject;

import java.time.Instant;

@Getter
public class Sprint {
	private Long id;
	private String name;
	private String state;
	private Instant start;
	private Instant end;

	public Sprint(JSONObject jsonSprint) {
		this.id = jsonSprint.getLong("id");
		this.name = jsonSprint.getString("name");
		this.state = jsonSprint.getString("state");
		if (!this.state.equals("future")) {
			this.start = JiraTime.getInstant(jsonSprint.getString("startDate"));
			this.end = JiraTime.getInstant(jsonSprint.getString("endDate"));
		}
	}

	public boolean isFutureSprint() {
		return this.state.equals("future");
	}
}

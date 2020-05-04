package com.ashkan.jira.model;

import com.ashkan.jira.util.JiraTime;
import lombok.Getter;
import org.json.JSONObject;

import java.time.Duration;
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
			this.start = JiraTime.getInstant(jsonSprint.getString("startDate")).minus(Duration.ofHours(3)); // convert it from GMT to EST
			this.end = JiraTime.getInstant(jsonSprint.getString("endDate")).minus(Duration.ofHours(3)); // convert it from GMT to EST
		}
	}

	public boolean isFutureSprint() {
		return this.state.equals("future");
	}
}

package com.ashkan.jira;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class Sprint {
	private Long id;
	private String name;
	private String state;
	private String start;
	private String end;

	public Sprint(JSONObject jsonSprint) {
		this.id = jsonSprint.getLong("id");
		this.name = jsonSprint.getString("name");
		this.state = jsonSprint.getString("state");
		if (!this.state.equals("future")) {
			this.start = jsonSprint.getString("startDate");
			this.end = jsonSprint.getString("endDate");
		}
	}

	public boolean isFutureSprint() {
		return this.state.equals("future");
	}
}

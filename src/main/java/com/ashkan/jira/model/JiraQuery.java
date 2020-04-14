package com.ashkan.jira.model;

import lombok.Data;

import java.util.List;

@Data
public class JiraQuery {
	private String jql;
	private List<String> fields;
	private int maxResults;
	private int startAt;
}

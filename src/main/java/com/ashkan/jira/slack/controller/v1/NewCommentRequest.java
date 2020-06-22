package com.ashkan.jira.slack.controller.v1;

import lombok.Data;

@Data
public class NewCommentRequest {
	private String ticketName;
	private String comment;
}

package com.ashkan.jira.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SlackSlashCommand {
	private String command;
	private String token;
	private String text;
	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("user_name")
	private String userName;
}

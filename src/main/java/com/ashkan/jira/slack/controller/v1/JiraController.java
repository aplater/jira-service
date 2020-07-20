package com.ashkan.jira.slack.controller.v1;

import com.ashkan.jira.model.FormSlackSlashCommand;
import com.ashkan.jira.service.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/slack/events")
public class JiraController {

	private final JiraService jiraService;

	@Autowired
	public JiraController(JiraService jiraService) {
		this.jiraService = jiraService;
	}

	@PostMapping("/")
	public String testMe() {
		return "Hello! :wave:";
	}

	@PostMapping(value = "/wave", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	FormSlackSlashCommand testSlack(@ModelAttribute FormSlackSlashCommand slackSlashCommand) {
		return slackSlashCommand;
	}

	@PostMapping("/comment")
	public ResponseEntity<String> postComment(@RequestBody NewCommentRequest newCommentRequest) {
		Optional<Exception> optionalResponse = jiraService.addComment(newCommentRequest.getTicketName(), newCommentRequest.getComment());
		if (optionalResponse.isPresent()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} else {
			return ResponseEntity.ok("Comment posted.");
		}
	}
}

package com.ashkan.jira.slack.controller.v1;

import com.ashkan.jira.service.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/jira")
public class JiraController {

	private final JiraService jiraService;

	@Autowired
	public JiraController(JiraService jiraService) {
		this.jiraService = jiraService;
	}

	@PostMapping("/comment")
	public void postComment(@RequestBody NewCommentRequest newCommentRequest) {
		jiraService.addComment(newCommentRequest.getTicketName(), newCommentRequest.getComment());
	}
}

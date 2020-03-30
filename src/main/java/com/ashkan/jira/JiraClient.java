package com.ashkan.jira;

import com.ashkan.jira.auth.OAuthClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;

@Component
public class JiraClient {

//	@Value("${jira.username}")
//	private String username;
//
//	@Value("${jira.password}")
//	private String password;
//
//	@Value("${jira.url}")
//	private String jiraUrl;
//
//	private JiraRestClient jiraRestClient;
//
//	private JiraRestClient getJiraRestClient() {
//		return new AsynchronousJiraRestClientFactory()
//				.createWithBasicHttpAuthentication(getJiraUri(), this.username, this.password);
//	}
//
//	private URI getJiraUri() {
//		return URI.create(this.jiraUrl);
//	}
//
	private OAuthClient oAuthClient;

	@Autowired
	public JiraClient(OAuthClient oAuthClient) {
		this.oAuthClient = oAuthClient;
	}

	@PostConstruct
	private void initialize() {
		oAuthClient.authenticate();
	}

}

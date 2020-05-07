package com.ashkan.jira.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OAuthClient {

	@Autowired
	private JiraOAuthClient jiraOAuthClient;

	@Getter
	private String verificationCode;

	@Getter
	private String accessToken;

	public Optional<Exception> authenticate() {
		String requestToken;

		if (verificationCode == null) {
			try {
				requestToken = jiraOAuthClient.getAndAuthorizeTemporaryToken();
				verificationCode = jiraOAuthClient.getVerificationCodeFromUser();
				accessToken = jiraOAuthClient.getAccessToken(requestToken, verificationCode);
			} catch (Exception ex) {
				ex.printStackTrace();
				return Optional.of(ex);
			}
		}
		return Optional.empty();
	}
}

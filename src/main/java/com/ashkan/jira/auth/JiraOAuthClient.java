package com.ashkan.jira.auth;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

@Component
public class JiraOAuthClient {

	@Value("${jira.home}plugins/servlet/oauth/authorize")
	private String authorizationUrl;

	@Value("${consumer_key}")
	private String consumerKey;

	@Value("${private_key}")
	private String privateKey;

	private JiraOAuthTokenFactory jiraOAuthTokenFactory;

	@Autowired
	public JiraOAuthClient(JiraOAuthTokenFactory jiraOAuthTokenFactory) {
		this.jiraOAuthTokenFactory = jiraOAuthTokenFactory;
	}

	/**
	 * Gets temporary request token and creates url to authorize it
	 *
	 * @return request token value
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public String getAndAuthorizeTemporaryToken() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		JiraOAuthGetTemporaryToken temporaryToken = jiraOAuthTokenFactory.getTemporaryToken(consumerKey, privateKey);
		OAuthCredentialsResponse response = temporaryToken.execute();

		System.out.println("Token:\t\t\t" + response.token);
		System.out.println("Token secret:\t" + response.tokenSecret);

		OAuthAuthorizeTemporaryTokenUrl authorizationURL = new OAuthAuthorizeTemporaryTokenUrl(authorizationUrl);
		authorizationURL.temporaryToken = response.token;
		System.out.println("Retrieve request token. Go to " + authorizationURL.toString() + " to authorize it.");

		return response.token;
	}

	public String getVerificationCodeFromUser() throws IllegalAccessException {
		System.out.println("Please enter the verification code:");
		Scanner scanner = new Scanner(System.in);
		String verificationCode = scanner.next();
		if (verificationCode.isEmpty()) {
			throw new IllegalAccessException("Verification code can not be empty");
		}
		return verificationCode;
	}

	/**
	 * Gets access token from JIRA
	 *
	 * @param tmpToken    temporary request token
	 * @param secret      secret (verification code provided by JIRA after request token authorization)
	 * @return access token value
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public String getAccessToken(String tmpToken, String secret) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		JiraOAuthGetAccessToken oAuthAccessToken = jiraOAuthTokenFactory.getJiraOAuthGetAccessToken(tmpToken, secret, consumerKey, privateKey);
		OAuthCredentialsResponse response = oAuthAccessToken.execute();

		System.out.println("Access token:\t\t\t" + response.token);
		return response.token;
	}

	/**
	 * Creates OAuthParameters used to make authorized request to JIRA
	 *
	 * @param tmpToken temporary access token
	 * @param secret client verification code
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public OAuthParameters getParameters(String tmpToken, String secret) throws NoSuchAlgorithmException, InvalidKeySpecException {
		JiraOAuthGetAccessToken oAuthAccessToken = jiraOAuthTokenFactory.getJiraOAuthGetAccessToken(tmpToken, secret, consumerKey, privateKey);
		oAuthAccessToken.verifier = secret;
		return oAuthAccessToken.createParameters();
	}

}

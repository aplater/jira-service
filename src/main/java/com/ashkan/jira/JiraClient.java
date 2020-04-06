package com.ashkan.jira;

import com.ashkan.jira.auth.JiraOAuthClient;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

@Component
public class JiraClient {

	private JiraOAuthClient jiraOAuthClient;

	@Autowired
	public JiraClient(JiraOAuthClient jiraOAuthClient) {
		this.jiraOAuthClient = jiraOAuthClient;
	}

	public Optional<Exception> sendGetRequest(String tempToken, String verificationCode, String url) {
		try {
			OAuthParameters oAuthParameters = jiraOAuthClient.getParameters(tempToken, verificationCode);
			HttpResponse response = getResponseFromUrl(oAuthParameters, new GenericUrl(url));
			parseResponse(response);
			return Optional.empty();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Optional.of(ex);
		}
	}

	/**
	 * Reads teh request URL from user input
	 * @return request URL
	 */
	public String getRequestUrlFromUser() {
		System.out.println("Please enter the request URL:");
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}

	/**
	 * Authenticates to JIRA with given OAuthParameters and makes request to url
	 *
	 * @param parameters
	 * @param jiraUrl
	 * @return
	 * @throws IOException
	 */
	private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
		HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
		HttpRequest request = requestFactory.buildGetRequest(jiraUrl);
		return request.execute();
	}

	/**
	 * Prints response content
	 * if response content is valid JSON it prints it in 'pretty' format
	 *
	 * @param response
	 * @throws IOException
	 */
	private void parseResponse(HttpResponse response) throws IOException {
		Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";

		try {
			JSONObject jsonObj = new JSONObject(result);
			System.out.println(jsonObj.toString(2));
		} catch (Exception e) {
			System.out.println(result);
		}
	}

}

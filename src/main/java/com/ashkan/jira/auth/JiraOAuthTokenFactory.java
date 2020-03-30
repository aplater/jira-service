package com.ashkan.jira.auth;

import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class JiraOAuthTokenFactory {
	@Value("${jira.home}plugins/servlet/oauth/access-token")
	protected String accessTokenUrl;

	@Value("${jira.home}plugins/servlet/oauth/request-token")
	protected String requestTokenUrl;

	/**
	 * Initialize JiraOAuthGetTemporaryToken
	 * by setting it to use POST method, oob (Out of Band) callback
	 * and setting consumer and private keys.
	 *
	 * @param consumerKey consumer key
	 * @param privateKey  private key in PKCS8 format
	 * @return JiraOAuthGetTemporaryToken request
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public JiraOAuthGetTemporaryToken getTemporaryToken(String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		JiraOAuthGetTemporaryToken oAuthGetTemporaryToken = new JiraOAuthGetTemporaryToken(requestTokenUrl);
		oAuthGetTemporaryToken.consumerKey = consumerKey;
		oAuthGetTemporaryToken.signer = getOAuthRsaSigner(privateKey);
		oAuthGetTemporaryToken.transport = new ApacheHttpTransport();
//		oAuthGetTemporaryToken.transport = new NetHttpTransport();
		oAuthGetTemporaryToken.callback = "oob";
		return oAuthGetTemporaryToken;
	}

	/**
	 * @param privateKey private key in PKCS8 format
	 * @return OAuthRsaSigner
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private OAuthRsaSigner getOAuthRsaSigner(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		OAuthRsaSigner oAuthRsaSigner = new OAuthRsaSigner();
		oAuthRsaSigner.privateKey = getPrivateKey(privateKey);
		return oAuthRsaSigner;
	}

	/**
	 * Creates PrivateKey from string
	 *
	 * @param privateKey private key in PKCS8 format
	 * @return private key
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] privateBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * Initialize JiraOAuthGetAccessToken
	 * by setting it to use POST method, secret, request token
	 * and setting consumer and private keys.
	 *
	 * @param requestToken    request token
	 * @param secret      secret (verification code provided by JIRA after request token authorization)
	 * @param consumerKey consumer ey
	 * @param privateKey  private key in PKCS8 format
	 * @return JiraOAuthGetAccessToken request
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public JiraOAuthGetAccessToken getJiraOAuthGetAccessToken(String requestToken, String secret, String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		JiraOAuthGetAccessToken accessToken = new JiraOAuthGetAccessToken(accessTokenUrl);
		accessToken.consumerKey = consumerKey;
		accessToken.signer = getOAuthRsaSigner(privateKey);
//		accessToken.transport = new ApacheHttpTransport();
		accessToken.transport = new NetHttpTransport();
		accessToken.verifier = secret;
		accessToken.temporaryToken = requestToken;
		return accessToken;
	}
}

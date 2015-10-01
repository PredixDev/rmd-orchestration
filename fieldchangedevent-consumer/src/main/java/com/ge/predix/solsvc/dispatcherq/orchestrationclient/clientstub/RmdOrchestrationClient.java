package com.ge.predix.solsvc.dispatcherq.orchestrationclient.clientstub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.predix.solsvc.dispatcherq.config.OrchestrationRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;

@SuppressWarnings("javadoc")
// @Component
public class RmdOrchestrationClient {

	private static final Logger logger = LoggerFactory
			.getLogger(RmdOrchestrationClient.class.getName());

	@Autowired
	private RestClient restClient;

	@Autowired
	private OrchestrationRestConfig orchestrationConfig;

	public String runOrchestration(String orchestrationRequest)
			throws IOException {

		String orchestrationResponse = null;

		System.out.println("Ref App Summary service URI="
				+ orchestrationConfig.getOrchestrationEndpoint());

		System.out.println("Rest client = " + restClient);
		// String tokenResponse = this.restClient.requestToken(null,
		// orchestrationConfig.getOauthClientId(), true,
		// orchestrationConfig.getOauthResource(),
		// orchestrationConfig.getOauthHost(),
		// orchestrationConfig.getOauthPort(),
		// orchestrationConfig.getOauthGrantType(), null, null,
		// orchestrationConfig.getUserName(), orchestrationConfig.getPassword(),
		// false);

		// System.out.println("TOKEN = " + tokenResponse);

		// Token token = new Token("asdfd");
		// token.update(tokenResponse);

		List<Header> headers = new ArrayList<Header>();
		Header contentTypeHeader = new BasicHeader("Content-Type",
				"application/json");

		headers.add(contentTypeHeader);

		HttpResponse response = restClient.post(
				orchestrationConfig.getOrchestrationEndpoint(),
				orchestrationRequest, headers);

		System.out.println("Response = " + response.getStatusLine());
		System.out.println("Response = " + response.toString());

		return orchestrationResponse;

	}

}

package com.ge.predix.solsvc.dispatcherq.orchestrationclient.clientstub;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.predix.solsvc.dispatcherq.config.OrchestrationRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;

@SuppressWarnings("javadoc")
// @Component
public class RmdOrchestrationClient {

	private static final Logger logger = LoggerFactory.getLogger(RmdOrchestrationClient.class.getName());

	@Autowired
	private RestClient restClient;

	@Autowired
	private OrchestrationRestConfig orchestrationConfig;

	@SuppressWarnings({})
	public String runOrchestration(String orchestrationRequest) {
		CloseableHttpResponse httpResponse = null;
		try {
			logger.info("Predix Platform Orchestration Runtime URI=" //$NON-NLS-1$
					+ this.orchestrationConfig.getOrchestrationEndpoint());

			List<Header> headers = this.restClient.getSecureTokenForClientId();
			logger.info("Headers 1 = " + headers.get(0)); //$NON-NLS-1$
			Header contentTypeHeader = new BasicHeader("Content-Type", //$NON-NLS-1$
					"application/json"); //$NON-NLS-1$

			headers.add(contentTypeHeader);
			logger.info("Headers 2 = " + headers.get(1)); //$NON-NLS-1$

			Header predixZoneIdHeader = new BasicHeader("Predix-Zone-Id", //$NON-NLS-1$
					this.orchestrationConfig.getZoneId());
			headers.add(predixZoneIdHeader);
			logger.info("Headers 3 = " + headers.get(2)); //$NON-NLS-1$

			httpResponse = this.restClient.post(this.orchestrationConfig.getOrchestrationEndpoint(),
					orchestrationRequest, headers, this.orchestrationConfig.getOrchestrationConnectionTimeout(),
					this.orchestrationConfig.getOrchestrationSocketTimeout());

			logger.info("Response = " + httpResponse.getStatusLine()); //$NON-NLS-1$
			logger.info("Response = " + httpResponse.toString()); //$NON-NLS-1$
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("unable able to connect to the url=" //$NON-NLS-1$
						+ this.orchestrationConfig.getOrchestrationEndpoint() + " response=" + httpResponse); //$NON-NLS-1$
			}

			String response = this.restClient.getResponse(httpResponse);
			return response;
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public String getAnalyticsEndpoint() {
		return this.orchestrationConfig.getAnalyticsEndpoint();
	}

}

/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.dispatcherq.consumer.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ge.predix.entity.eventasset.Asset;
import com.ge.predix.entity.fieldchanged.FieldChanged;
import com.ge.predix.entity.fieldchanged.FieldChangedList;
import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherq.orchestrationclient.clientstub.RmdOrchestrationClient;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * 
 * @author 212367843
 */
@SuppressWarnings({ "nls" })
@Component
public class FieldChangedEventMessageHandler implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(FieldChangedEventMessageHandler.class.getName());

	@Autowired
	private RmdOrchestrationClient client;

	@Autowired
	private JsonMapper jsonMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.amqp.core.MessageListener#onMessage(org.
	 * springframework .amqp.core.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {

			onMessageDoWork(message);
		} catch (Throwable e) {
			// Throw away message until proper error handling is implemented.
			// Ideally, it would retry N times and if still failing put the
			// message in an error queue
			// another way is to configure a dead letter queue on the queue
			// manager
			String messageAsString = null;
			if ( message != null ) 
				messageAsString = new String(message.getBody());
			logger.error("Message is not processable. error=" + e.getMessage() + " message=" + messageAsString, e);
		}
	}

	/**
	 * @param message
	 *            -
	 * @return -
	 */
	public List<String> onMessageDoWork(Message message) {
		// Get the java object from the payload
		String payloadAsString = new String(message.getBody());
		logger.info("Listener received message----->" + payloadAsString);

		FieldChangedEvent fce = getFieldChangedEventFromPayload(payloadAsString);

		// Convert the retrieved java object to the REST service request object
		// and Invoke the REST service
		try {
			return convertFCEtoRunAnalyticAndRunOrchestration(fce);
		} catch (JsonProcessingException e) {
			logger.error("JsonProcessingException", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("IOException", e);
			throw new RuntimeException(e);
		}
	}

	private FieldChangedEvent getFieldChangedEventFromPayload(String payload) {
		FieldChangedEvent fieldChangedEvent = this.jsonMapper.fromJson(payload, FieldChangedEvent.class);
		logger.info("Listener received message----->" + fieldChangedEvent);
		return fieldChangedEvent;
	}

	private List<String> convertFCEtoRunAnalyticAndRunOrchestration(final FieldChangedEvent fieldChangedEvent)
			throws JsonProcessingException, IOException {

		List<String> results = new ArrayList<String>();
		for (FieldChanged fieldChanged : fieldChangedEvent.getFieldChangedList().getFieldChanged()) {
			for (Asset asset : fieldChanged.getAssetList().getAsset()) {

				logger.info("Asset uri =" + asset.getUri());
				logger.info("Asset type = " + asset.getAssetType());
				logger.info("FieldChanged = " + fieldChanged);
				FieldChangedEvent fieldChangedEventToUse = new FieldChangedEvent();
				fieldChangedEventToUse.setFieldChangedList(new FieldChangedList());
				fieldChangedEventToUse.getFieldChangedList().getFieldChanged().add(fieldChanged);

				String orchestrationRequest = constructOrchRqstFromRunAnalyticRqst(fieldChangedEventToUse);

				String result = this.client.runOrchestration(orchestrationRequest);
				results.add(result);
			}
		}
		return results;

	}

	private String constructOrchRqstFromRunAnalyticRqst(FieldChangedEvent fieldChangedEvent)
			throws JsonProcessingException, IOException {
		String orchRqst = null;

		putAnalyticRequestInTemplate(fieldChangedEvent);
		FileInputStream workflowFile = null;
		try {
			workflowFile = new FileInputStream(new File("alertStatusWorkflow.json"));
			orchRqst = IOUtils.toString(workflowFile);
			logger.info("Predix Orch Request = " + orchRqst);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (workflowFile != null)
				workflowFile.close();
		}

		return orchRqst;
	}

	private void putAnalyticRequestInTemplate(FieldChangedEvent fieldChangedEvent)
			throws JsonProcessingException, IOException {

		logger.info("ANALYTICS ENDPOINT = " + this.client.getAnalyticsEndpoint());
		logger.info("FieldChangedEvent = " + fieldChangedEvent);

		String analyticRequestData = searchAndReplace(fieldChangedEvent);

		ObjectMapper m = new ObjectMapper();
		logger.info("Predix Orch Request template json from file = " + getClass().getClassLoader()
				.getResourceAsStream("orchestrations/alertStatusWorkflow/alertStatusWorkflowTemplate.json"));
		JsonNode rootNode = m.readTree(getClass().getClassLoader()
				.getResourceAsStream("orchestrations/alertStatusWorkflow/alertStatusWorkflowTemplate.json"));

		// bpmnXml
		JsonNode bpmnXmlNode = rootNode.path("bpmnXml");
		String bpmnString = bpmnXmlNode.asText();
		logger.info("bpmnXml value =" + bpmnString);

		String replacedbpmnString = bpmnString.replaceAll("ANALYTICSENDPOINT", this.client.getAnalyticsEndpoint());
		logger.info("replaced bpmnXml value =" + replacedbpmnString);

		((ObjectNode) rootNode).put("bpmnXml", replacedbpmnString);

		// analyticInputData
		ArrayNode analyticInputDataNode = (ArrayNode) rootNode.path("analyticInputData");
		for (int i = 0; i < analyticInputDataNode.size(); i++) {
			JsonNode dataNode1 = analyticInputDataNode.get(i);
			JsonNode dataNode = analyticInputDataNode.get(i).get("data");
			logger.info("dataNode = " + dataNode.asText());
			((ObjectNode) dataNode1).put("data", analyticRequestData);
		}
		m.writeValue(new File("alertStatusWorkflow.json"), rootNode);
	}

	private String searchAndReplace(FieldChangedEvent fieldChangedEvent) {

		StringBuffer afterReplacingAnalyticRequestData = new StringBuffer();

		try {
			String beforeReplacingAnalyticRequestData = IOUtils.toString(getClass().getClassLoader()
					.getResourceAsStream("orchestrations/alertStatusWorkflow/alarmThresholdAnalyticTemplate.json"));
			logger.info("Use JSONPath to replace the volatile data...before Replacing RunAnalytic Request = "
					+ beforeReplacingAnalyticRequestData);

			final String regex = "(\\{\\{)(.*)(\\}\\})";
			final Pattern p = Pattern.compile(regex);
			final Matcher m = p.matcher(beforeReplacingAnalyticRequestData);

			while (m.find()) {
				String whatToSrchFor = m.group(2);
				logger.debug("What to search for =" + whatToSrchFor);
				List<String> srchResults = null;

				try {
					String fceJsonString = this.jsonMapper.toJson(fieldChangedEvent);
					logger.debug("fceJsonString =" + fceJsonString);

					srchResults = JsonPath.parse(fceJsonString).read(whatToSrchFor);
				} catch (PathNotFoundException e) {
					m.appendReplacement(afterReplacingAnalyticRequestData, "{{" + whatToSrchFor + "}}");
					continue;
				}

				m.appendReplacement(afterReplacingAnalyticRequestData, srchResults.get(0));
			}
			m.appendTail(afterReplacingAnalyticRequestData);

			logger.info("Use JSONPath to replace the volatile data...after search and replacement ="
					+ afterReplacingAnalyticRequestData);
			return afterReplacingAnalyticRequestData.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

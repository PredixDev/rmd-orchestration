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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ge.predix.entity.analytic.runanalytic.RunAnalyticRequest;
import com.ge.predix.entity.eventasset.Asset;
import com.ge.predix.entity.fieldchanged.FieldChanged;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherq.orchestrationclient.clientstub.RmdOrchestrationClient;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

/**
 * 
 * @author 212367843
 */
@SuppressWarnings({ "nls" })
@Component
public class FieldChangedEventMessageHandler implements MessageListener {

	private static final Logger logger = LoggerFactory
			.getLogger(FieldChangedEventMessageHandler.class.getName());

	@Autowired
	private MessageConverter fieldChangedEventMessageConverter;

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
		onMessageDoWork(message);
	}

	/**
	 * @param message
	 *            -
	 * @return -
	 */
	public List<String> onMessageDoWork(Message message) {
		// Get the java object from the payload
		FieldChangedEvent fce = getFieldChangedEventFromPayload(message);
		logger.info("Listener received message----->" + fce);

		// Convert the retreived java object to the REST service request object
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

	private FieldChangedEvent getFieldChangedEventFromPayload(Message payload) {
		FieldChangedEvent fieldChangedEvent = (FieldChangedEvent) this.fieldChangedEventMessageConverter
				.fromMessage(payload);

		logger.info("Listener received message----->" + fieldChangedEvent);

		return fieldChangedEvent;
	}

	private List<String> convertFCEtoRunAnalyticAndRunOrchestration(
			final FieldChangedEvent fieldChangedEvent)
			throws JsonProcessingException, IOException {

		List<String> results = new ArrayList<String>();
		for (FieldChanged fieldChanged : fieldChangedEvent
				.getFieldChangedList().getFieldChanged()) {
			logger.info("Field Identifier1 ="
					+ fieldChanged.getFieldIdentifierValueList()
							.getFieldIdentifierValue().get(0));

			logger.info("Field Identifier2 ="
					+ fieldChanged.getFieldIdentifierValueList()
							.getFieldIdentifierValue().get(0)
							.getFieldIdentifier());

			for (Asset asset : fieldChanged.getAssetList().getAsset()) {

				logger.info("Asset Id =" + asset.getAssetIdentifier());
				logger.info("Asset Id =" + asset.getAssetIdentifier().getId());

				String orchestrationRequest = constructOrchRqstFromRunAnalyticRqst(
						fieldChanged.getExternalAttributeMap());

				logger.info("");
				String result = this.client
						.runOrchestration(orchestrationRequest);
				results.add(result);
			}
		}
		return results;

	}

	private String constructOrchRqstFromRunAnalyticRqst(
			AttributeMap externalAttributes)
			throws JsonProcessingException, IOException {
		String orchRqst = null;

		putAnalyticRequestInTemplate(
				externalAttributes);

		try {
			File jarPath = new File(FieldChangedEventMessageHandler.class
					.getProtectionDomain().getCodeSource().getLocation()
					.getPath());
			String propertiesPath = jarPath.getParent();
			propertiesPath = propertiesPath
					+ "/predixOrchestrationRuntimeRequest.json";

			logger.info(" propertiesPath = " + propertiesPath);
			// logger.info("Predix Orch Request json from file = " + new
			// FileInputStream(propertiesPath));
			File requestFile = new File(
					"predixOrchestrationRuntimeRequest.json");

			orchRqst = IOUtils.toString(new FileInputStream(requestFile));
			logger.info("Predix Orch Request = " + orchRqst);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return orchRqst;
	}

	private void putAnalyticRequestInTemplate(
			AttributeMap externalAttributes) throws JsonProcessingException,
			IOException {

		logger.info("ANALYTICS ENDPOINT = "
				+ this.client.getAnalyticsEndpoint());

		String analyticRequestData = searchAndReplace(externalAttributes);

		ObjectMapper m = new ObjectMapper();
		logger.info("Predix Orch Request template json from file = "
				+ getClass().getClassLoader().getResourceAsStream(
						"predixOrchestrationRuntimeRequestTemplate.json"));
		JsonNode rootNode = m.readTree(getClass().getClassLoader()
				.getResourceAsStream(
						"predixOrchestrationRuntimeRequestTemplate.json"));

		// bpmnXml
		JsonNode bpmnXmlNode = rootNode.path("bpmnXml");
		String bpmnString = bpmnXmlNode.asText();
		logger.info("bpmnXml value =" + bpmnString);

		String replacedbpmnString = bpmnString.replaceAll("ANALYTICSENDPOINT",
				this.client.getAnalyticsEndpoint());
		logger.info("replaced bpmnXml value =" + replacedbpmnString);

		((ObjectNode) rootNode).put("bpmnXml", replacedbpmnString);

		// analyticInputData
		ArrayNode analyticInputDataNode = (ArrayNode) rootNode
				.path("analyticInputData");
		for (int i = 0; i < analyticInputDataNode.size(); i++) {
			JsonNode dataNode1 = analyticInputDataNode.get(i);
			JsonNode dataNode = analyticInputDataNode.get(i).get("data");
			logger.info("dataNode = " + dataNode.asText());
			((ObjectNode) dataNode1).put("data", analyticRequestData);
		}
		m.writeValue(new File("predixOrchestrationRuntimeRequest.json"),
				rootNode);
	}

	private String searchAndReplace(AttributeMap whatToReplaceWith) {

		String afterReplacingAnalyticRequestData = null;

		try {
			String beforeReplacingAnalyticRequestData = IOUtils
					.toString(getClass().getClassLoader().getResourceAsStream(
							"RunAnalyticRequestTemplate.json"));
			logger.info("Before Replacing RunAnalytic Request = "
					+ beforeReplacingAnalyticRequestData);

			/*
			 * Map<String, Object> anotherWhatToReplaceWith = new
			 * HashMap<String, Object>();
			 * 
			 * Template template = Mustache.compiler().compile(
			 * beforeReplacingAnalyticRequestData); for (Entry replaceWithThis :
			 * whatToReplaceWith.getEntry()) {
			 * anotherWhatToReplaceWith.put((String) replaceWithThis.getKey(),
			 * replaceWithThis.getValue()); }
			 * 
			 * return template.execute(anotherWhatToReplaceWith);
			 */

			for (Entry replaceWithThis : whatToReplaceWith.getEntry()) {
				afterReplacingAnalyticRequestData = beforeReplacingAnalyticRequestData.replaceAll(
						"\\{\\{" + (String) replaceWithThis.getKey() + "\\}\\}",
						(String) replaceWithThis.getValue());
				logger.info("After Replacing RunAnalytic Request = "
						+ afterReplacingAnalyticRequestData);
				beforeReplacingAnalyticRequestData = afterReplacingAnalyticRequestData;
			}

			return afterReplacingAnalyticRequestData;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

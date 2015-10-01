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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.analytic.entity.runanalytic.RunAnalyticRequest;
import com.ge.dsp.pm.ext.entity.asset.Asset;
import com.ge.dsp.pm.solution.service.entity.fieldchanged.FieldChanged;
import com.ge.dsp.pm.solution.service.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherq.orchestrationclient.clientstub.RmdOrchestrationClient;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.amqp.core.MessageListener#onMessage(org.springframework
	 * .amqp.core.Message)
	 */
	@Override
	public void onMessage(Message message) {
		// Get the java object from the payload
		FieldChangedEvent fce = getFieldChangedEventFromPayload(message);
		logger.debug("Listener received message----->" + fce);

		// Convert the retreived java object to the REST service request object
		// and Invoke the REST service
		convertFCEtoRunAnalytic(fce);

	}

	private FieldChangedEvent getFieldChangedEventFromPayload(Message payload) {
		FieldChangedEvent fieldChangedEvent = (FieldChangedEvent) this.fieldChangedEventMessageConverter
				.fromMessage(payload);

		System.out.println("Listener received message----->"
				+ fieldChangedEvent);

		return fieldChangedEvent;
	}

	private void convertFCEtoRunAnalytic(
			final FieldChangedEvent fieldChangedEvent) {

		for (FieldChanged fieldChanged : fieldChangedEvent
				.getFieldChangedList().getFieldChanged()) {
			Long solutionId = Long.parseLong(fieldChanged
					.getSolutionIdentifier().getId().toString());
			if (logger.isDebugEnabled())
				logger.debug("solutionId   : " + solutionId);

			for (Asset asset : fieldChanged.getAssetList().getAsset()) {
				RunAnalyticRequest runAnalyticRequest = RunAnalyticTemplate.getActualRunAnalyticFromTemplate(
						solutionId, (String) fieldChanged
								.getFieldIdentifierValueList()
								.getFieldIdentifierValue().get(0)
								.getFieldIdentifier().getId(), (String) asset
								.getAssetIdentifier().getId(), asset
								.getAssetIdentifier().getName());
				
				String orchestrationRequest = constructFromRunAnalyticRequest(runAnalyticRequest);
				try {
					client.runOrchestration(orchestrationRequest);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
	
	private String constructFromRunAnalyticRequest(RunAnalyticRequest runAnalyticRequest)
	{
		String response = null;
		
		return response;
	}

}

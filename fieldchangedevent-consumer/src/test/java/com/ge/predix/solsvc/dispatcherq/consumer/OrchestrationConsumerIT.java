/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.dispatcherq.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.bootstrap.ams.client.AssetClientImpl;
import com.ge.predix.solsvc.bootstrap.ams.dto.Attribute;
import com.ge.predix.solsvc.dispatcherq.boot.FieldChangedEventConsumerApplication;
import com.ge.predix.solsvc.dispatcherq.consumer.handler.FieldChangedEventMessageHandler;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { FieldChangedEventConsumerApplication.class })
@ContextConfiguration(locations = { "classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
		"classpath*:Test-solution-change-event-consumer.xml" })
public class OrchestrationConsumerIT {

	/**
	 * 
	 */
	static final Logger log = LoggerFactory.getLogger(OrchestrationConsumerIT.class);

	@Autowired
	private FieldChangedEventMessageHandler fieldChangedEventMessageHandler;

	@Autowired
	private Jackson2JsonMessageConverter jacksonMessageConverter;

	// @author 212672942. Making changes with adding Qualifier since now all
	// Factory classes extend ModelFactory
	@Autowired
	@Qualifier("AssetClient")
	private AssetClientImpl assetClient;


	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private TimeseriesClient timeseriesClient;

	private List<Header>           assetHeaders;
	private String	timeseriesTag;

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Do set up here
	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Cleanup after execution
	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@SuppressWarnings("nls")
	@Before
	public void setUp() throws Exception {
        this.assetHeaders = setHeaders();
        this.timeseriesTag = "Machine-102:CompressionRatio";	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@After
	public void tearDown() throws Exception {
		// clean up
	}

	/**
	 * -
	 */
	@Test
	public void testAlarm() {
		createAsset();
    	sleep();
		setAlertStatus(this.assetHeaders, false);
		Integer actualDatapoint = new Integer(29);
		createDatapoint(this.timeseriesTag, actualDatapoint); 

		Message msg = this.jacksonMessageConverter.toMessage(createFieldChangedEvent(), null);

		List<String> results = this.fieldChangedEventMessageHandler.onMessageDoWork(msg);

		verifyResponse(this.assetHeaders, results, true, actualDatapoint);
	}

	/**
	 * @throws IOException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testRunAnalyticTemplateVariableMatch() throws IOException {
		final String regex = "(\\{\\{)(.*)(\\}\\})";
		final String string = IOUtils
				.toString(getClass().getClassLoader().getResourceAsStream("alarmThresholdAnalyticTemplate.json"));

		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(string);

		while (matcher.find()) {
			log.info("Full match: " + matcher.group(0));
			for (int i = 1; i <= matcher.groupCount(); i++) {
				log.info("Group " + i + ": " + matcher.group(i));
			}
		}

	}

	private void createDatapoint(String tag, Integer actualValueOfSensor) {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		long currentTimeMillis = System.currentTimeMillis() + 1000000;
		dpIngestion.setMessageId(String.valueOf(currentTimeMillis));

		List<Object> datapoint1 = new ArrayList<Object>();
		datapoint1.add(currentTimeMillis);
		datapoint1.add(actualValueOfSensor);
		datapoint1.add(3); // quality

		List<Object> datapoints = new ArrayList<Object>();
		datapoints.add(datapoint1);

		Body body = new Body();
		body.setName(tag);
		body.setDatapoints(datapoints);

		List<Body> bodies = new ArrayList<Body>();
		bodies.add(body);

		dpIngestion.setBody(bodies);

		this.timeseriesClient.createTimeseriesWebsocketConnectionPool();
		this.timeseriesClient.postDataToTimeseriesWebsocket(dpIngestion);
	}

	@SuppressWarnings("nls")
	private void verifyResponse(List<Header> headers, List<String> results, boolean expectedAlertStatus,
			Integer expectedAlertLevelValue) {

		log.debug("Results = " + results);
		Assert.assertNotNull(results);
		for (String result : results) {
			Assert.assertTrue(!result.contains("errorMsg"));
		}

		List<Object> models = this.assetClient
				.getModels("/asset/machine102.alert-status.compressionratio", "Asset", headers);

		Map attributes = ((Asset) models.get(0)).getAttributes();
		log.debug(attributes.keySet().toString());
		Attribute alertLevelValue = (Attribute) attributes.get("alertLevelValue");
		Attribute alertStatus = (Attribute) attributes.get("alertStatus");
		if (alertLevelValue.getValue().get(0) instanceof Integer) {
			Integer alertLevelValueAsInteger = (Integer) alertLevelValue.getValue().get(0);
			if (!alertLevelValueAsInteger.equals(expectedAlertLevelValue))
				Assert.fail(
						"The value the test case set, is not the value that timeseries and/or the analytic saved alertLevelValue="
								+ alertLevelValueAsInteger + " expectedAlertLevelValue=" + expectedAlertLevelValue);

			if (!alertStatus.getValue().get(0).equals(expectedAlertStatus))
				Assert.fail("The expected alertStatus=" + expectedAlertStatus + " does not match actualAlertStatus"
						+ alertStatus.getValue().get(0));
		} else if (alertLevelValue.getValue().get(0) instanceof Double) {
			Double actualValueOfSensor = (Double) alertLevelValue.getValue().get(0);

			if (actualValueOfSensor >= 23) {
				Assert.assertEquals(true, alertStatus.getValue().get(0));
			} else {
				Assert.assertEquals(false, alertStatus.getValue().get(0));
			}
		}

	}

	@SuppressWarnings("nls")
	private void setAlertStatus(List<Header> headers, Boolean status) {
		List<Object> models = this.assetClient
				.getModels("/asset/machine102.alert-status.compressionratio", "Asset", headers);

		((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus")).getValue().set(0, status);

		this.assetClient.updateModel(models.get(0), "Asset", headers);
	}

	@SuppressWarnings("nls")
    private List<Header> setHeaders() {

		List<Header> headers = this.assetClient.getAssetHeaders();

		Header header = new BasicHeader("Content-Type", "application/json");
		headers.add(header);
		header = new BasicHeader("Accept", "application/json");
		headers.add(header);

		return headers;
	}

	@SuppressWarnings("nls")
	private FieldChangedEvent createFieldChangedEvent() {
		FieldChangedEvent fieldChangedEvent = new FieldChangedEvent();

		try {
			String fieldChangedEventJsonString = IOUtils
					.toString(getClass().getClassLoader().getResourceAsStream("FieldChangedEvent.json"));

			fieldChangedEvent = this.jsonMapper.fromJson(fieldChangedEventJsonString, FieldChangedEvent.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldChangedEvent;
	}

    private void sleep()
    {
        try
        {
            Thread.currentThread();
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            log.error(e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("nls")
    private String readAssetInfoFromFile()
    {
        try
        {
            return IOUtils.toString(
                    getClass().getClassLoader().getResourceAsStream("Machine102.json"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("nls")
    private void createAsset()
    {
    	this.assetClient.createFromJson("/asset", readAssetInfoFromFile(), this.assetHeaders);
    }

}

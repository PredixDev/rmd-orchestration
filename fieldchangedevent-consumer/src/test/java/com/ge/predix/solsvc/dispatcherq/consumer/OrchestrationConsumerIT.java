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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.eventasset.AssetList;
import com.ge.predix.entity.eventasset.eventassetidentifier.AssetIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fieldchanged.FieldChanged;
import com.ge.predix.entity.fieldchanged.FieldChangedList;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValueList;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.dto.Attribute;
import com.ge.predix.solsvc.dispatcherq.boot.FieldChangedEventConsumerApplication;
import com.ge.predix.solsvc.dispatcherq.consumer.handler.FieldChangedEventMessageHandler;
import com.ge.predix.solsvc.dispatcherq.util.StringUtil;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { FieldChangedEventConsumerApplication.class })
@ContextConfiguration(locations = {
		"classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
		"classpath*:Test-solution-change-event-consumer.xml" })
public class OrchestrationConsumerIT {

	@Autowired
	private FieldChangedEventMessageHandler fieldChangedEventMessageHandler;

	@Autowired
	private MessageConverter messageConverter;

	@Autowired
	private ModelFactory modelFactory;

	@Autowired
	private RestClient restClient;

	@Autowired
	private AssetConfig assetConfig;

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
	@Before
	public void setUp() throws Exception {
		// setUp
	}

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
	@SuppressWarnings("nls")
	@Test
	public void testFieldChangedAndAlarmAccordingly() {

		List<Header> headers = setHeaders();

		setAlertStatus(headers, true);

		Message msg = this.messageConverter.toMessage(
				createFieldChangedEvent(
						"/asset/assetTag/crank-frame-dischargepressure", //$NON-NLS-1$
						"/asset/assetTag/crank-frame-dischargepressure", //$NON-NLS-1$
						"/asset/compressor-2015", //$NON-NLS-1$
						StringUtil.parseDate("2012-09-11T07:16:13"), //$NON-NLS-1$
						"/asset/assetUri", "/asset/compressor-2015"), null);

		List<String> results = this.fieldChangedEventMessageHandler
				.onMessageDoWork(msg);

		verifyResponse(headers, results);
	}

	private void verifyResponse(List<Header> headers, List<String> results) {

		Assert.assertNotNull(results);
		for (String result : results) {
			Assert.assertTrue(!result.contains("errorMsg"));
		}

		List<Object> models = this.modelFactory
				.getModels(
						"/asset/compressor-2015.tag-extensions.crank-frame-discharge-pressure",
						"Asset", headers);

		if (((Attribute) ((Asset) models.get(0)).getAttributes().get(
				"alertLevelValue")).getValue().get(0) instanceof Integer) {
			Integer actualValueOfSensor = (Integer) ((Attribute) ((Asset) models
					.get(0)).getAttributes().get("alertLevelValue")).getValue()
					.get(0);

			if (actualValueOfSensor > 23) {
				Assert.assertEquals(true,
						(Boolean) ((Attribute) ((Asset) models.get(0))
								.getAttributes().get("alertStatus")).getValue()
								.get(0));
			} else {
				Assert.assertEquals(false,
						(Boolean) ((Attribute) ((Asset) models.get(0))
								.getAttributes().get("alertStatus")).getValue()
								.get(0));
			}
		} else if (((Attribute) ((Asset) models.get(0)).getAttributes().get(
				"alertLevelValue")).getValue().get(0) instanceof Double) {
			Double actualValueOfSensor = (Double) ((Attribute) ((Asset) models
					.get(0)).getAttributes().get("alertLevelValue")).getValue()
					.get(0);

			if (actualValueOfSensor > 23) {
				Assert.assertEquals(true,
						(Boolean) ((Attribute) ((Asset) models.get(0))
								.getAttributes().get("alertStatus")).getValue()
								.get(0));
			} else {
				Assert.assertEquals(false,
						(Boolean) ((Attribute) ((Asset) models.get(0))
								.getAttributes().get("alertStatus")).getValue()
								.get(0));
			}
		}

	}

	private void setAlertStatus(List<Header> headers, Boolean status) {
		List<Object> models = this.modelFactory
				.getModels(
						"/asset/compressor-2015.tag-extensions.crank-frame-discharge-pressure",
						"Asset", headers);

		((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus"))
				.getValue().set(0, status);

		this.modelFactory.updateModel(models.get(0), "Asset", headers);
	}

	private List<Header> setHeaders() {

		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

		Header header = new BasicHeader("Content-Type", "application/json");
		headers.add(header);
		header = new BasicHeader("Accept", "application/json");
		headers.add(header);

		return headers;
	}

	private FieldChangedEvent createFieldChangedEvent(String triggerField,
			String triggerFieldName, String assetId, Date time,
			Object externalMapKey, Object externalMapValue) {
		FieldChangedEvent fieldChangedEvent = new FieldChangedEvent();

		FieldChanged fieldChanged = new FieldChanged();
		AttributeMap attributeMap = new AttributeMap();
		if (externalMapKey != null) {
			Entry entry = new Entry();

			entry.setKey(externalMapKey);
			entry.setValue(externalMapValue);
			attributeMap.getEntry().add(entry);
		}
		fieldChanged.setExternalAttributeMap(attributeMap);

		fieldChanged.setTimeChanged(StringUtil.getXMLDate(time));

		FieldIdentifierValueList fieldIdentifierValueList = new FieldIdentifierValueList();
		List<FieldIdentifierValue> fieldIdentifierValues = new ArrayList<FieldIdentifierValue>();

		FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
		FieldIdentifier fieldIdentifier = createFieldIdentifier(triggerField,
				triggerFieldName);
		// Orchestration
		fieldIdentifierValue.setFieldIdentifier(fieldIdentifier);
		fieldIdentifierValues.add(fieldIdentifierValue);
		fieldIdentifierValueList.setFieldIdentifierValue(fieldIdentifierValues);
		fieldChanged.setFieldIdentifierValueList(fieldIdentifierValueList);

		AssetList assetList = new AssetList();
		com.ge.predix.entity.eventasset.Asset asset = new com.ge.predix.entity.eventasset.Asset();

		FieldIdentifier assetFieldIdentifier = createFieldIdentifier(
				"/asset/assetId", "/asset/assetId"); //$NON-NLS-1$ //$NON-NLS-2$
		asset.setAssetIdFieldIdentifier(assetFieldIdentifier);
		AssetIdentifier assetIdentifier = new AssetIdentifier();
		assetIdentifier.setId(assetId);
		assetIdentifier.setName(assetId);
		asset.setAssetIdentifier(assetIdentifier);

		assetList.getAsset().add(asset);

		fieldChanged.setAssetList(assetList);

		List<FieldChanged> lstFieldChanged = new ArrayList<FieldChanged>();
		lstFieldChanged.add(fieldChanged);

		FieldChangedList fieldChangedList = new FieldChangedList();
		fieldChangedList.getFieldChanged().addAll(lstFieldChanged);
		fieldChangedEvent.setFieldChangedList(fieldChangedList);
		return fieldChangedEvent;
	}

	private FieldIdentifier createFieldIdentifier(String fieldId,
			String fieldName) {
		FieldIdentifier assetFieldIdentifier = new FieldIdentifier();
		assetFieldIdentifier.setId(fieldId);
		assetFieldIdentifier.setName(fieldName);
		assetFieldIdentifier.setSource("PREDIX_ASSET"); //$NON-NLS-1$
		return assetFieldIdentifier;
	}

}

/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.dispatcherqproducer.impl;

import java.io.IOException;
import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherqproducer.api.NotifyFieldChangedEvent;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/field-change-event-producer.xml",
		"classpath*:META-INF/spring/ext-util-scan-context.xml" })
public class NotifyFCEToRabbitMQTestHarness {
	private static final Logger logger = LoggerFactory.getLogger(NotifyFieldChangedEventToRabbitMQueue.class.getName());

	@Autowired
	private NotifyFieldChangedEvent notifyToQ;

	@Autowired
	private JsonMapper jsonMapper;

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setUp before run
	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// clean up
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
	@Test
	public void testNotifyFieldChangedEvent() {
		this.notifyToQ.notify(createFieldChangedEvent());

	}

	private FieldChangedEvent createFieldChangedEvent() {
		FieldChangedEvent fieldChangedEvent = new FieldChangedEvent();

		try {
			String fieldChangedEventJsonString = IOUtils.toString(getClass()
					.getClassLoader().getResourceAsStream(
							"FieldChangedEvent.json"));

			fieldChangedEvent = this.jsonMapper.fromJson(
					fieldChangedEventJsonString, FieldChangedEvent.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldChangedEvent;
	}

}

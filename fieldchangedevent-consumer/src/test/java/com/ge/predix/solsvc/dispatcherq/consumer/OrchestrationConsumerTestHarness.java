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
import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:Test-solution-change-event-consumer.xml")
public class OrchestrationConsumerTestHarness {

	@Autowired
	private RabbitTemplate fieldChangedEventTemplate;

	@Autowired
	private MessageConverter messageConverter;
	
	@Autowired
	private JsonMapper jsonMapper;

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
	@Test
	public void test() {
		Message msg = this.messageConverter
				.toMessage(createFieldChangedEvent(), null);

		this.fieldChangedEventTemplate.convertAndSend("mainq", msg); //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
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

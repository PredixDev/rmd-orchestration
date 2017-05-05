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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.jayway.jsonpath.JsonPath;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath:Test-solution-change-event-consumer.xml",
		"classpath:META-INF/spring/predix-rest-client-scan-context.xml" })
public class OrchestrationConsumerTest {
	
	private static final Logger    log = LoggerFactory.getLogger(OrchestrationConsumerTest.class);

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
	 * @throws Exception -
	 */
	@Test
	public void test() throws Exception {

		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection((ExecutorService) null))
				.thenReturn(mockConnection);
		when(mockConnection.isOpen()).thenReturn(true);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		when(mockChannel.isOpen()).thenReturn(true);

		Message msg = this.messageConverter.toMessage(
				createFieldChangedEvent(), null);

		final RabbitTemplate fieldChangedEventTemplate = new RabbitTemplate(
				new CachingConnectionFactory(mockConnectionFactory));
		fieldChangedEventTemplate.convertAndSend("fieldchangedeventMainQ", msg); //$NON-NLS-1$
	}

	/**
	 * @throws IOException -
	 */
	@SuppressWarnings("nls")
    @Test
	public void testFCEJsonPathMatch() throws IOException {
		String whatToSrchFor = "$..assetList.asset[0].fieldList.field[0].fieldKey";
		String fceJsonString = IOUtils.toString(getClass().getClassLoader()
				.getResourceAsStream("FieldChangedEvent.json"));

		List<String> srchResults = JsonPath.parse(fceJsonString).read(
				whatToSrchFor);
		log.info("Search results =" + srchResults.get(0));

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

package com.ge.predix.solsvc.dispatcherqproducer.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author 212367843 Sankar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-local-properties-context.xml",
		"classpath*:Test-field-change-event-producer.xml" })
public class NotifyFCEToRabbitMQIT {

	private static final Logger log = LoggerFactory
			.getLogger(NotifyFCEToRabbitMQIT.class.getName());

	@Autowired
	private RestClient restClient;

	@Value("${rabbitmq.server.url}")
	private String rabbitMQServerUrl;

	@Value("${rabbitmq.server.auth}")
	private String rabbitMQAuthcode;
	
	@Value("${rabbitmq.server.exchange}")
	private String rabbitMQExchange;

	@Value("${rabbitmq.server.routing.key}")
	private String rabbitMQRoutingKey;
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
		log.debug("Host = " + rabbitMQServerUrl);
				
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Content-Type", "text/plain;charset=UTF-8"));
		headers.add(new BasicHeader("Authorization", "Basic " + rabbitMQAuthcode));

		CloseableHttpResponse response = this.restClient.post(rabbitMQServerUrl, createFieldChangedEvent(), headers);	
		
        log.debug("RESPONSE: Response from RabbitMQ post message  = " + response);

        String responseAsString = this.restClient.getResponse(response);
        log.debug("RESPONSE: Response from RabbitMQ post message  = " + responseAsString);

        Assert.assertNotNull(response);
        Assert.assertNotNull(responseAsString);
        Assert.assertFalse(responseAsString.contains("\"status\":500"));
        
        Assert.assertTrue(responseAsString.contains("routed"));
        Assert.assertTrue(responseAsString.contains("true"));
	}

	private String createFieldChangedEvent() {
		String fieldChangedEventStr = null;
		String tempStr = null;

		try {
			fieldChangedEventStr = IOUtils.toString(getClass()
					.getClassLoader().getResourceAsStream(
							"WrappedFieldChangedEvent.json"));
			
			tempStr = fieldChangedEventStr.replaceFirst("#exchange#", rabbitMQExchange);
			fieldChangedEventStr = tempStr.replaceFirst("#routingkey#", rabbitMQRoutingKey);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldChangedEventStr;
	}

}

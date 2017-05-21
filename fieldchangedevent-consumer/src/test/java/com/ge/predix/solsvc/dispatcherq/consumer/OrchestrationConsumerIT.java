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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.dto.Attribute;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;
import com.ge.predix.solsvc.dispatcherq.boot.FieldChangedEventConsumerApplication;
import com.ge.predix.solsvc.dispatcherq.consumer.handler.FieldChangedEventMessageHandler;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes =
{
        FieldChangedEventConsumerApplication.class
})
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:Test-solution-change-event-consumer.xml"
})
public class OrchestrationConsumerIT
{

    /**
     * 
     */
    static final Logger                     log = LoggerFactory.getLogger(OrchestrationConsumerIT.class);

    @Autowired
    private FieldChangedEventMessageHandler fieldChangedEventMessageHandler;

    @Autowired
	private Jackson2JsonMessageConverter jacksonMessageConverter;

    @Autowired
    private ModelFactory                    modelFactory;

    @Autowired
    private RestClient                      restClient;

    @Autowired
    private AssetConfig                     assetConfig;

    @Autowired
    private JsonMapper                      jsonMapper;

    @Autowired
    private TimeseriesClient                timeseriesClient;

    private List<Header>                    timeseriesHeaders;

    /**
     * @throws java.lang.Exception
     *             -
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
        // Do set up here
    }

    /**
     * @throws java.lang.Exception
     *             -
     */
    @AfterClass
    public static void tearDownAfterClass()
            throws Exception
    {
        // Cleanup after execution
    }

    /**
     * @throws java.lang.Exception
     *             -
     */
    @Before
    public void setUp()
            throws Exception
    {
        // setUp
    }

    /**
     * @throws java.lang.Exception
     *             -
     */
    @After
    public void tearDown()
            throws Exception
    {
        // clean up
    }

    /**
     * -
     */
    @Test
    public void testFieldChangedAndAlarmAccordingly()
    {

        List<Header> headers = setHeaders();

        setAlertStatus(headers, true);
        Integer actualDatapoint = new Integer(29);
        createDatapoint(actualDatapoint);

        Message msg = this.jacksonMessageConverter.toMessage(createFieldChangedEvent(), null);

        List<String> results = this.fieldChangedEventMessageHandler.onMessageDoWork(msg);

        verifyResponse(headers, results);
    }

    /**
     * @throws IOException -
     */
    @SuppressWarnings("nls")
    @Test
    public void testRunAnalyticTemplateVariableMatch()
            throws IOException
    {
        final String regex = "(\\{\\{)(.*)(\\}\\})";
        final String string = IOUtils
                .toString(getClass().getClassLoader().getResourceAsStream("alarmThresholdAnalyticTemplate.json"));

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find())
        {
            log.info("Full match: " + matcher.group(0));
            for (int i = 1; i <= matcher.groupCount(); i++)
            {
                log.info("Group " + i + ": " + matcher.group(i));
            }
        }

    }

    @SuppressWarnings("nls")
    private void createDatapoint(Integer actualValueOfSensor)
    {
        DatapointsIngestion dpIngestion = new DatapointsIngestion();
        dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));

        List<Object> datapoint1 = new ArrayList<Object>();
        datapoint1.add(System.currentTimeMillis());
        datapoint1.add(actualValueOfSensor);
        datapoint1.add(3); // quality

        List<Object> datapoints = new ArrayList<Object>();
        datapoints.add(datapoint1);

        Body body = new Body();
        body.setName("Compressor-2017:DischargePressure");
        body.setDatapoints(datapoints);

        List<Body> bodies = new ArrayList<Body>();
        bodies.add(body);

        dpIngestion.setBody(bodies);

        this.timeseriesClient.createTimeseriesWebsocketConnectionPool();
        this.timeseriesClient.postDataToTimeseriesWebsocket(dpIngestion);

        queryForLatestDatapoints(actualValueOfSensor);
    }

    @SuppressWarnings("nls")
    private void queryForLatestDatapoints(Integer actualValueOfSensor)
    {
        boolean done = false;
        while (!done)
        {
            com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery datapoints = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery();
            com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
            tag.setName("Compressor-2017:DischargePressure"); //$NON-NLS-1$

            List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tagList = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag>();
            tagList.add(tag);
            datapoints.setTags(tagList);
            this.timeseriesHeaders = this.timeseriesClient.getTimeseriesHeaders();
            com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse response = this.timeseriesClient
                    .queryForLatestDatapoint(datapoints, this.timeseriesHeaders);
            assertNotNull(response);
            try
            {
                assertEquals(((List<?>) response.getTags().get(0).getResults().get(0).getValues().get(0)).get(1),
                        actualValueOfSensor);
                done = true;
            }
            catch (AssertionError e)
            {
                try
                {
                    log.warn("timeseries value=" + actualValueOfSensor + " is not available yet, sleeping");
                    Thread.sleep(3000);
                }
                catch (InterruptedException e1)
                {
                    throw new RuntimeException(e1);
                }
            }

        }
    }

    @SuppressWarnings("nls")
    private void verifyResponse(List<Header> headers, List<String> results)
    {

        log.debug("Results = " + results);
        Assert.assertNotNull(results);
        for (String result : results)
        {
            Assert.assertTrue(!result.contains("errorMsg"));
        }

        List<Object> models = this.modelFactory
                .getModels("/asset/compressor-2017.alert-status.crank-frame-discharge-pressure", "Asset", headers);

        if ( ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertLevelValue")).getValue()
                .get(0) instanceof Integer )
        {
            Integer actualValueOfSensor = (Integer) ((Attribute) ((Asset) models.get(0)).getAttributes()
                    .get("alertLevelValue")).getValue().get(0);

            if ( actualValueOfSensor > 23 )
            {
                Assert.assertEquals(true,
                        ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus")).getValue().get(0));
            }
            else
            {
                Assert.assertEquals(false,
                        ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus")).getValue().get(0));
            }
        }
        else if ( ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertLevelValue")).getValue()
                .get(0) instanceof Double )
        {
            Double actualValueOfSensor = (Double) ((Attribute) ((Asset) models.get(0)).getAttributes()
                    .get("alertLevelValue")).getValue().get(0);

            if ( actualValueOfSensor > 23 )
            {
                Assert.assertEquals(true,
                        ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus")).getValue().get(0));
            }
            else
            {
                Assert.assertEquals(false,
                        ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus")).getValue().get(0));
            }
        }

    }

    @SuppressWarnings("nls")
    private void setAlertStatus(List<Header> headers, Boolean status)
    {
        List<Object> models = this.modelFactory
                .getModels("/asset/compressor-2017.alert-status.crank-frame-discharge-pressure", "Asset", headers);

        ((Attribute) ((Asset) models.get(0)).getAttributes().get("alertStatus")).getValue().set(0, status);

        this.modelFactory.updateModel(models.get(0), "Asset", headers);
    }

    @SuppressWarnings("nls")
    private List<Header> setHeaders()
    {

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        Header header = new BasicHeader("Content-Type", "application/json");
        headers.add(header);
        header = new BasicHeader("Accept", "application/json");
        headers.add(header);

        return headers;
    }

    @SuppressWarnings("nls")
    private FieldChangedEvent createFieldChangedEvent()
    {
        FieldChangedEvent fieldChangedEvent = new FieldChangedEvent();

        try
        {
            String fieldChangedEventJsonString = IOUtils
                    .toString(getClass().getClassLoader().getResourceAsStream("FieldChangedEvent.json"));

            fieldChangedEvent = this.jsonMapper.fromJson(fieldChangedEventJsonString, FieldChangedEvent.class);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fieldChangedEvent;
    }

}

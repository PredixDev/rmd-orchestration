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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.dsp.pm.ext.entity.asset.Asset;
import com.ge.dsp.pm.ext.entity.asset.AssetList;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValueList;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.ext.entity.util.map.Entry;
import com.ge.dsp.pm.solution.service.entity.fieldchanged.FieldChanged;
import com.ge.dsp.pm.solution.service.entity.fieldchanged.FieldChangedList;
import com.ge.dsp.pm.solution.service.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherqproducer.api.NotifyFieldChangedEvent;

/**
 * 
 * @author 212367843
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/field-change-event-producer.xml")
@PropertySource("classpath:application.properties")
public class NotifyFieldChangedEventToRabbitMQueueTestHarness
{

    private static final Logger log = LoggerFactory.getLogger(NotifyFieldChangedEventToRabbitMQueueTestHarness.class); 
    
    @Autowired
    private NotifyFieldChangedEvent notifyToQ;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass()
            throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
            throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown()
            throws Exception
    {
    }

    @Test
    public void testNotifyFieldChangedEvent()
    {
        System.out.println("Starting test...Notify Field Changed Event");
        System.out.println("=======================================");
        
        FieldChangedEvent data = createFieldChangedEvent(81L, "Reported Distance", "", parseDate("2012-09-11T07:16:13"), null, null);
        
        System.out.println("=======================================" + data); 
        
        notifyToQ.notify(data);
        
    }
    
    private FieldChangedEvent createFieldChangedEvent(long triggerField, String triggerFieldName, String assetId,
            Date time, Object externalMapKey, Object externalMapValue)
    {
        FieldChangedEvent fieldChangedEvent = new FieldChangedEvent();

        FieldChanged fieldChanged = new FieldChanged();
        com.ge.dsp.pm.ext.entity.util.map.Map attributeMap = new com.ge.dsp.pm.ext.entity.util.map.Map();
        Entry entry = new Entry();
        entry.setKey(externalMapKey);
        entry.setValue(externalMapValue);
        attributeMap.getEntry().add(entry);
        fieldChanged.setExternalAttributeMap(attributeMap);

        fieldChanged.setTimeChanged(getXMLDate(time));

        FieldIdentifierValueList fieldIdentifierValueList = new FieldIdentifierValueList();
        List<FieldIdentifierValue> fieldIdentifierValues = new ArrayList<FieldIdentifierValue>();

        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        // fieldIdentifier.setId(7l); fieldIdentifier.setName("sensorHeight");
        // //Tank Volume Orchestration

        fieldIdentifier.setId(triggerField);
        fieldIdentifier.setName(triggerFieldName); // Temperature Converted Flux
                                                   // Orchestration
        fieldIdentifierValue.setFieldIdentifier(fieldIdentifier);
        fieldIdentifierValues.add(fieldIdentifierValue);
        fieldIdentifierValueList.setFieldIdentifierValue(fieldIdentifierValues);
        fieldChanged.setFieldIdentifierValueList(fieldIdentifierValueList);

        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(2);
        solutionIdentifier.setName("WaterRMD123");
        fieldChanged.setSolutionIdentifier(solutionIdentifier);

        AssetList assetList = new AssetList();
        Asset asset = new Asset();

        FieldIdentifier assetFieldIdentifier = new FieldIdentifier();
        assetFieldIdentifier.setId(5);
        assetFieldIdentifier.setName("assetId");
        asset.setAssetIdFieldIdentifier(assetFieldIdentifier);

        // FieldMap fieldMap = new FieldMap();

        // fieldMap.getFieldEntry().add(
        // createFieldEntry(1, "customer", "EBMUD", 1));
        // fieldMap.getFieldEntry().add(
        // createFieldEntry(2, "site", "San Ramon", 2));
        // fieldMap.getFieldEntry().add(createFieldEntry(3, "line", "CMS", 3));
        // fieldMap.getFieldEntry().add(
        // createFieldEntry(4, "assetType", "water tank", 4));
        // fieldMap.getFieldEntry()
        // .add(createFieldEntry(5, "assetId", "12099", 5));

        org.mimosa.osacbmv3_3.Asset osaAsset = new org.mimosa.osacbmv3_3.Asset();
        // osaAsset.setSerialNo("12099"); //water tank - tank volume
        // orchestration

        osaAsset.setSerialNo(assetId); // water train - temperature converted
                                       // flux orchestration
        //asset.setOsaAsset(osaAsset);

        assetList.getAsset().add(asset);

        fieldChanged.setAssetList(assetList);

        // com.ge.dsp.schemas.pm.entity.util.map.Map attributeMap = new
        // com.ge.dsp.schemas.pm.entity.util.map.Map();
        // Entry entry = new Entry();
        // entry.setKey(1);
        // entry.setValue(1);
        // attributeMap.getEntry().add(entry);
        // fieldChanged.setExternalAttributeMap(attributeMap);

        List<FieldChanged> lstFieldChanged = new ArrayList<FieldChanged>();
        lstFieldChanged.add(fieldChanged);

        FieldChangedList fieldChangedList = new FieldChangedList();
        fieldChangedList.getFieldChanged().addAll(lstFieldChanged);
        fieldChangedEvent.setFieldChangedList(fieldChangedList);
        return fieldChangedEvent;
    }
    
    private static XMLGregorianCalendar getXMLDate(Date dateTime)
    {
        DatatypeFactory f = null;
        try
        {
            f = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e)
        {
            throw new RuntimeException("convert to runtime exception", e);
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(dateTime);
        XMLGregorianCalendar xmLGregorianCalendar = f.newXMLGregorianCalendar(cal);
        return xmLGregorianCalendar;
    }
    
    private static Date parseDate(String gmtTimeString)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            Date date = df.parse(gmtTimeString);
            return date;
        }
        catch (ParseException e)
        {
            throw new RuntimeException("convrt to runtime exception", e);
        }
    }

}

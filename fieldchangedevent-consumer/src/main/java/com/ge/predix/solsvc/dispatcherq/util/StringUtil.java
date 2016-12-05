/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.dispatcherq.util;

/**
 * 
 * @author 212367843
 */

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

import org.apache.log4j.Logger;

/**
 * Utilities for String manipulation
 * 
 * @author tturner
 * 
 */
@SuppressWarnings("nls")
public class StringUtil
{

	private static Logger logger = Logger.getLogger(StringUtil.class);
    /**
     * @param enums -
     * @return -
     */
    public static <T extends Enum<T>> List<String> enumToString(List<T> enums)
    {
        List<String> stringList = new ArrayList<String>();
        for (T theEnum : enums)
        {
            stringList.add(theEnum.name());
        }
        return stringList;
    }



    /**
     * e.g. "2012-09-11T10:10:10"
     * 
     * @param dateTime -
     * @return -
     */
    public static XMLGregorianCalendar getXMLDate(Date dateTime)
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

    /**
     * @param d1 -
     * @return -
     */
    public static String convertToDateTimeString(Date d1)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String dString = df.format(d1);
        
        return dString;
    }

    /**
     * @param gmtTimeString -
     * @return -
     */
    public static Date parseDate(String gmtTimeString)
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
        	logger.error("Date parsing exception : ",e);
            throw new RuntimeException("convrt to runtime exception", e);
        }
    }

}


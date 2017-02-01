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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherqproducer.api.NotifyFieldChangedEvent;

/**
 * 
 * @author 212367843
 */
@PropertySource("classpath:application.properties")
public class NotifyFieldChangedEventToRabbitMQueue
        implements NotifyFieldChangedEvent
{
    private static final Logger logger = LoggerFactory.getLogger(NotifyFieldChangedEventToRabbitMQueue.class.getName());
    
    @Autowired
    private RabbitTemplate fieldChangedEventTemplate;

    @Autowired
    private MessageConverter messageConverter;
    
    @Value("${fieldChangedEvent.MainQueue}")
    private String mainQ;
    
    
    @Override
    public boolean notify(FieldChangedEvent data)
    {

        logger.debug("In notify......"); //$NON-NLS-1$
        Message msg = this.messageConverter.toMessage(data, null);
        
        logger.debug("In notify......mainq = " + this.mainQ); //$NON-NLS-1$
        this.fieldChangedEventTemplate.convertAndSend(this.mainQ, msg);
        
        return true;
    }

}

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

import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.ge.predix.solsvc.dispatcherqproducer.api.NotifyFieldChangedEvent;

/**
 * 
 * @author 212367843
 */
public class NotifyFieldChangedEventToKafkaQ
        implements NotifyFieldChangedEvent
{

    /* (non-Javadoc)
     * @see com.ge.predix.dispatcherqproducer.api.notifyFieldChangedEvent#notify(com.ge.predix.solution.service.fieldchanged.FieldChangedEvent)
     */
    @Override
    public boolean notify(FieldChangedEvent data)
    {
        // TODO Auto-generated method stub
        return false;
    }

}

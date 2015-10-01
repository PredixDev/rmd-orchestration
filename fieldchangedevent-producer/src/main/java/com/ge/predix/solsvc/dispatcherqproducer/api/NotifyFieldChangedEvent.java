/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.dispatcherqproducer.api;

import com.ge.dsp.pm.solution.service.fieldchanged.FieldChangedEvent;

/**
 * 
 * @author 212367843
 */
public interface NotifyFieldChangedEvent
{

        public boolean notify(FieldChangedEvent data);
}

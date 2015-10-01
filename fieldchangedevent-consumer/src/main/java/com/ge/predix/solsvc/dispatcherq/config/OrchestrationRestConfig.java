/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.dispatcherq.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author 212367843
 */
public class OrchestrationRestConfig
{
    @Value("${predix.restProtocol:https}")
    private String  restProtocol;
    @Value("${predix.restHost:localhost}")
    private String  restHost;
    @Value("${predix.restPort:9093}")
    private String  restPort;
    @Value("${predix.restBaseResource:service}")
    private String  restBaseResource;

    /**
     * @return full URI
     */
    @SuppressWarnings("nls")
    public String getOrchestrationEndpoint()
    {
        return this.restProtocol + "://" + this.restHost + ":" + this.restPort + "/" + this.restBaseResource;
    }
}

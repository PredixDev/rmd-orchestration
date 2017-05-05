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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 212367843
 */
@Component
@Profile("local")
@SuppressWarnings("nls")
public class OrchestrationRestConfig
        implements EnvironmentAware, IOrchestrationRestConfig
{

    // Predix orchestration runtime endpoint
    @Value("${predix.orchestration.zoneId}")
    private String orchZoneId;    
    @Value("${predix.orchestration}")
    private String orchEndpoint;
    @Value("${predix.orchestration.restBaseResource:service}")
    private String orchRestBaseResource;

    // RMD analytics endpoint
    @Value("${predix.analytic.restProtocol:http}")
    private String analyticRestProtocol;
    @Value("${predix.analytic.restHost:localhost}")
    private String analyticRestHost;
    @Value("${predix.analytic.restPort:9093}")
    private String analyticRestPort;
    @Value("${predix.analytic.restBaseResource:service}")
    private String analyticRestBaseResource;

    @Value("${predix.analytic.endpoint:dummy}")
    private String analyticsEndpoint;

    @Value("${predix.orch.connectionTimeout:10000}")
    private int    orchestrationConnectionTimeout;
    @Value("${predix.orch.socketTimeout:10000}")
    private int    orchestrationSocketTimeout;
       
    private static final String ORCHESTRATION_VCAPS_NAME = "predix_orchestration";   //$NON-NLS-1$

    /**
     * @return full URI
     */
    @Override
    public String getOrchestrationEndpoint()
    {
        return this.orchEndpoint + "/" + this.orchRestBaseResource;
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.dispatcherq.config.IOrchestrationRestConfig#
     * getAnalyticsEndpoint()
     */
    @Override
    public String getAnalyticsEndpoint()
    {
        if ( this.analyticsEndpoint == null || this.analyticsEndpoint.contains("dummy") )
        {
            return this.analyticRestProtocol + "://" + this.analyticRestHost + ":" + this.analyticRestPort + "/"
                    + this.analyticRestBaseResource;
        }
        return this.analyticsEndpoint;
    }

    @Override
    public String getZoneId()
    {
        return this.orchZoneId;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
     * springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment env)
    {
        String vcapPropertyName = null;
        String tsName = env.getProperty(ORCHESTRATION_VCAPS_NAME); // this is set
                                                                 // on the
                                                                 // manifest
                                                                 // of the
                                                                 // application

        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + tsName + ".credentials.execution_uri";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) )
        {
            this.orchEndpoint = env.getProperty(vcapPropertyName);

        }
    }

    /**
     * @return -
     */
    public int getOrchestrationConnectionTimeout()
    {
        return this.orchestrationConnectionTimeout;
    }

    /**
     * @return -
     */
    public int getOrchestrationSocketTimeout()
    {
        return this.orchestrationSocketTimeout;
    }
}

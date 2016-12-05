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
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 212367843
 */
@Component
@Profile("cloud")
public class OrchestrationRestCloudConfig  implements EnvironmentAware, IOrchestrationRestConfig
{
    // Predix orchestration runtime endpoint
    @Value("${predix_orchestration_restProtocol:http}")
    private String  orchRestProtocol;
    @Value("${predix_orchestration_restHost:localhost}")
    private String  orchRestHost;
    @Value("${predix_orchestration_restPort:443}")
    private String  orchRestPort;
    @Value("${predix_orchestration_restBaseResource:service}")
    private String  orchRestBaseResource;
    
    // RMD analytics endpoint
    @Value("${predix_analytic_restProtocol:http}")
    private String  analyticRestProtocol;
    @Value("${predix_analytic_restHost:localhost}")
    private String  analyticRestHost;
    @Value("${predix_analytic_restPort:443}")
    private String  analyticRestPort;
    @Value("${predix_analytic_restBaseResource:service}")
    private String  analyticRestBaseResource;
    
    @Value("${predix.orchestration.zoneId}")
    private String orchZoneId;

    /**
     * @return full URI
     */
    @Override
    public String getOrchestrationEndpoint()
    {
        return this.orchRestProtocol + "://" + this.orchRestHost + ":" + this.orchRestPort + "/" + this.orchRestBaseResource; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    @Override
    public String getZoneId()
    {
        return this.orchZoneId;
    }

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.dispatcherq.config.IOrchestrationRestConfig#getAnalyticsEndpoint()
     */
    @Override
    public String getAnalyticsEndpoint()
    {
        return this.analyticRestProtocol + "://" + this.analyticRestHost + ":" + this.analyticRestPort + "/" + this.analyticRestBaseResource; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /* (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment arg0)
    {
        // TODO Auto-generated method stub
        
    }
}

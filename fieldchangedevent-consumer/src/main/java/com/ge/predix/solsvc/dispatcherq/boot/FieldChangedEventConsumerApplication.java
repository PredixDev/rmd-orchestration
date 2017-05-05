/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.dispatcherq.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 
 * @author 212367843
 */
@EnableAutoConfiguration
@Configuration
@ImportResource(
{
        "classpath:META-INF/spring/solution-change-event-consumer.xml",
        "classpath:/META-INF/spring/ext-util-scan-context.xml",
        "classpath:META-INF/spring/predix-rest-client-scan-context.xml"

})
public class FieldChangedEventConsumerApplication
{
    /**
     * @param args - initial args
     * @throws InterruptedException -
     */
    public static void main(String[] args)
            throws InterruptedException
    {
        SpringApplication.run(FieldChangedEventConsumerApplication.class, args);
    }
}

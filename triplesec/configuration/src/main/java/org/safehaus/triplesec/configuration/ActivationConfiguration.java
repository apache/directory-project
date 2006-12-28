/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.configuration;


/**
 * Configuration for activation application.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ActivationConfiguration
{
    public static final boolean ENABLE_DECOY_MIDLET_DEFAULT = false;
    public static final int OTP_LENGTH_DEFAULT = 6;
    public static final String MIDLET_NAME_ATTRIBUTE_DEFAULT = "midletNameAttribute";
    
    private boolean enableDecoyMidlet = ENABLE_DECOY_MIDLET_DEFAULT;
    private int otpLength = OTP_LENGTH_DEFAULT;
    private String midletNameAttribute = MIDLET_NAME_ATTRIBUTE_DEFAULT;
    private String activationBaseUrl = null;
    
    
    public void setEnableDecoyMidlet( boolean enableDecoyMidlet )
    {
        this.enableDecoyMidlet = enableDecoyMidlet;
    }
    
    
    public boolean isEnableDecoyMidlet()
    {
        return enableDecoyMidlet;
    }


    public void setOtpLength( int otpLength )
    {
        this.otpLength = otpLength;
    }


    public int getOtpLength()
    {
        return otpLength;
    }


    public void setMidletNameAttribute( String midletNameAttribute )
    {
        this.midletNameAttribute = midletNameAttribute;
    }


    public String getMidletNameAttribute()
    {
        return midletNameAttribute;
    }
    
    
    public void setActivationBaseUrl( String activationBaseUrl )
    {
        this.activationBaseUrl = activationBaseUrl;
    }
    
    
    public String getActivationBaseUrl()
    {
        return activationBaseUrl;
    }
}

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


import org.apache.directory.server.configuration.ServerStartupConfiguration;


/**
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecStartupConfiguration extends ServerStartupConfiguration
{
    private static final long serialVersionUID = -7138616822614155454L;

    private boolean enableHttp = true;
    private int httpPort = 8383;
    private String presentationBaseUrl;
    private ActivationConfiguration activationConfiguration = new ActivationConfiguration();
    private SmsConfiguration smsConfiguration = new SmsConfiguration();
    private SmtpConfiguration smtpConfiguration = new SmtpConfiguration();

    
    public TriplesecStartupConfiguration()
    {
    }
    
    
    public SmsConfiguration getSmsConfiguration()
    {
        return smsConfiguration;
    }
    
    
    protected void setSmsConfiguration( SmsConfiguration smsConfiguration )
    {
        this.smsConfiguration = smsConfiguration;
    }
    
    
    public SmtpConfiguration getSmtpConfiguration()
    {
        return smtpConfiguration;
    }
    
    
    protected void setSmtpConfiguration( SmtpConfiguration smtpConfiguration )
    {
        this.smtpConfiguration = smtpConfiguration;
    }


    public ActivationConfiguration getActivationConfiguration()
    {
        return activationConfiguration;
    }
    
    
    protected void setActivationConfiguration( ActivationConfiguration activationConfiguration )
    {
        this.activationConfiguration = activationConfiguration;
    }


    protected void setEnableHttp( boolean enableHttpService )
    {
        this.enableHttp = enableHttpService;
    }


    public boolean isEnableHttp()
    {
        return enableHttp;
    }


    protected void setHttpPort( int httpServicePort )
    {
        this.httpPort = httpServicePort;
    }


    public int getHttpPort()
    {
        return httpPort;
    }


    protected void setPresentationBaseUrl( String presentationBaseUrl )
    {
        this.presentationBaseUrl = presentationBaseUrl;
    }


    public String getPresentationBaseUrl()
    {
        return presentationBaseUrl;
    }
}

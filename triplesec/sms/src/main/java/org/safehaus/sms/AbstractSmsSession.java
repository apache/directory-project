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
package org.safehaus.sms;


/**
 * An abstract SmsSession where everything except the sendMessage method is implemented.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public abstract class AbstractSmsSession implements SmsSession
{
    /** the session identifier associated with this SmsSession */
    private String sessionId;
    /** the SMS message transport type associated with this SmsSession */
    private SmsTransportType transport;
    /** the user account associated with this SmsSession */
    private String user;
    /** the application identifier associated with this SmsSession */
    private String applicationId;
    /** flag used to toggle keep alive aspect of this SmsSession */
    private boolean isKeepAliveEnabled;


    public String getSessionId()
    {
        return sessionId;
    }


    /**
     * Sets the session identifier for this SmsSession instance.
     *
     * @param sessionId the session identifier
     */
    protected void setSessionId( String sessionId )
    {
        this.sessionId = sessionId;
    }


    public SmsTransportType getTransport()
    {
        return transport;
    }


    /**
     * Sets the transport type for this SmsSession instance.
     *
     * @param transport the transport type used by this session
     */
    protected void setTransport( SmsTransportType transport )
    {
        this.transport = transport;
    }


    public String getUser()
    {
        return user;
    }


    /**
     * Sets the user that is associated with this SmsSession instance.
     *
     * @param user the user account associated with this SmsSession
     */
    protected void setUser( String user )
    {
        this.user = user;
    }


    public String getApplicationId()
    {
        return applicationId;
    }


    /**
     * Sets the application identifier associated with this SmsSession instance.
     *
     * @param applicationId the application identifier associated with this SmsSession instance
     */
    protected void setApplicationId( String applicationId )
    {
        this.applicationId = applicationId;
    }


    public boolean isKeepAliveEnabled()
    {
        return isKeepAliveEnabled;
    }


    /**
     * Sets whether or not this SmsSession will be kept alive by the implementation or it
     * will time out.
     *
     * @param isKeepAliveEnabled true means the session is preserved, false means it could
     * time out at any point in time unless immediately used.
     */
    protected void setKeepAliveEnabled( boolean isKeepAliveEnabled )
    {
        this.isKeepAliveEnabled = isKeepAliveEnabled;
    }
}

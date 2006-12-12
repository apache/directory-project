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


import java.io.IOException;


/**
 * An SMS session interface is required to send SMS messages.  Gateways require a
 * an authenticated session to send SMS messages.  A session can be used repeatedly
 * to send SMS messages especially if they are keep alive enabled.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public interface SmsSession
{
    /**
     * Sends an SMS message using this session.
     *
     * @param msg the SMS message to send
     * @return the result of 0 for success or nonzero error code
     * @throws SmsException
     * @throws IOException
     */
    int sendMessage( SmsMessage msg ) throws SmsException, IOException;

    /**
     * Gets the unique session identifier associated with this SmsSession.
     *
     * @return the session identifier
     */
    String getSessionId();

    /**
     * Gets the transport type associated with this SmsMessage.
     *
     * @return the SMS message transport type
     */
    SmsTransportType getTransport();

    /**
     * Gets the name of the provider/gateway user account this session is associated with.
     *
     * @return the name of the user account
     */
    String getUser();

    /**
     * Gets the application identifier associated with this session.  Multiple appss
     * may be associated with the same user account.
     *
     * @return the application identifier associated with this session
     */
    String getApplicationId();

    /**
     * Checks to see if keep alives are enabled for this SMS session.
     *
     * @return true if keep alives are enabled for this session
     */
    boolean isKeepAliveEnabled();

    /**
     * Invalidates the session object.
     *
     * @return true if the session is invalidated, false otherwise
     */
    boolean invalidate();
}

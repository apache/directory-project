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
 * An SMS session factory creates SmsSessions used to send SmsMessages.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class SmsSessionFactory
{
    /** the name of the default concrete factory implementation to use */
    private static final String DEFAULT_FACTORY = "org.safehaus.sms.smpp.SmppSmsSessionFactory";
    /** a reusable instance of the default smss session factory */
    private static SmsSessionFactory defaultInstance;


    /**
     * Gets a handle on an existing SmsSession using the session id.
     *
     * @param sessionId the unique identifier for the current session
     * @return the SMS session object associated with the id
     */
    public SmsSession getSmsSession( String sessionId )
    {
        SmsSessionFactory overridden = getOverridenFactory();

        if ( overridden == null )
        {
            return getDefaultInstance().getSmsSession( sessionId );
        }

        return getDefaultInstance().getSmsSession( sessionId );
    }


    /**
     * Attempts to lookup the overrriden factory whose class name is supplied using
     * the system property org.safehaus.sms.SmsSessionFactory.  If the property is not
     * specified then null is returned.
     *
     * @return the overriden factory or null if no override is specified
     */
    private SmsSessionFactory getOverridenFactory()
    {
        String override = System.getProperty( "org.safehaus.sms.SmsSessionFactory" );

        if ( override != null )
        {
            try
            {
                return ( SmsSessionFactory ) Class.forName( override ).newInstance();
            }
            catch ( InstantiationException e )
            {
                e.printStackTrace();
            }
            catch ( IllegalAccessException e )
            {
                e.printStackTrace();
            }
            catch ( ClassNotFoundException e )
            {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * Gets the default instance creating it if need be.
     *
     * @return the default instance
     */
    private SmsSessionFactory getDefaultInstance()
    {
        if ( defaultInstance != null )
        {
            return defaultInstance;
        }

        try
        {
            defaultInstance = ( SmsSessionFactory ) Class.forName( DEFAULT_FACTORY ).newInstance();
        }
        catch ( InstantiationException e )
        {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
        }

        return defaultInstance;
    }


    /**
     * Gets an SMS session object for sending SMS messages.  The transport used
     * will be determined by the underlying SMS provider.  The returned SmsSession
     * may or may not have the keep alive feature enabled.
     *
     * @param user the username of the account the session is associated with
     * @param password the password of the user
     * @param applicationId the application identifier associated with the account
     * @return an SMS session object for the specified parameters
     */
    public SmsSession getSmsSession( String user, String password, String applicationId )
            throws SmsException, IOException
    {
        SmsSessionFactory overridden = getOverridenFactory();

        if ( overridden == null )
        {
            return getDefaultInstance().getSmsSession( user, password, applicationId );
        }

        return getDefaultInstance().getSmsSession( user, password, applicationId );
    }


    /**
     * Gets an SMS session object for sending SMS messages while requesting a
     * specific transport type if it is available.  There is no guarrantee the
     * underlying provider supports the requested transport.  No exception is thrown
     * if the requested transport is unavailable.  Instead some default transport is
     * returned.  Users should not presume their requests are satisfied.
     *
     * @param user the username of the account the session is associated with
     * @param password the password of the user
     * @param applicationId the application identifier associated with the account
     * @param transport the SMS transport type requested
     * @return an SMS session object for the specified parameters
     */
    public SmsSession getSmsSession( String user, String password, String applicationId,
                                     SmsTransportType transport ) throws SmsException, IOException
    {
        SmsSessionFactory overridden = getOverridenFactory();

        if ( overridden == null )
        {
            return getDefaultInstance().getSmsSession( user, password, applicationId, transport );
        }

        return getDefaultInstance().getSmsSession( user, password, applicationId, transport );
    }


    /**
     * Gets an SMS session object for sending SMS messages with a transport type and
     * keep alive request.  If the underlying provider/gateway does not support the
     * transport type or keep alives then these will not be set within the SmsSession
     * object returned.  Users should not presume their requests are satisfied.
     *
     * @param user the username of the account the session is associated with
     * @param password the password of the user
     * @param applicationId the application identifier associated with the account
     * @param transport the SMS transport type requested
     * @param enableKeepAlive if true requests the session be kept alive, false otherwise
     * @return an SMS session object for the specified parameters
     */
    public SmsSession getSmsSession( String user, String password, String applicationId,
                                              SmsTransportType transport, boolean enableKeepAlive )
            throws SmsException, IOException
    {
        SmsSessionFactory overridden = getOverridenFactory();

        if ( overridden == null )
        {
            return getDefaultInstance().getSmsSession( user, password, applicationId, transport, enableKeepAlive );
        }

        return getDefaultInstance().getSmsSession( user, password, applicationId, transport, enableKeepAlive );
    }


    /**
     * Gets an SMS session object for sending SMS messages with the default transport
     * type supported by the underlying provider while requesting keep alives to be
     * enabled or disabled.  If the underlying provider/gateway does not support the
     * keep alives or the lack there of then these will not be set within the SmsSession
     * object returned.  Users should not presume their requests are satisfied.
     *
     * @param user the username of the account the session is associated with
     * @param password the password of the user
     * @param applicationId the application identifier associated with the account
     * @param enableKeepAlive if true requests the session be kept alive, false otherwise
     * @return an SMS session object for the specified parameters
     */
    public SmsSession getSmsSession( String user, String password, String applicationId,
                                              boolean enableKeepAlive ) throws SmsException, IOException
    {
        SmsSessionFactory overridden = getOverridenFactory();

        if ( overridden == null )
        {
            return getDefaultInstance().getSmsSession( user, password, applicationId, enableKeepAlive );
        }

        return getDefaultInstance().getSmsSession( user, password, applicationId, enableKeepAlive );
    }
}

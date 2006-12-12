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
 * A type safe enumeration for SMS transport types.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class SmsTransportType
{
    /** the HTTP transport type */
    public final static SmsTransportType HTTP = new SmsTransportType( "http", 0 );
    /** the HTTPS transport type */
    public final static SmsTransportType HTTPS = new SmsTransportType( "https", 1 );
    /** SMPP/TCP/IP Transport type */
    public static final SmsTransportType SMPP = new SmsTransportType( "smpp", 2 );

    /** the ordinal for this type safe enumeration */
    private final int ordinal;
    /** the scheme name for the transport protocol used */
    private final String scheme;


    /**
     * Creates a type safe enumeration for SMS message transport types.
     *
     * @param scheme the scheme name for the transport protocol
     * @param ordinal the ordinal for the type safe enumeration
     */
    private SmsTransportType( String scheme, int ordinal )
    {
        this.scheme = scheme;
        this.ordinal = ordinal;
    }


    /**
     * Gets the scheme name for the SMS message transport.
     *
     * @return the scheme name for the transport
     */
    public String getScheme()
    {
        return this.scheme;
    }


    /**
     * Gets the ordinal for this type safe enumeration.
     *
     * @return the ordinal for this type safe enumeration
     */
    public int getOrdinal()
    {
        return this.ordinal;
    }
}

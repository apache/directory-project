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
package org.safehaus.triplesec.demo.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class DefaultSmsManager implements SmsManager {
    private String smsTransportUrl;
    private String smsUsername;
    private String smsPassword;
    private String smsAccountName;
    private String wapUrl;

    public void sendSmsMessage( String mobile, String carrier, String username )
            throws Exception
    {
        // Note: we will rely on clients to provide non-null input variables
        // and just throw RuntimeExceptions if we get a null value
        if ( mobile == null || carrier == null || username == null )
        {
            throw new RuntimeException( "Input variable(s) have illegal NULL values" );
        }

        // trim mobile number if necessary...
        if ( mobile.startsWith( "+" ) )
        {
            mobile = mobile.substring( 1 );
        }
        if ( mobile.startsWith( "1" ) )
        {
            mobile = mobile.substring( 1 );
        }
        if ( mobile.length() < 10 )
        {
            throw new RuntimeException( "Mobile number was less than 10 digits in length" );
        }

        // build WAP URL
        StringBuffer buffer = new StringBuffer( wapUrl );
        if ( ! wapUrl.endsWith("/") )
        {
            buffer.append( "/" );
        }
        buffer.append( username ).append( ".jar" );

        // set the cellular carrier code
        int carrierCode = getCarrierCode( carrier );

        // setup HTTP client
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod( smsTransportUrl );
        NameValuePair[] params = new NameValuePair[] {
                new NameValuePair( "Carrier", String.valueOf( carrierCode ) ),
                new NameValuePair( "UID", smsUsername ),
                new NameValuePair( "PWD", smsPassword ),
                new NameValuePair( "Campaign", smsAccountName ),
                new NameValuePair( "CellNumber", mobile ),
                new NameValuePair( "msg", buffer.toString() )
        };
        method.setQueryString( params );
        client.executeMethod( method );
    }

    private int getCarrierCode( String carrier )
    {
        int carrierCode = 31004;
        if ( carrier.equalsIgnoreCase( "AT&T" ) )
        {
            carrierCode = 31001;
        }
        else if ( carrier.equalsIgnoreCase( "Cingular" ) )
        {
            carrierCode = 31002;
        }
        else if ( carrier.equalsIgnoreCase( "Verizon" ) )
        {
            carrierCode = 31003;
        }
        else if ( carrier.equalsIgnoreCase( "T-Mobile" ) )
        {
            carrierCode = 31004;
        }
        else if ( carrier.equalsIgnoreCase( "Spring" ) )
        {
            carrierCode = 31005;
        }
        else if ( carrier.equalsIgnoreCase( "Nextel" ) )
        {
            carrierCode = 310007;
        }
        return carrierCode;
    }
    public void setSmsTransportUrl( String smsTransportUrl )
    {
        this.smsTransportUrl = smsTransportUrl;
    }

    public void setSmsUsername( String smsUsername )
    {
        this.smsUsername = smsUsername;
    }

    public void setSmsPassword( String smsPassword )
    {
        this.smsPassword = smsPassword;
    }

    public void setSmsAccountName( String smsAccountName )
    {
        this.smsAccountName = smsAccountName;
    }

    public void setWapUrl(String wapUrl)
    {
        this.wapUrl = wapUrl;
    }
}

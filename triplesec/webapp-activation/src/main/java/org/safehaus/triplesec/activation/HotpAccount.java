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
package org.safehaus.triplesec.activation;


import org.safehaus.sms.Carrier;

/**
 * Bean representing HOTP centric account information.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpAccount
{
    private final Carrier carrier;
    private final String cellularNumber;
    private final String emailAddress;
    private final String midletName;
    private final boolean deliveryMethodSms;
    private final String realm;
    private final String name;
    private final byte[] secret;
    private final String pin;
    private final long factor;
    
    
    public HotpAccount( Carrier carrier, String cellularNumber, String emailAddress, String midletName,
                        boolean deliveryMethodSms, String realm, String name, byte[] secret, String pin, long factor )
    {
        this.carrier = carrier;
        this.cellularNumber = cellularNumber;
        this.emailAddress = emailAddress;
        this.midletName = midletName;
        this.deliveryMethodSms = deliveryMethodSms;
        this.realm = realm;
        this.name = name;
        this.secret = secret;
        this.pin = pin;
        this.factor = factor;
    }


    public Carrier getCarrier()
    {
        return carrier;
    }


    public String getCellularNumber()
    {
        return cellularNumber;
    }


    public String getEmailAddress()
    {
        return emailAddress;
    }


    public String getMidletName()
    {
        return midletName;
    }


    public boolean isDeliveryMethodSms()
    {
        return deliveryMethodSms;
    }


    public String getRealm()
    {
        return realm;
    }


    public String getName()
    {
        return name;
    }


    public byte[] getSecret()
    {
        byte[] copy = new byte[secret.length];
        System.arraycopy( secret, 0, copy, 0, secret.length );
        return copy;
    }


    public String getPin()
    {
        return pin;
    }


    public long getFactor()
    {
        return factor;
    }
}

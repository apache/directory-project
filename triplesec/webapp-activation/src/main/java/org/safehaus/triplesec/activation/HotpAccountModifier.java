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


import java.io.UnsupportedEncodingException;

import org.safehaus.sms.Carrier;


/**
 * A modifier for a HotpAccount.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpAccountModifier
{
    private Carrier carrier;
    private String cellularNumber;
    private String emailAddress;
    private String midletName;
    private boolean deliveryMethodSms;
    private String realm;
    private String name;
    private byte[] secret;
    private String pin;
    private long factor;
    
    
    public void setCarrier( String carrierName )
    {
        if ( carrierName.equalsIgnoreCase( Carrier.ATT.getName() ) )
        {
            carrier = Carrier.ATT;
        }
        else if ( carrierName.equalsIgnoreCase( Carrier.CINGULAR.getName() ) )
        {
            carrier = Carrier.CINGULAR;
        }
        else if ( carrierName.equalsIgnoreCase( Carrier.NEXTEL.getName() ) )
        {
            carrier = Carrier.NEXTEL;
        }
        else if ( carrierName.equalsIgnoreCase( Carrier.SPRINT.getName() ) )
        {
            carrier = Carrier.SPRINT;
        }
        else if ( carrierName.equalsIgnoreCase( Carrier.T_MOBILE.getName() ) )
        {
            carrier = Carrier.T_MOBILE;
        }
        else if ( carrierName.equalsIgnoreCase( Carrier.VERIZON.getName() ) )
        {
            carrier = Carrier.VERIZON;
        }
        else
        {
            throw new IllegalArgumentException( "Unrecognized carrier name: " + carrierName );
        }
    }
    
    
    public Carrier getCarrier()
    {
        return carrier;
    }


    public void setCellularNumber( String cellularNumber )
    {
        this.cellularNumber = cellularNumber;
    }


    public String getCellularNumber()
    {
        return cellularNumber;
    }


    public void setEmailAddress( String emailAddress )
    {
        this.emailAddress = emailAddress;
    }


    public String getEmailAddress()
    {
        return emailAddress;
    }


    public void setMidletName( String midletName )
    {
        this.midletName = midletName;
    }


    public String getMidletName()
    {
        return midletName;
    }


    public void setDeliveryMethodSms( boolean deliveryMethodSms )
    {
        this.deliveryMethodSms = deliveryMethodSms;
    }


    public void setDeliveryMethodSms( String deliveryMethodSmsName )
    {
        if ( deliveryMethodSmsName.equalsIgnoreCase( "sms" ) )
        {
            this.deliveryMethodSms = true;
        }
        else
        {
            this.deliveryMethodSms = false;
        }
    }


    public boolean isDeliveryMethodSms()
    {
        return deliveryMethodSms;
    }


    public void setRealm( String realm )
    {
        this.realm = realm;
    }


    public String getRealm()
    {
        return realm;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    public void setSecret( Object secret )
    {
        if ( secret instanceof byte[] )
        {
            this.secret = ( byte[] ) secret;
        }
        else if ( secret instanceof String )
        {
            try
            {
                this.secret = ( ( String ) secret ).getBytes( "UTF-8" );
            }
            catch ( UnsupportedEncodingException e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                this.secret = String.valueOf( secret ).getBytes( "UTF-8" );
            }
            catch ( UnsupportedEncodingException e )
            {
                e.printStackTrace();
            }
        }
    }


    public byte[] getSecret()
    {
        return secret;
    }


    public void setPin( String pin )
    {
        this.pin = pin;
    }


    public String getPin()
    {
        return pin;
    }


    public void setFactor( long factor )
    {
        this.factor = factor;
    }


    public long getFactor()
    {
        return factor;
    }


    public HotpAccount create()
    {
        return new HotpAccount( carrier, cellularNumber, emailAddress, midletName, 
            deliveryMethodSms, realm, name, secret, pin, factor );
    }
}

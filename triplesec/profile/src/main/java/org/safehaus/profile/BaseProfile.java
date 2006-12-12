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
package org.safehaus.profile;


/**
 * A base Profile bean.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class BaseProfile implements Profile
{
    /** the lable or identifier for this profile */
    protected String label; 

    /** the shared secret */
    protected byte[] secret;

    /** the moving factor or counter */
    protected long factor;

    /** additional (optional) account info */
    protected String info = "";

    /** whether or not this profile is disabled */
    protected boolean disabled = false;

    /**
     * Creates a Profile bean with all properties set to defaults.
     */
    protected BaseProfile()
    {
        // do nothing
    }


    /**
     * Creates a new profile using a unique label, shared secret key, and moving
     * factor for the OTP based account.
     *
     * @param label a unique label for this BaseProfile
     * @param factor the moving factor (counter)
     * @param secret the 160 bit shared secret key
     */
    public BaseProfile( String label, long factor, byte[] secret )
    {
        this.label = label;
        this.secret = secret;
        this.factor = factor;
    }


    /**
     * Creates a new profile using a unique label, shared secret key, and moving
     * factor for the OTP based account.
     *
     * @param label a unique label for this BaseProfile
     * @param factor the moving factor (counter)
     * @param secret the shared secret key
     */
    public BaseProfile( String label, long factor, byte[] secret, String info )
    {
        this.label = label;

        this.secret = secret;

        this.factor = factor;

        this.info = info;
    }


    public String getLabel()
    {
        return label;
    }


    void setLabel( String label )
    {
        this.label = label;
    }


    public byte[] getSecret()
    {
        return secret;
    }


    void setSecret( byte[] secret )
    {
        this.secret = secret;
    }


    public long getFactor()
    {
        return factor;
    }


    void setFactor( long factor )
    {
        this.factor = factor;
    }


    void incrementFactor()
    {
        this.factor++;
    }


    public String getInfo()
    {
        return info;
    }


    void setInfo( String info )
    {
        this.info = info;
    }
    
    
    public boolean isDisabled()
    {
        return disabled;
    }
    
    
    void setDisabled( boolean disabled )
    {
        this.disabled = disabled;
    }
}

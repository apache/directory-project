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
 * A BaseProfile modifier.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class BaseProfileModifier
{
    /** the moveing factor delta */
    protected long factor;

    /** the optional account info delta */
    protected String info;

    /** the delta for the account label */
    protected String label;

    /** the delta for the shared secret */
    protected byte[] secret;

    private boolean disabled;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------


    /**
     * Creates a BaseProfileModifier without any initial values set.
     */
    public BaseProfileModifier()
    {
    }


    /**
     * Creates a BaseProfileModifier using initially the values of an existing profile.
     *
     * @param profile the profile to use for initial values
     */
    public BaseProfileModifier( Profile profile )
    {
        factor = profile.getFactor();
        info = profile.getInfo();
        label = profile.getLabel();
        secret = profile.getSecret();
    }


    // ------------------------------------------------------------------------
    // Builder method
    // ------------------------------------------------------------------------


    /**
     * Builds the profile using all the properties.
     *
     * @return the changed properties
     */
    public BaseProfile getProfile()
    { 
        BaseProfile profile = new BaseProfile( getLabel(), getFactor(), getSecret(), getInfo() );
        profile.setDisabled( this.disabled );
        return profile;
    }


    // ------------------------------------------------------------------------
    // Modifier methods
    // ------------------------------------------------------------------------

    
    /**
     * Set's whether or not this profile is disabled.
     */
    public void setDisabled( boolean disabled )
    {
        this.disabled = disabled;
    }
    

    /**
     * Sets the label used to identify the Profile
     *
     * @param label the new label for the Profile
     */
    public void setLabel( String label )
    {
        this.label = label;
    }


    /**
     * Sets the shared secret key used to generate the HOTP value.
     *
     * @param secret the shared secret key between client and server
     */
    public void setSecret( byte[] secret )
    {
        this.secret = secret;
    }


    /**
     * Sets the moving factor used to generate an OTP.
     *
     * @param factor the OTP moving factor (counter)
     */
    public void setFactor( long factor )
    {
        this.factor = factor;
    }


    /**
     * Increments the OTP moving factor (counter).  This is called after the
     * password is generated.  Then this Profile is serialized back to the
     * profile store.
     */
    public void incrementFactor()
    {
        factor = getFactor() + 1;
    }


    /**
     * Sets additional account information about this Profile.  Null values will
     * become empty Strings when serializing and resusitating Profile records.
     *
     * @param info additional account information about this Profile
     */
    public void setInfo( String info )
    {
        this.info = info;
    }


    // ------------------------------------------------------------------------
    // protected accessor methods
    // ------------------------------------------------------------------------


    /**
     * Gets the altered label associated with this ProfileModifier.
     *
     * @return the altered label that identifies this ProfileModifier
     */
    protected String getLabel()
    {
        return label;
    }


    /**
     * Gets the altered shared secret key used to generate the HOTP value.
     *
     * @return the altered shared secret key between client and server
     */
    protected byte[] getSecret()
    {
        return secret;
    }


    /**
     * The altered moving factor (counter) used to generate an OTP.
     *
     * @return altered the OTP moving factor (counter)
     */
    protected long getFactor()
    {
        return factor;
    }


    /**
     * Gets altered additional account information about this ProfileModifier.
     *
     * @return altered additional account information
     */
    protected String getInfo()
    {
        return info;
    }
}

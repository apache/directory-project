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
 * A BaseServerProfileModifier.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class BaseServerProfileModifier extends BaseProfileModifier
{
    /** the delta for the user id */
    private String id;

    /** the delta for the realm */
    private String realm;

    /** the delta for the resynchronization counts that have succeed */
    private int resynchCount;

    /** the delta for the number of failures to authenticate in epoch */
    private int failuresInEpoch;

    private String activationKey;
    
    private byte[] password;
    private String tokenPin;
    

    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------


    /**
     * Creates a BaseServerProfileModifier without any initial values.
     */
    public BaseServerProfileModifier()
    {
        super();
    }


    /**
     * Creates a BaseServerProfileModifier with initial values copied from an existing profile.
     *
     * @param profile the existing profile used for initial values
     */
    public BaseServerProfileModifier( ServerProfile profile )
    {
        super( profile );

        this.resynchCount = profile.getResynchCount();
        this.id = profile.getUserId();
        this.factor = profile.getFactor();
        this.realm = profile.getRealm();
        this.failuresInEpoch = profile.getFailuresInEpoch();
        this.info = profile.getInfo();
        this.activationKey = profile.getActivationKey();
        this.password = profile.getPassword();
        this.tokenPin = profile.getTokenPin();
    }


    // ------------------------------------------------------------------------
    // Builder method
    // ------------------------------------------------------------------------


    /**
     * Builds the profile using all its altered properties.
     *
     * @return the changed properties
     */
    public BaseServerProfile getServerProfile()
    {
        BaseServerProfile profile = new BaseServerProfile( this.id, this.realm, getLabel(), 
            getFactor(), getSecret(), getTokenPin(), getPassword() );
        profile.setInfo( info );
        profile.setFailuresInEpoch( failuresInEpoch );
        profile.setResynchCount( resynchCount );
        profile.setActivationKey( activationKey );
        return profile;
    }


    // ------------------------------------------------------------------------
    // Modifier methods
    // ------------------------------------------------------------------------

    
    public void setPassword( byte[] password )
    {
        this.password = password;
    }
    
    
    public void setTokenPin( String tokenPin )
    {
        this.tokenPin = tokenPin;
    }
    

    /**
     * Sets the number of successful consecutive resync operations that have
     * passed.
     *
     * @param resynchCount the number of successful resynch passes until now
     */
    public void setResynchCount( int resynchCount )
    {
        this.resynchCount = resynchCount;
    }


    /**
     * Gets the unique user id associated with this profile.
     *
     * @param id the unique user id associated with this profile
     */
    public void setUserId( String id )
    {
        this.id = id;
    }


    /**
     * Gets the authentication realm associated with this Profile.
     *
     * @param realm the authentication realm associated with this Profile
     */
    public void setRealm( String realm )
    {
        this.realm = realm;
    }


    /**
     * Sets the number of authentication failures within an epoch.
     *
     * @param failuresInEpoch the number of authentication failures within an epoch
     */
    public void setFailuresInEpoch( int failuresInEpoch )
    {
        this.failuresInEpoch = failuresInEpoch;
    }

    
    public void setActivationKey ( String activationKey )
    {
    	this.activationKey = activationKey;
    }
    

    // ------------------------------------------------------------------------
    // Protected accessorr methods
    // ------------------------------------------------------------------------


    protected String getTokenPin()
    {
        return tokenPin;
    }
    
    
    protected byte[] getPassword()
    {
        return password;
    }
    
    
    /**
     * Gets the unique user id associated with this profile.
     *
     * @return the unique user id associated with this profile
     */
    protected String getUserId()
    {
        return this.id;
    }


    /**
     * Gets the authentication realm associated with this Profile.
     *
     * @return the authentication realm associated with this Profile
     */
    protected String getRealm()
    {
        return this.realm;
    }


    /**
     * Gets the number of successful consecutive resync operations that have
     * passed.  This count is set to a negative number to denote that no resynch
     * is in progress.  When users are undergoing the resynch process they may
     * be asked to type in their password a certain number of times
     * consecutively.  Each time the user succeeds this counter is incremented.
     * When the user has successfully completed the resynch operation the
     * counter is set to a negative value.  If the user fails during resynch
     * the counter is set to 0.
     *
     * @return the number of successful resynch passes until now
     */
    protected int getResynchCount()
    {
        return this.resynchCount;
    }


    /**
     * Gets the number of authentication failures within an epoch.  The number
     * of authentication failures are tracked here within time periods
     * determined by the server.  If the user exceeds some threshold the account
     * is automatically locked to prevent brute force attacks.
     *
     * @return the number of authentication failures within an epoch
     */
    protected int getFailuresInEpoch()
    {
        return this.failuresInEpoch;
    }
}

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
 * The base profile implementation used by servers.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class BaseServerProfile extends BaseProfile implements ServerProfile
{
    /** the user id associated with this profile */
    private String id;
    /** the realm associated with this profile */
    private String realm;
    /** the successful resynch attempt count */
    private int resynchCount = -1;
    /** the number of auth failures within a server epoch */
    private int failuresInEpoch;
    /** the activation key for this profile if it has not yet been activated */
    private String activationKey;
    private String tokenPin;
    private String notifyBy = "sms";
    private byte[] password;

    
    /**
     * Creates a new profile using a unique label, shared secret key, and moving
     * factor for the OTP based account.
     *
     * @param id the user id associated with this profile
     * @param realm the authentication realm this profile is in
     * @param label a unique label for this BaseProfile
     * @param factor the moving factor (counter)
     * @param secret the 160 bit shared secret key
     */
    public BaseServerProfile( String id, String realm, String label, long factor, byte[] secret,
        String pin, byte[] password )
    {
        this.id = id;
        this.realm = realm;
        this.label = label;
        this.secret = secret;
        this.factor = factor;
        this.tokenPin = pin;
        this.password = password;
    }


    /**
     * Creates a new profile using a unique label, shared secret key, and moving
     * factor for the OTP based account.
     *
     * @param id the user id associated with this profile
     * @param realm the authentication realm this profile is in
     * @param label a unique label for this BaseProfile
     * @param factor the moving factor (counter)
     * @param secret the shared secret key
     */
    public BaseServerProfile( String id, String realm, String label, long factor, byte[] secret, 
        String pin, byte[] password, String info, String activationKey )
    {
        this.id = id;
        this.realm = realm;
        this.label = label;
        this.secret = secret;
        this.factor = factor;
        this.tokenPin = pin;
        this.password = password;
        this.info = info;
        this.activationKey = activationKey;
    }


    public String getUserId()
    {
        return id;
    }


    public String getRealm()
    {
        return realm;
    }


    public int getResynchCount()
    {
        return this.resynchCount;
    }


    public int getFailuresInEpoch()
    {
        return this.failuresInEpoch;
    }


    public boolean isActive()
    {
    	return activationKey == null || activationKey.length() == 0;
    }
    
    
    public String getActivationKey()
    {
    	return activationKey;
    }
    
    
    public String getTokenPin()
    {
        return tokenPin;
    }
    
    
    void setTokenPin( String tokenPin )
    {
        this.tokenPin = tokenPin;
    }
    
    
    public byte[] getPassword()
    {
        return password;
    }
    
    
    void setPassword( byte[] password )
    {
        this.password = password;
    }
    
    void setUserId( String id )
    {
        this.id = id;
    }


    void setRealm( String domain )
    {
        this.realm = domain;
    }


    void setResynchCount( int resynchCount )
    {
        this.resynchCount = resynchCount;
    }


    void setFailuresInEpoch( int failuresInEpoch )
    {
        this.failuresInEpoch = failuresInEpoch;
    }


    void setActivationKey( String activationKey )
    {
    	this.activationKey = activationKey;
    }
    
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "ServerProfile[realm=" ).append( this.realm );
        buf.append( ",              id = ").append( this.id );
        buf.append( ",          factor = *****" );  // do not log this for security reasons
        buf.append( ",          secret = *****" );  // do not log this for security reasons
        buf.append( ",             pin = *****" );  // do not log this for security reasons
        buf.append( ",        password = *****" );  // do not log this for security reasons
        buf.append( ", failuresInEpoch = " ).append( this.failuresInEpoch );
        buf.append( ",            info = " ).append( this.info );
        buf.append( ",           label = " ).append( this.label );
        buf.append( ",    resynchCount = " ).append( this.resynchCount );
        buf.append( "]" );
        return buf.toString();
    }


    public String getNotifyBy()
    {
        return notifyBy;
    }
}

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
 * A HOTP validation server needs more parameters for a Profile which are
 * modelled within this interface which extends the Profile interface.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public interface ServerProfile extends Profile
{
    /**
     * Gets the unique user id associated with this profile.
     *
     * @return the unique user id associated with this profile
     */
    String getUserId();


    /**
     * Gets the authentication realm associated with this Profile.
     *
     * @return the authentication realm associated with this Profile
     */
    String getRealm();


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
    int getResynchCount();

    /**
     * Gets the number of authentication failures within an epoch.  The number
     * of authentication failures are tracked here within time periods
     * determined by the server.  If the user exceeds some threshold the account
     * is automatically locked to prevent brute force attacks.
     *
     * @return the number of authentication failures within an epoch
     */
    int getFailuresInEpoch();
    
    /**
     * Checks to see if this profile is active.
     */
    boolean isActive();
    
    /**
     * Gets the profile's activation key.
     */
    String getActivationKey();
    
    /**
     * Get's the safehausTokenPin value for this account.
     */
    String getTokenPin();
    
    /**
     * Get's the static password for this account.
     */
    byte[] getPassword();


    String getNotifyBy();
}

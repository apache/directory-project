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
 * The interface for a Safehaus account profile.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public interface Profile
{
    /**
     * Check to see if this profile is disabled.
     * 
     * @return true if the profile is disabled, false if it is not
     */
    boolean isDisabled();
    
    /**
     * Gets the label associated with this Profile.
     *
     * @return the label that identifies this Profile
     */
    String getLabel();


    /**
     * Gets the shared secret key used to generate the HOTP value.
     *
     * @return the shared secret key between client and server
     */
    byte[] getSecret();


    /**
     * The moving factor (counter) used to generate an OTP.
     *
     * @return the OTP moving factor (counter)
     */
    long getFactor();


    /**
     * Gets additional account information about this Profile.
     *
     * @return additional account information
     */
    String getInfo();
}

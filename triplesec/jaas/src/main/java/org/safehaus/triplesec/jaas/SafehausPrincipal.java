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
package org.safehaus.triplesec.jaas;

import java.security.Principal;

import org.safehaus.triplesec.guardian.Profile;


/**
 * A security Principal which has a Guardian Authorization Profile associated 
 * with it.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class SafehausPrincipal implements Principal
{
    /** the Guardian authorization profile for this principal */
    private Profile profile;


    SafehausPrincipal( Profile profile )
    {
        this.profile = profile;
    }
    
    
    public String getName()
    {
        return profile.getProfileId();
    }


    /**
     * Gets the Guardian authorization profile for this SafehausPrincipal.
     * 
     * @return the authorization Profile 
     */
    public Profile getAuthorizationProfile()
    {
        return profile;
    }
}

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
package org.safehaus.triplesec.verifier.hotp;


import javax.security.auth.kerberos.KerberosPrincipal;

import org.safehaus.profile.ServerProfile;
import org.safehaus.otp.ResynchParameters;
import org.safehaus.triplesec.verifier.hotp.HotpMonitor;


/**
 * A 'do nothing' adapter for HotpMonitor interface.  At a minimum exceptions
 * are printed to standard err to avoid silencing critical alerts.
 *
 * @version $Rev$
 */
public class HotpMonitorAdapter implements HotpMonitor
{
    public void verificationFailed( ServerProfile p, ResynchParameters params )
    {
    }


    public void initiatingResynch( ServerProfile p, ResynchParameters params )
    {
    }


    public void checkingLookahead( ServerProfile p, ResynchParameters params )
    {
    }


    public void integrityCheckFailed( ServerProfile p )
    {
    }


    public void resynchCompleted( ServerProfile p, ResynchParameters params )
    {
    }


    public void resynchInProgress( ServerProfile p, ResynchParameters params )
    {
    }


    public void integrityCheckPassed( ServerProfile p )
    {
    }


    public void accountLocked( ServerProfile p, ResynchParameters params )
    {
    }


    public void verifying( KerberosPrincipal principal )
    {
    }
}

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
package org.safehaus.triplesec;


import javax.security.auth.kerberos.KerberosPrincipal;

import org.safehaus.triplesec.verifier.hotp.HotpMonitor;
import org.safehaus.profile.ServerProfile;
import org.safehaus.otp.ResynchParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A Log4J based logging HOTP verifier monitor.
 *
 * @author <a href="mailto:directory-dev@incubator.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class LoggingHotpMonitor implements HotpMonitor
{
    /** underlying logger to use for logging events */
    private final Log log;


    public LoggingHotpMonitor( Log log )
    {
        if ( log == null )
        {
            this.log = LogFactory.getLog( "HotpMonitor" );

            return;
        }

        this.log = log;
    }


    public LoggingHotpMonitor()
    {
        this.log = LogFactory.getLog( "HotpMonitor" );
    }


    public void verificationFailed( ServerProfile p, ResynchParameters params )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "verification failed for " + p + " with " + params );
        }
    }


    public void initiatingResynch( ServerProfile p, ResynchParameters params )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "resynch initiated for " + p + " with " + params );
        }
    }


    public void checkingLookahead( ServerProfile p, ResynchParameters params )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "checking lookahead for " + p + " with " + params );
        }
    }


    public void integrityCheckFailed( ServerProfile p )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "integrity check failed for " + p );
        }
    }


    public void resynchCompleted( ServerProfile p, ResynchParameters params )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "resynch completed for " + p + " with " + params );
        }
    }


    public void resynchInProgress( ServerProfile p, ResynchParameters params )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "resynch in progress for " + p + " with " + params );
        }
    }


    public void integrityCheckPassed( ServerProfile p )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "verification passed for " + p );
        }
    }


    public void accountLocked( ServerProfile p, ResynchParameters params )
    {
        log.warn( "account locked for " + p + " with " + params );
    }


    public void verifying( KerberosPrincipal principal )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "attempting verification for " + principal );
        }
    }
}

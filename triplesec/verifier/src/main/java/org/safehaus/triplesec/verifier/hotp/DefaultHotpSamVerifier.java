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


import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.server.kerberos.shared.messages.value.SamType;
import org.apache.directory.server.kerberos.sam.KeyIntegrityChecker;
import org.apache.directory.server.kerberos.sam.SamException;

import org.safehaus.otp.Hotp;
import org.safehaus.otp.ResynchParameters;
import org.safehaus.profile.ServerProfile;
import org.safehaus.profile.BaseServerProfileModifier;
import org.safehaus.triplesec.store.ServerProfileStore;
import org.safehaus.triplesec.store.DefaultServerProfileStore;


/**
 * A HOTP based SAM verifier.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class DefaultHotpSamVerifier implements HotpSamVerifier
{
    /**
     * For now we hardcode the HOTP_SIZE to 6 but we can use the realm info
     * or profile specific info to determine size to use dynamically at runtime.
     */
    public static final int HOTP_SIZE = 6;

    /** Message for failure to access properties */
    public static final String FAILED_PROP_ACCESS_MSG = "Failed to open safehaus store properties";

    /** Message for store initialization failures */
    private static final String FAILED_INITIALIZING_STORE = "Failed initializing store";

    /** temporary property key which controls the use of alternate monitor */
    private static final String MONITOR_PROP = "org.safehaus.verifier.monitor";

    /** the safehaus (hotp) profile store */
    ServerProfileStore store = null;

    /** checks keys based on hotp values for integrity */
    KeyIntegrityChecker keyChecker = null;

    /** the verification process monitor */
    private HotpMonitor monitor = findHotpMonitor();

    /** The context under which users are stored for the realm*/
    private DirContext userContext;


    // -----------------------------------------------------------------------
    // SamVerifier Method Implementations
    // -----------------------------------------------------------------------


    public void setUserContext( DirContext userContext )
    {
        this.userContext = userContext;
    }


    /**
     * Starts up the verifier by initializing the profile store using
     * the provided environment properties.
     *
     * @throws SamException if there are problems initializing the store
     */
    public void startup() throws SamException
    {
        // Check if userContext is set

        if ( this.userContext == null )
        {
            throw new SamException( getSamType(), "User context has not been initialized!" );
        }

        // Initialize the profile store using context

        try
        {
            store = new DefaultServerProfileStore( userContext );
            store.init();
        }
        catch ( NamingException e )
        {
            throw new SamException( getSamType(), FAILED_INITIALIZING_STORE, e );
        }

        // Make sure KeyIntegrityChecker has been set

        if ( keyChecker == null )
        {
            throw new SamException( getSamType(), "KeyIntegrityChecker not set!" );
        }
    }


    public void shutdown()
    {
        this.userContext = null;
        this.store = null;
        this.monitor = null;
        this.keyChecker = null;
    }


    public void setIntegrityChecker( KeyIntegrityChecker keyChecker )
    {
        this.keyChecker = keyChecker;
    }


    public KerberosKey verify( KerberosPrincipal principal, byte[] sad ) throws SamException
    {
        monitor.verifying( principal );

        try
        {
            ServerProfile p = store.getProfile( principal );
            if ( p == null )
            {
                throw new SamException( getSamType(), "Principal " + principal + " not found in store!" );
            }

            ResynchParameters params = getResychParameters( p );

            // ------------------------------------------------------------
            // blow chunks for accounts that are inactive or disabled
            // ------------------------------------------------------------

            if ( ! p.isActive() )
            {
                throw new AccountInactiveException();
            }
            
            if ( p.isDisabled() )
            {
                throw new AccountDisabledException();
            }

            // ------------------------------------------------------------
            // blow chunks for accounts that are locked out
            // ------------------------------------------------------------

            if ( p.getFailuresInEpoch() >= params.getLockoutCount() )
            {
                monitor.accountLocked( p, params );

                throw new AccountLockedOutException();
            }

            // ------------------------------------------------------------
            // let generate and verify if client value is valid
            // ------------------------------------------------------------

            byte[] secret = p.getSecret();
            String serverValue = Hotp.generate( secret, p.getFactor(), HOTP_SIZE );
            KerberosKey serverKey = new KerberosKey( principal, serverValue.toCharArray(), "DES" );

            if ( keyChecker.checkKeyIntegrity( sad, serverKey )  )
            {
                monitor.integrityCheckPassed( p );

                BaseServerProfileModifier modifier = new BaseServerProfileModifier( p );

                modifier.setFactor( p.getFactor() + 1 );

                // ------------------------------------------------------------
                // let's check to see if the client is under resynch process
                // ------------------------------------------------------------

                if ( p.getResynchCount() > 0 )
                {
                    // --------------------------------------------------------
                    // if client must continue process we generate exception
                    // --------------------------------------------------------

                    if ( p.getResynchCount() < params.getNumResyncValidations() - 1 )
                    {
                        modifier.setResynchCount( p.getResynchCount() + 1 );
                        store.update( principal, modifier.getServerProfile() );
                        monitor.resynchInProgress( p, params );
                        throw new ResynchInProgressException();
                    }

                    // --------------------------------------------------------
                    // the client has successfully completed resynch process
                    // --------------------------------------------------------

                    monitor.resynchCompleted( p, params );
                    modifier.setResynchCount( -1 );
                }

                store.update( principal, modifier.getServerProfile() );

                return serverKey;
            }

            monitor.integrityCheckFailed( p );

            // ----------------------------------------------------------------
            // client gave incorrect value so we check if resynch is possible
            // ----------------------------------------------------------------
            // o if we find a match in window,
            //   + advance factor to one past the val that made curr client val
            //   + increment the resynch count so we can terminate resynch proc
            //   + update the profile with new values in store
            //   + blow stack to show we've started resync process
            // ----------------------------------------------------------------

            monitor.checkingLookahead( p, params );
            KerberosKey[] window = getWindow( principal, p, params.getLookaheadSize() );
            BaseServerProfileModifier modifier = new BaseServerProfileModifier( p );

            for ( int ii = 0; ii < window.length; ii++ )
            {
                if ( keyChecker.checkKeyIntegrity( sad, window[ii] ) )
                {
                    modifier.setFactor( p.getFactor() + ii + 2 );
                    modifier.setResynchCount( 1 );
                    store.update( principal, modifier.getServerProfile() );
                    monitor.initiatingResynch( p, params );
                    throw new ResynchStartingException();
                }
            }

            // ----------------------------------------------------------------
            // client gave incorrect value and resynch is not at all possible
            // ----------------------------------------------------------------
            // o moving factor does NOT increment but the failure count does
            // o we update profile and throw an exception to indicate failure
            // ----------------------------------------------------------------

            modifier.setFailuresInEpoch( p.getFailuresInEpoch() + 1 );
            monitor.verificationFailed( p, params );
            store.update( principal, modifier.getServerProfile() );
            throw new PreauthFailedException();
        }
        catch ( NamingException e )
        {
            String msg = "Failed to access profile for " + principal.getName();

            throw new SamException( getSamType(), msg, e );
        }
    }


    public final SamType getSamType()
    {
        return SamType.PA_SAM_TYPE_APACHE;
    }


    public HotpMonitor getHotpMonitor()
    {
        return this.monitor;
    }


    public void setHotpMonitor( HotpMonitor monitor )
    {
        if ( monitor == null )
        {
            this.monitor = findHotpMonitor();
        }
        else
        {
            this.monitor = monitor;
        }
    }


    /**
     * Used by test cases.
     *
     * @return returns the store for testing purposes only
     */
    ServerProfileStore getStore()
    {
        return this.store;
    }


    /**
     * Generates a window of Hotp values as KerberosKeys used to determine if a
     * moving factor resynchronization is required.  The values start with the
     * next factor value in the profile.
     *
     * @param principal the kerberos principal associated with the profile
     * @param p the server side safehaus (hotp) profile
     * @param size the size of the resynchronization window
     * @return the hotp values as KerberosKeys for the window
     */
    private KerberosKey[] getWindow( KerberosPrincipal principal, ServerProfile p, int size )
    {
        KerberosKey[] window = new KerberosKey[size];

        byte[] secret = p.getSecret();

        for ( int ii = 0; ii < size; ii++ )
        {
            String hotp = Hotp.generate( secret, p.getFactor() + ii + 1, HOTP_SIZE );

            window[ii] = new KerberosKey( principal, hotp.toCharArray(), "DES" );
        }

        return window;
    }


    /**
     * Gets an alternative monitor if one exists, otherwise an adapter is used.
     */
    public static HotpMonitor findHotpMonitor()
    {
        String fqcn = null;

        fqcn = System.getProperty( MONITOR_PROP );

        if ( fqcn == null )
        {
            return new HotpMonitorAdapter();
        }

        try
        {
            Class c = Class.forName( fqcn );

            if ( c != null )
            {
                HotpMonitor alt = ( HotpMonitor ) c.newInstance();

                if ( alt != null )
                {
                    return alt;
                }
            }
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
        catch ( InstantiationException e )
        {
            e.printStackTrace();
        }

        return new HotpMonitorAdapter();
    }


    /**
     * Gets the resynch parameters for a specific profile or its domain if
     * available.  If a profile does not contain resynch parameters it's domain
     * will be consulted.  If the domain does not contain the properties then
     * the defaults will be used.
     *
     * @param p the user's server side profile
     * @return the resynch parameters to use
     */
    private ResynchParameters getResychParameters( ServerProfile p )
    {
        ResynchParameters params = null;

        if ( p == null )
        {
            params = ResynchParameters.DEFAULTS;
        }

        // replace code here to search for parameters
        params = ResynchParameters.DEFAULTS;

        return params;
    }
}

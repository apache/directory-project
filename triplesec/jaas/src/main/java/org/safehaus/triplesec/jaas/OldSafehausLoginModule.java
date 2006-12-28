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


import java.util.Map;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.safehaus.otp.HotpErrorConstants;


/**
 * A Safehaus login module which emits hotp exception types.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class OldSafehausLoginModule implements LoginModule
{
    /** the underlying LoginModule is the Krb5LoginModule */
    LoginModule module;


    public OldSafehausLoginModule()
    {
        String javaVendor = System.getProperty( "java.vendor" );
        if ( javaVendor.equalsIgnoreCase( "IBM Corporation" ) )
        {
            /// init IBM's Krb5LoginModule
            try
            {
                module = ( LoginModule ) Class.forName( "com.ibm.security.auth.module.Krb5LoginModule" ).newInstance();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        
        if ( javaVendor.equalsIgnoreCase( "Sun Microsystems Inc." ) )
        {
            /// init SUN's Krb5LoginModule
            try
            {
                module = ( LoginModule ) Class.forName( "com.sun.security.auth.module.Krb5LoginModule" ).newInstance();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    public boolean abort() throws LoginException
    {
        try
        {
            return module.abort();
        }
        catch ( LoginException le )
        {
            // the return shuts the compiler up

            return handle( le );
        }
    }


    public boolean commit() throws LoginException
    {
        try
        {
            return module.commit();
        }
        catch ( LoginException le )
        {
            // the return shuts the compiler up

            return handle( le );
        }
    }


    public boolean login() throws LoginException
    {
        try
        {
            return module.login();
        }
        catch ( LoginException le )
        {
            // the return shuts the compiler up

            return handle( le );
        }
    }


    public boolean logout() throws LoginException
    {
        try
        {
            return module.logout();
        }
        catch ( LoginException le )
        {
            // the return shuts the compiler up

            return handle( le );
        }
    }


    public void initialize( Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options )
    {
        module.initialize( subject, callbackHandler, sharedState, options );
    }


    /**
     * Handles the LoginException by throwing a more specific HOTP exception type if it detects
     * an embedded ordinal value within the exception message, otherwise it rethrows le itself.
     *
     * @param le the initial LoginException thrown
     * @return never returns - exceptions always thrown
     * @throws LoginException always but a more specific on if possible
     */
    public boolean handle( LoginException le ) throws LoginException
    {
        if ( ! HotpErrorConstants.hasEmbeddedOrdinal( le.getMessage() ) )
        {
            throw le;
        }

        int ordinal = HotpErrorConstants.getEmbeddedOrdinal( le.getMessage() );

        switch( ordinal )
        {
            case( HotpErrorConstants.HOTPAUTH_FAILURE_VAL ):

                throw new PreauthFailedException();

            case( HotpErrorConstants.LOCKEDOUT_VAL ):

                throw new AccountLockedOutException();

            case( HotpErrorConstants.RESYNCH_INPROGRESS_VAL ):

                throw new ResynchInProgressException();

            case( HotpErrorConstants.RESYNCH_STARTING_VAL ):

                throw new ResynchStartingException();

            default:
                throw le;
        }
    }
}

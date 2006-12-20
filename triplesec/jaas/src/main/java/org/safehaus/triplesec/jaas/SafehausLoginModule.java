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


import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import org.safehaus.otp.HotpErrorConstants;
import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Safehaus login module which emits hotp exception types.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class SafehausLoginModule implements LoginModule
{
    public static final String ALLOW_ADMIN = SafehausLoginModule.class.getName() + ".allowAdmin";
    private static final Logger log = LoggerFactory.getLogger( SafehausLoginModule.class );
    
    /** the underlying LoginModule is the Krb5LoginModule */
    private NameCallback profileIdCallback;
    private PasswordCallback passwordCallback;
    private RealmCallback realmCallback;
    private PasscodeCallback passcodeCallback;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;
    private PolicyCallback policyCallback;
    private Profile profile;
    LoginModule module;


    public SafehausLoginModule()
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
            if ( module.commit() )
            {
                this.subject.getPrincipals().clear();
                this.subject.getPrincipals().add( new SafehausPrincipal( this.profile ) );
                return true;
            }
            
            return false;
        }
        catch ( LoginException le )
        {
            // the return shuts the compiler up

            return handle( le );
        }
    }


    public boolean login() throws LoginException
    {
        Callback[] callbacks = new Callback[] {
            profileIdCallback, passwordCallback, realmCallback, passcodeCallback, policyCallback
        };
        
        // -------------------------------------------------------------------
        // Invoke the handler populate all the parameters we need
        // -------------------------------------------------------------------

        try
        {
            callbackHandler.handle( callbacks );
        }
        catch ( Exception e )
        {
            log.error( "Callback handler failed.", e );
            LoginException le = new LoginException( "Callback handler failed." );
            le.initCause( e );
            throw le;
        }
        
        // -------------------------------------------------------------------
        // Collect all the parameters we need and determine what kind of auth
        // we're going to have to perform.
        // -------------------------------------------------------------------

        final String profileId = profileIdCallback.getName();
        if ( profileId == null )
        {
            String msg = "Cannot login with null username field.";
            log.error( msg );
            throw new NullPointerException( msg );
        }
        
        final char[] password = passwordCallback.getPassword();
        if ( password == null )
        {
            String msg = "Cannot login with null password.";
            log.error( msg );
            throw new NullPointerException( msg );
        }
        
        final String realm = realmCallback.getRealm();
        if ( realm == null )
        {
            String msg = "Cannot login with null realm.";
            log.error( msg );
            throw new NullPointerException( msg );
        }
        
        final ApplicationPolicy policy = policyCallback.getPolicy();
        if ( policy == null )
        {
            String msg = "Cannot login without a non-null .";
            log.error( msg );
            throw new NullPointerException( msg );
        }

        // -------------------------------------------------------------------
        // Passcode is optional and may be null, check to make sure we 
        // get a valid profile back for the profileId and report findings
        // -------------------------------------------------------------------

        final String passcode = passcodeCallback.getPasscode();
        this.profile = policy.getProfile( profileId );
        if ( this.profile == null )
        {
            log.info( "Profile " + profileId + " not found for user." );
            return false;
        }
        else if ( profileId.equals( "admin" ) )
        {
            if ( ! options.containsKey( ALLOW_ADMIN ) ||
               ( options.containsKey( ALLOW_ADMIN ) && ! ( ( String ) options.get( ALLOW_ADMIN ) ).equals( "true" ) ) )
            {
                throw new LoginException( "Admin authentication has not been enabled." );
            }
            
            // ---------------------------------------------------------------
            // Do just LDAP auth now but with special DN for the admin user
            // ---------------------------------------------------------------
            
            if ( bindAs( "uid=admin,ou=system", "admin" ) )
            {
                this.subject.getPrincipals().add( new SafehausPrincipal( profile ) );
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            log.info( "Profile " + profileId + " found for user " + profile.getUserName() );
        }
        
        // If the profile is disabled then throw and exceptions
        if ( profile.isDisabled() )
        {
            throw new AccountDisabledException( "The profile "  + profile.getProfileId() 
                + " associated with your account for application " 
                + profile.getApplicationName() + " has been disabled." );
        }
        
        // -------------------------------------------------------------------
        // Setup for standard login without a keyfob using kerberos: 1-FACTOR
        // -------------------------------------------------------------------

        CallbackHandler cbHandler;
        final StringBuffer krb5PrincipalName = new StringBuffer();
        krb5PrincipalName.append( profile.getUserName() ).append( "@" ).append( realm.toUpperCase() );
        if ( passcode == null || passcode.length() == 0 )  
        {
            cbHandler = new CallbackHandler() 
            {
                public void handle( Callback[] callbacks )
                {
                    for ( int ii = 0; ii < callbacks.length; ii++ )
                    {
                        if ( callbacks[ii] instanceof NameCallback )
                        {
                            NameCallback ncb = ( NameCallback ) callbacks[ii];
                            ncb.setName( krb5PrincipalName.toString() );
                        }

                        else if ( callbacks[ii] instanceof PasswordCallback )
                        {
                            PasswordCallback pcb = ( PasswordCallback ) callbacks[ii];
                            pcb.setPassword( password );
                        }
                    }
                }
            };
        }
        // -------------------------------------------------------------------
        // Setup to login with keyfob: 2-FACTOR
        // -------------------------------------------------------------------
        else 
        {
            cbHandler = new CallbackHandler() 
            {
                public void handle( Callback[] callbacks )
                {
                    for ( int ii = 0; ii < callbacks.length; ii++ )
                    {
                        if ( callbacks[ii] instanceof NameCallback )
                        {
                            NameCallback ncb = ( NameCallback ) callbacks[ii];
                            ncb.setName( krb5PrincipalName.toString() );
                        }

                        else if ( callbacks[ii] instanceof PasswordCallback )
                        {
                            PasswordCallback pcb = ( PasswordCallback ) callbacks[ii];
                            // Notice we use the passcode instead of the password
                            pcb.setPassword( passcode.toCharArray() );
                        }
                    }
                }
            };
            
            // ---------------------------------------------------------------
            // Now we verify the static password using LDAP
            // ---------------------------------------------------------------

            bindAs( getUserDn( profile.getUserName(), realm ), profile.getUserName() );
        }
        
        try
        {
            module.initialize( subject, cbHandler, sharedState, options );
            return module.login();
        }
        catch ( LoginException le )
        {
            // the return shuts the compiler up
            return handle( le );
        }
    }

    
    public boolean bindAs( String principalDn, String userName )
    {
        Hashtable env = new Hashtable();
        for ( Iterator ii = options.keySet().iterator(); ii.hasNext(); /**/ ) 
        {
            Object key = ii.next();
            env.put( key, options.get( key ) );
        }
        env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        
        InitialDirContext ctx = null;
        try
        {
            ctx = new InitialDirContext( env );
            ctx.close();
            return true;
        }
        catch ( NamingException e )
        {
            log.error( "Failed to bind to directory as user " + userName, e );
            return false;
        }
        finally
        {
            if ( ctx == null )
            {
                try
                {
                    ctx.close();
                }
                catch ( NamingException e )
                {
                    log.error( "can't close ldap context", e );
                }
            }
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
        // What is the username to the application is really the profileId to us
        profileIdCallback = new NameCallback( "Username: " ); 
        passwordCallback = new PasswordCallback( "Password: ", false );
        realmCallback = new RealmCallback();
        passcodeCallback = new PasscodeCallback();
        policyCallback = new PolicyCallback();
        
        // Save these values for delayed initialization of the Krb5LoginModule
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
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
            case( HotpErrorConstants.DISABLED_VAL ):
                throw new AccountDisabledException();
            case( HotpErrorConstants.RESYNCH_INPROGRESS_VAL ):
                throw new ResynchInProgressException();
            case( HotpErrorConstants.RESYNCH_STARTING_VAL ):
                throw new ResynchStartingException();
            default:
                throw le;
        }
    }
    
    
    public static String getUserDn( String username, String realm )
    {
        StringBuffer buf = new StringBuffer( realm.length() + username.length() + 5 );
        buf.append( "uid=" ).append( username ).append( ",ou=users," );
        if ( realm == null || realm.length() == 0  )
        {
            return buf.toString();
        }

        buf.append( "dc=" );
        int start = 0, end = 0;
        // Replace all the '.' by ",dc=". The comma is added because
        // the string is not supposed to start with a dot, so another
        // dc=XXXX already exists in any cases.
        // The realm is also not supposed to finish with a '.'
        while ( ( end = realm.indexOf( '.', start ) ) != -1 )
        {
            buf.append( realm.substring( start, end ) ).append( ",dc=" );
            start = end + 1;
        }

        buf.append( realm.substring( start ) );
        return buf.toString();
    }
}

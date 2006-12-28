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
package org.safehaus.triplesec.admin.swing;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

import javax.naming.Context;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.safehaus.triplesec.admin.TriplesecAdmin;
import org.safehaus.triplesec.admin.dao.DaoFactory;
import org.safehaus.triplesec.admin.dao.ldap.LdapDaoFactory;
import org.safehaus.triplesec.configuration.TriplesecStartupConfiguration;
import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.jaas.PasscodeCallback;
import org.safehaus.triplesec.jaas.PolicyCallback;
import org.safehaus.triplesec.jaas.RealmCallback;
import org.safehaus.triplesec.jaas.SafehausLoginModule;
import org.safehaus.triplesec.jaas.SafehausPrincipal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionManager extends Observable
{
    private static final Logger log = LoggerFactory.getLogger( ConnectionManager.class );
    private Throwable lastFailure;
    private TriplesecAdmin admin;
    private String realm;
    
    
    public boolean connect( final ConnectionInfo connectionInfo, final ApplicationPolicy policy ) throws LoginException
    {
        CallbackHandler handler = new CallbackHandler()
        {
            public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException
            {
                for ( int ii = 0; ii < callbacks.length; ii++ )
                {
                    if ( callbacks[ii] instanceof NameCallback )
                    {
                        NameCallback ncb = ( NameCallback ) callbacks[ii];
                        ncb.setName( connectionInfo.getPrincipal() );
                    }

                    else if ( callbacks[ii] instanceof PasswordCallback )
                    {
                        PasswordCallback pcb = ( PasswordCallback ) callbacks[ii];
                        pcb.setPassword( connectionInfo.getCredentials().toCharArray() );
                    }
                    
                    else if ( callbacks[ii] instanceof RealmCallback )
                    {
                        RealmCallback rcb = ( RealmCallback ) callbacks[ii];
                        rcb.setRealm( connectionInfo.getRealm() );
                    }
                    
                    else if ( callbacks[ii] instanceof PolicyCallback )
                    {
                        PolicyCallback pcb = ( PolicyCallback ) callbacks[ii];
                        pcb.setPolicy( policy );
                    }
                    
                    else if ( callbacks[ii] instanceof PasscodeCallback )
                    {
                        PasscodeCallback pcb = ( PasscodeCallback ) callbacks[ii];
                        pcb.setPasscode( connectionInfo.getPasscode() );
                    }
                }
            }
        };
        Subject subject = new Subject();
        SafehausLoginModule module = new SafehausLoginModule();
        Map options = new HashMap();
        options.put( SafehausLoginModule.ALLOW_ADMIN, "true" );
        module.initialize( subject, handler, new HashMap(), options );
        if ( ! module.login() )
        {
            throw new LoginException ( "Authentication failed!" );
        }
        module.commit();
        
        // -------------------------------------------------------------------
        // Need to connect to the server as the user to initialize DAO factories
        // -------------------------------------------------------------------

        Properties env = new Properties();
        env.put( DaoFactory.IMPLEMENTATION_CLASS, LdapDaoFactory.class.getName() );
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        StringBuffer buf = new StringBuffer();
        
        buf.append( "ldap" );
        if ( connectionInfo.isUseLdaps() )
        {
            buf.append( "s" );
        }
        buf.append( "://" ).append( connectionInfo.getHost() ).append( ":" ).append( connectionInfo.getLdapPort() );
        buf.append( "/" ).append( connectionInfo.getLdapRealmBase() );
        env.put( Context.PROVIDER_URL, buf.toString() );
        
        if ( connectionInfo.getPrincipal().equals( "admin" ) )
        {
            env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        }
        else
        {
            SafehausPrincipal principal = ( SafehausPrincipal ) subject.getPrincipals().toArray()[0];
            Profile profile = principal.getAuthorizationProfile();
            StringBuffer principalDn = new StringBuffer();
            principalDn.append( "uid=" ).append( profile.getUserName() ).append( ",ou=Users," )
                .append( connectionInfo.getLdapRealmBase() );
            env.put ( Context.SECURITY_PRINCIPAL, principalDn.toString() );
        }
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, connectionInfo.getCredentials() );
        realm = connectionInfo.getRealm();
        try
        {
            admin = new TriplesecAdmin( env );
        }
        catch ( Throwable t ) // @todo make sure we catch all sorts of subclasses and handle differently
        {
            log.error( "Failed to connect to triplesec server: " + connectionInfo, t );
            lastFailure = t;
            admin = null;
            return false;
        }
        super.setChanged();
        super.notifyObservers();
        return true;
    }

    
    public boolean disconnect()
    {
        if ( admin == null )
        {
            return true;
        }
        admin.close();
        admin = null;
        realm = null;
        super.setChanged();
        super.notifyObservers();
        return true;
    }

    
    public boolean connect( TriplesecStartupConfiguration config, Properties env )
    {
        Properties props = new Properties();
        props.putAll( env );
        props.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        props.put( DaoFactory.IMPLEMENTATION_CLASS, LdapDaoFactory.class.getName() );
        StringBuffer buf = new StringBuffer();
        buf.append( "ldap://localhost:" ).append( config.getLdapPort() );
        buf.append( "/" ).append( props.getProperty( Context.PROVIDER_URL ) );
        props.put( Context.PROVIDER_URL, buf.toString() );
        props.remove( "java.naming.factory.object" );
        props.remove( "java.naming.factory.state" );
        
        realm = props.getProperty( "kdc.primary.realm" );
        try
        {
            admin = new TriplesecAdmin( props );
        }
        catch ( Throwable t )
        {
            log.error( "Failed to connect to triplesec server: " + env, t );
            lastFailure = t;
            admin = null;
            realm = null;
            return false;
        }
        
        super.setChanged();
        super.notifyObservers();
        return true;
    }

    
    public boolean isConnected()
    {
        return admin != null;
    }
    
    
    public TriplesecAdmin getTriplesecAdmin()
    {
        return admin;
    }
    
    
    public Throwable getLastFailure()
    {
        return lastFailure;
    }


    public String getRealm()
    {
        return realm;
    }
}

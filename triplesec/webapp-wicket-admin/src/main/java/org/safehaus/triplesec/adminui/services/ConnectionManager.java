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
package org.safehaus.triplesec.adminui.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.safehaus.triplesec.admin.TriplesecAdmin;
import org.safehaus.triplesec.admin.dao.DaoFactory;
import org.safehaus.triplesec.admin.dao.ldap.LdapDaoFactory;
import org.safehaus.triplesec.adminui.models.ConnectionInfo;
import org.safehaus.triplesec.configuration.TriplesecStartupConfiguration;

import javax.naming.Context;
import java.util.Observable;
import java.util.Properties;

/**
 * Manages a connection to a Triplesec server.
 */
public class ConnectionManager extends Observable
{
    private static final Logger log = LoggerFactory.getLogger( ConnectionManager.class );
    private Throwable lastFailure;
    private TriplesecAdmin admin;
    private String realm;

    /**
     * Connects to the TripleSec server as identified by the <code>ConnectionInfo</code> object.
     */
    public boolean connect( ConnectionInfo connectionInfo )
    {
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
            // TODO
            // use profile information to determine the actual user
            // we need to login first using an ApplicationPolicy
            throw new RuntimeException( "not yet implemented..." );
        }
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, connectionInfo.getCredentials() );
        realm = connectionInfo.getRealm();
        try
        {
            admin = new TriplesecAdmin( env );
        }
        // TODO make sure we catch all kinds of subclasses and handle differently...
        catch ( Throwable t )
        {
            log.error( "Failed to connect to Triplesec server: " + connectionInfo, t );
            lastFailure = t;
            admin = null;
            return false;
        }
        super.setChanged();
        super.notifyObservers();
        return true;
    }


    /**
     * Disconnects from the Triplesec server.
     */
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

    /**
     * Connect to the specified Triplesec server.
     */
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
            log.error( "Failed to connect to Triplesec server: " + env, t );
            lastFailure = t;
            admin = null;
            realm = null;
            return false;
        }

        super.setChanged();
        super.notifyObservers();
        return true;
    }


    /**
     * Returns <code>true</code> if the connection manager is connected to a Triplesec server instance; otherwise
     * returns <code>false</code>.
     */
    public boolean isConnected()
    {
        return admin != null;
    }


    /**
     * Returns instance of last exception thrown by the connection manager.
     */
    public Throwable getLastFailure()
    {
        return lastFailure;
    }


    /**
     * Returns a handle to the Triplesec server admin API.
     */
    public TriplesecAdmin getAdmin()
    {
        return admin;
    }


    /**
     * Returns the kerberos security realm.
     */
    public String getRealm()
    {
        return realm;
    }
}

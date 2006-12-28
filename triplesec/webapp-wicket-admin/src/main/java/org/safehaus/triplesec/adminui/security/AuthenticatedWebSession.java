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
package org.safehaus.triplesec.adminui.security;

import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebSession;
import org.safehaus.triplesec.adminui.models.ConnectionInfo;
import org.safehaus.triplesec.adminui.services.ConnectionManager;

/**
 * Basic authenticated web session.  Subclasses must provide an implementation
 * of the <code>authenticate</code> method based on connection and credential
 * information contained within an instance of the
 * {@link org.safehaus.triplesec.adminui.models.ConnectionInfo} bean.
 */
public class AuthenticatedWebSession extends WebSession
{
    private static final long serialVersionUID = 1L;
    /**
     * Manages an authenticated connection to the Triplesec server.
     */
    private ConnectionManager connection = null;


    /**
     * Construct the authenticated web session.
     *
     * @param application the web application
     */
    public AuthenticatedWebSession( final WebApplication application ) {
        super( application );
    }


    /**
     * Authenticates this session to the Triplesec server using the given
     * {@link org.safehaus.triplesec.adminui.models.ConnectionInfo} properties.
     *
     * @param info the <code>ConnectionInfo</code> properties
     * @return <code>true</code> if the user was authenticated successfully
     */
    public boolean authenticate( ConnectionInfo info ) {

        // if the user is (somehow) already signed in, then wave 'em on by...
        if ( isAuthenticated() )
        {
            return true;
        }

        // create a session-level instance of the Triplesec server connection manager
        connection = new ConnectionManager();

        // TODO -- delete this once the login dialogs are completed...
        if ( info == null ) {
            info = new ConnectionInfo();
            info.setCredentials( "secret" );
            info.setHost( "localhost" );
            info.setKrb5Port( 88 );
            info.setLdapPort( 10389 );
            info.setPrincipal( "admin" );
            info.setRealm( "EXAMPLE.COM" );
            info.setUseLdaps( false );
        }

        // return the results of our attempt to connect to the Triplesec server
        return connection.connect( info );
    }


    /**
     * @return <code>true</code> if the user is signed in to this session
     */
    public boolean isAuthenticated()
    {
        return connection != null;
    }


    public Throwable getAuthenticationError()
    {
        if ( connection == null )
        {
            throw new IllegalStateException(
                    "Instance of " + ConnectionManager.class.getName() + " not yet instantiated"
            );
        }
        return connection.getLastFailure();
    }

    /**
     * Invalidate the session by disconnecting from the Triplesec server.
     */
    public void invalidate() {
        if ( connection != null )
        {
            connection.disconnect();
            connection = null;
        }
        super.invalidate();
    }


    /**
     * Returns the Triplesec server admin connection manager.
     */
    public ConnectionManager getConnection()
    {
        if ( connection == null )
        {
            throw new IllegalStateException(
                    "Instance of " + ConnectionManager.class.getName() + " not yet instantiated"
            );
        }
        return connection;
    }
}

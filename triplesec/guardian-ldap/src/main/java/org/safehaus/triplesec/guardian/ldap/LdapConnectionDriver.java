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
package org.safehaus.triplesec.guardian.ldap;


import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.ConnectionDriver;
import org.safehaus.triplesec.guardian.GuardianException;
import org.safehaus.triplesec.guardian.StoreConnectionException;


/**
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 62 $
 */
public class LdapConnectionDriver implements ConnectionDriver
{
    static
    {
        ApplicationPolicyFactory.registerDriver( new LdapConnectionDriver() );
    }

    public LdapConnectionDriver()
    {
    }

    public boolean accept( String url )
    {
        return url.startsWith("ldap://");

    }

    public ApplicationPolicy newStore( String url, Properties info ) throws GuardianException
    {
        if ( info == null )
        {
            info = new Properties();
        }

        if ( url == null )
        {
            throw new IllegalArgumentException( "A non-null url must be provided." );
        }

        String application = info.getProperty( "applicationPrincipalDN" );
        if ( application == null )
        {
            throw new IllegalArgumentException( "An applicationPrincipalDN property value must be provided." );
        }

        String password = info.getProperty( "applicationCredentials" );
        if ( password == null )
        {
            throw new IllegalArgumentException( "The applicationCredentials property must be provided" );
        }

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( Context.PROVIDER_URL, url );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_PRINCIPAL, info.get( "applicationPrincipalDN" ) );
        env.put( Context.SECURITY_CREDENTIALS, info.get( "applicationCredentials" ) );

        InitialDirContext ictx;
        try
        {
            ictx = new InitialDirContext( env );
        }
        catch ( NamingException e )
        {
            env.remove( Context.SECURITY_CREDENTIALS ); // remove credentials before printing to log
            throw new StoreConnectionException( "Failed to obtain initial context for " + env, e );
        }

        return new LdapApplicationPolicy( ictx, info );
    }
}

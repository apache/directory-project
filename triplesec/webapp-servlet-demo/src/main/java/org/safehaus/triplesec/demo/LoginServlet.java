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
package org.safehaus.triplesec.demo;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.Permissions;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.Roles;
import org.safehaus.triplesec.jaas.SafehausPrincipal;


/**
 * Demo login servlet.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = -8057005697331693436L;

    // Guardian connection init parameters
    private static final String APPLICATION_CREDENTIALS_PARAM = "applicationCredentials";
    private static final String APPLICATION_PRINCIPAL_DN_PARAM = "applicationPrincipalDn";
    private static final String CONNECTION_URL_PARAM = "connectionUrl";
    private static final String REALM_PARAM = "realm";

    private String realm;
    private ApplicationPolicy policy;
    
    
    public void init( ServletConfig config )
    {
        // -------------------------------------------------------------------
        // get the realm the guardian connection URL and the application DN 
        // -------------------------------------------------------------------
    
        realm = config.getInitParameter( REALM_PARAM );
        String connectionUrl = config.getInitParameter( CONNECTION_URL_PARAM );
        String applicationPrincipalDn = config.getInitParameter( APPLICATION_PRINCIPAL_DN_PARAM );
        String applicationCredentials = config.getInitParameter( APPLICATION_CREDENTIALS_PARAM );
        
        // -------------------------------------------------------------------
        // setup connection parameters and initialize the application policy 
        // -------------------------------------------------------------------
    
        Properties props = new Properties();
        props.setProperty( "applicationPrincipalDN", applicationPrincipalDn );
        props.setProperty( "applicationCredentials", applicationCredentials );
        try
        {
            Class.forName( "org.safehaus.triplesec.guardian.ldap.LdapConnectionDriver" );
            policy = ApplicationPolicyFactory.newInstance( connectionUrl, props );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
        IOException
    {
        doAll( request, response );
    }
    
    
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
        IOException
    {
        doAll( request, response );
    }
    
    
    protected void doAll( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
        IOException
    {
        // get the required parameters for authentication 
        String username = ( String ) request.getParameter( "username" );
        String password = ( String ) request.getParameter( "password" );
        String passcode = ( String ) request.getParameter( "passcode" );
        
        // prepare and execute the login command that wraps the login module
        LoginCommand command = new LoginCommand( username, password, realm, passcode, policy );
        boolean result = false;
        try
        {
            result = command.execute();
        }
        catch ( LoginException e )
        {
            doErrorMessage( request, response, e.getMessage() );
            return;
        }
        
        if ( result == false )
        {
            doErrorMessage( request, response, "Failed authentication!" );
            return;
        }
        
        // get the authorization profile of the authenticated user and print it out
        SafehausPrincipal principal = command.getSafehausPrincipal();
        Profile profile = principal.getAuthorizationProfile();
        PrintWriter out = response.getWriter();
        out.println( "<html><body><p><font color=\"green\">Authentication Succeeded</font></p><br/><br/>" );
        out.println( "<h2>Authorization Profile " + profile.getProfileId() 
            + " for User " + profile.getUserName() + "</h2>" );
        
        // print out the grants in the profile
        out.println( "<p>Profile Grants:</p><ul>" );
        Permissions grants = profile.getGrants();
        for ( Iterator ii = grants.iterator(); ii.hasNext(); /**/ )
        {
            out.println( "<li>" + ii.next() + "</li>" );
        }
        out.println( "</ul>" );
            
        // print out the denials in the profile
        out.println( "<p>Profile Denials:</p><ul>" );
        Permissions denials = profile.getDenials();
        for ( Iterator ii = denials.iterator(); ii.hasNext(); /**/ )
        {
            out.println( "<li>" + ii.next() + "</li>" );
        }
        out.println( "</ul>" );
            
        // print out the roles the profile puts the user in
        out.println( "<p>Profile Roles:</p><ul>" );
        Roles roles = profile.getRoles();
        for ( Iterator ii = roles.iterator(); ii.hasNext(); /**/ )
        {
            out.println( "<li>" + ii.next() + "</li>" );
        }
        out.println( "</ul>" );
        out.println( "</body></html>" );
    }
    
    
    private void doErrorMessage( HttpServletRequest request, HttpServletResponse response, String message ) throws IOException
    {
        PrintWriter out = response.getWriter();
        out.println( "<html><body><p><font color=\"red\">" + message + "</font></p></body></html>" );
        out.flush();
        return;
    }
}

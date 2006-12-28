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


import javax.security.auth.spi.LoginModule;
import javax.security.auth.callback.*;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.jaas.PasscodeCallback;
import org.safehaus.triplesec.jaas.PolicyCallback;
import org.safehaus.triplesec.jaas.RealmCallback;
import org.safehaus.triplesec.jaas.SafehausLoginModule;
import org.safehaus.triplesec.jaas.SafehausPrincipal;


/**
 * Simple login command used by the demo application.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LoginCommand
{
    /** the user id of the principal minus realm info */
    private final String userId;
    /** the realm the user is authenticating into */
    private final String realm;
    /** the value of the hotp */
    private final String passcode;
    /** the static password for the user */
    private final String password;
    /** the triplesec guardian policy for this application */
    private final ApplicationPolicy policy;

    /** the safehaus principal resulting from authentication */
    private SafehausPrincipal principal;

    
    /**
     * Creates a single use login command that can later be executed.
     *
     * @param userId the user id of the principal minus realm info
     * @param realm the realm the user is authenticating into
     * @param passcode the value of the hotp
     */
    public LoginCommand( String userId, String password, String realm, String passcode, ApplicationPolicy policy )
    {
        this.userId = userId;
        this.realm = realm;
        this.passcode = passcode;
        this.password = password;
        this.policy = policy;
    }


    /**
     * Logs the user into the system.  Exceptions will contain optional information used to determine
     * if a resync is in effect or if the account is locked out.
     *
     * @return true if we can authenticate the user, false otherwise
     */
    public boolean execute() throws LoginException
    {
        LoginModule module = new SafehausLoginModule();
        Subject subject = new Subject();
        Map options = new HashMap();
        options.put( SafehausLoginModule.ALLOW_ADMIN, "true" );
        module.initialize( subject, new LoginHandler(), new HashMap(), options );
        boolean result = module.login();
        result &= module.commit();
        Object[] principals = subject.getPrincipals().toArray();
        if ( principals.length > 0 )
        {
            principal = ( SafehausPrincipal ) principals[0];
        }
        return result;
    }

    
    public SafehausPrincipal getSafehausPrincipal()
    {
        return principal;
    }
    

    /**
     * Simple handler implementation for this Demo.
     */
    class LoginHandler implements CallbackHandler
    {
        public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException
        {
            for ( int ii = 0; ii < callbacks.length; ii++ )
            {
                if ( callbacks[ii] instanceof NameCallback )
                {
                    NameCallback ncb = ( NameCallback ) callbacks[ii];
                    ncb.setName( userId );
                }
                else if ( callbacks[ii] instanceof PasswordCallback )
                {
                    PasswordCallback pcb = ( PasswordCallback ) callbacks[ii];
                    pcb.setPassword( password.toCharArray() );
                }
                else if ( callbacks[ii] instanceof RealmCallback )
                {
                    RealmCallback rcb = ( RealmCallback ) callbacks[ii];
                    rcb.setRealm( realm );
                }
                else if ( callbacks[ii] instanceof PolicyCallback )
                {
                    PolicyCallback pcb = ( PolicyCallback ) callbacks[ii];
                    pcb.setPolicy( policy );
                }
                else if ( callbacks[ii] instanceof PasscodeCallback )
                {
                    PasscodeCallback pcb = ( PasscodeCallback ) callbacks[ii];
                    pcb.setPasscode( passcode );
                }
            }
        }
    }
}

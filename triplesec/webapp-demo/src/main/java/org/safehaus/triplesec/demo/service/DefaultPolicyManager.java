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
package org.safehaus.triplesec.demo.service;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.jaas.SafehausPrincipal;
import org.safehaus.triplesec.jaas.RealmCallback;
import org.safehaus.triplesec.jaas.PolicyCallback;
import org.safehaus.triplesec.jaas.PasscodeCallback;
import org.safehaus.triplesec.jaas.SafehausLoginModule;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class DefaultPolicyManager implements PolicyManager
{
    private String realm;
    private String ldapDriver;
    private String url;
    private Properties ldapProperties;

    public ApplicationPolicy getAppPolicy()
    {
        try
        {
            Class.forName( ldapDriver );
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        return ApplicationPolicyFactory.newInstance( url, ldapProperties );
    }

    public void setLdapDriver( String ldapDriver )
    {
        this.ldapDriver = ldapDriver;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public void setLdapProperties( Properties ldapProperties )
    {
        this.ldapProperties = ldapProperties;
    }

    public String getRealm()
    {
        return realm;
    }

    public SafehausPrincipal getPrincipal( String userid,
                                           String password,
                                           String passcode ) throws LoginException
    {
        SafehausPrincipal principal = null;
        LoginModule module = new SafehausLoginModule();
        Subject subject = new Subject();
        Map options = new HashMap();
        options.put( SafehausLoginModule.ALLOW_ADMIN, "true" );
        module.initialize( subject,
                new DemoHandler( userid, password, passcode ),
                new HashMap(), options );
        boolean result = module.login();
        result &= module.commit();
        if ( result )
        {
            Object[] principals = subject.getPrincipals().toArray();
            if ( principals.length > 0 )
            {
                principal = (SafehausPrincipal) principals[0];
            }
        }
        return principal;
    }

    public void setRealm( String realm )
    {
        this.realm = realm;
    }

    /**
     * Simple handler implementation for this Demo.
     */
    private class DemoHandler implements CallbackHandler
    {
        private String userId;
        private String password;
        private String passcode;

        public DemoHandler( String userId, String password, String passcode )
        {
            this.userId = userId;
            this.password = password;
            this.passcode = passcode;
        }

        public void handle(Callback[] callbacks)
                throws IOException, UnsupportedCallbackException
        {
            for ( int i = 0; i < callbacks.length; i++ )
            {
                if ( callbacks[i] instanceof NameCallback )
                {
                    NameCallback ncb = ( NameCallback ) callbacks[i];
                    ncb.setName( userId );
                }
                else if ( callbacks[i] instanceof PasswordCallback )
                {
                    PasswordCallback pcb = ( PasswordCallback ) callbacks[i];
                    pcb.setPassword( password.toCharArray() );
                }
                else if ( callbacks[i] instanceof RealmCallback )
                {
                    RealmCallback rcb = ( RealmCallback ) callbacks[i];
                    rcb.setRealm( realm.toUpperCase() );
                }
                else if ( callbacks[i] instanceof PolicyCallback )
                {
                    PolicyCallback pcb = ( PolicyCallback ) callbacks[i];
                    pcb.setPolicy( getAppPolicy() );
                }
                else if ( callbacks[i] instanceof PasscodeCallback )
                {
                    PasscodeCallback pcb = ( PasscodeCallback ) callbacks[i];
                    pcb.setPasscode( passcode );
                }
            }
        }
    }

}

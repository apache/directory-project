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


import org.apache.directory.shared.ldap.util.NamespaceTools;


public class ConnectionInfo
{
    private final String host;
    private final int ldapPort;
    private final int krb5Port;
    private final boolean useLdaps;
    private final String realm;
    private final String principal;
    private final String credentials;
    private final String passcode;

    
    public ConnectionInfo ( String host, int ldapPort, int krb5Port, boolean useLdaps, 
        String realm, String princial, String credentials, String passcode )
    {
        this.host = host;
        this.ldapPort = ldapPort;
        this.krb5Port = krb5Port;
        this.useLdaps = useLdaps;
        this.realm = realm;
        this.principal = princial;
        this.credentials = credentials;
        this.passcode = passcode;
    }

    
    public String getHost()
    {
        return host;
    }


    public int getLdapPort()
    {
        return ldapPort;
    }


    public int getKrb5Port()
    {
        return krb5Port;
    }


    public boolean isUseLdaps()
    {
        return useLdaps;
    }


    public String getRealm()
    {
        return realm;
    }

    
    public String getLdapRealmBase()
    {
        return NamespaceTools.inferLdapName( realm );
    }
    

    public String getPrincipal()
    {
        return principal;
    }


    public String getCredentials()
    {
        return credentials;
    }


    public String getPasscode()
    {
        return passcode;
    }
}

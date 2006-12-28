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
package org.safehaus.triplesec.adminui.models;

import org.apache.directory.shared.ldap.util.NamespaceTools;

/**
 * Model bean for storing and passing Triplesec connection metadata.
 */
public class ConnectionInfo
{
    private String host;
    private int ldapPort;
    private int krb5Port;
    private boolean useLdaps;
    private String realm;
    private String principal;
    private String credentials;
    private String passcode;

    public ConnectionInfo()
    {
    }


    public ConnectionInfo( String host, int ldapPort, int krb5Port, boolean useLdaps,
                           String realm, String principal, String credentials, String passcode )
    {
        this.host = host;
        this.ldapPort = ldapPort;
        this.krb5Port = krb5Port;
        this.useLdaps = useLdaps;
        this.realm = realm;
        this.principal = principal;
        this.credentials = credentials;
        this.passcode = passcode;
    }


    public String getHost()
    {
        return host;
    }


    public void setHost(String aHost)
    {
        host = aHost;
    }


    public int getLdapPort()
    {
        return ldapPort;
    }


    public void setLdapPort(int aLdapPort)
    {
        ldapPort = aLdapPort;
    }


    public int getKrb5Port()
    {
        return krb5Port;
    }


    public void setKrb5Port(int aKrb5Port)
    {
        krb5Port = aKrb5Port;
    }


    public boolean isUseLdaps()
    {
        return useLdaps;
    }


    public void setUseLdaps(boolean aUseLdaps)
    {
        useLdaps = aUseLdaps;
    }


    public String getRealm()
    {
        return realm;
    }


    public void setRealm(String aRealm)
    {
        realm = aRealm;
    }


    public String getLdapRealmBase()
    {
        return NamespaceTools.inferLdapName( realm );
    }


    public String getPrincipal()
    {
        return principal;
    }


    public void setPrincipal(String aPrincipal)
    {
        principal = aPrincipal;
    }


    public String getCredentials()
    {
        return credentials;
    }


    public void setCredentials(String aCredentials)
    {
        credentials = aCredentials;
    }


    public String getPasscode()
    {
        return passcode;
    }


    public void setPasscode(String aPasscode)
    {
        passcode = aPasscode;
    }
}

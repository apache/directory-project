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



public class ConnectionInfoModifier
{
    private String host;
    private int ldapPort;
    private int krb5Port;
    private boolean useLdaps;
    private String realm;
    private String principal;
    private String credentials;
    private String passcode;
    
    
    public ConnectionInfoModifier()
    {
    }

    
    public ConnectionInfoModifier( ConnectionInfo copy )
    {
        this.host = copy.getHost();
        this.credentials = copy.getCredentials();
        this.krb5Port = copy.getKrb5Port();
        this.ldapPort = copy.getLdapPort();
        this.useLdaps = copy.isUseLdaps();
        this.realm = copy.getRealm();
        this.principal = copy.getPrincipal();
        this.passcode = copy.getPasscode();
    }
    
    
    public void setHost( String host )
    {
        this.host = host;
    }


    public void setLdapPort( int ldapPort )
    {
        this.ldapPort = ldapPort;
    }


    public void setKrb5Port( int krb5Port )
    {
        this.krb5Port = krb5Port;
    }


    public void setUseLdaps( boolean useLdaps )
    {
        this.useLdaps = useLdaps;
    }


    public void setRealm( String realm )
    {
        this.realm = realm;
    }


    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }


    public void setCredentials( String credentials )
    {
        this.credentials = credentials;
    }


    public void setPasscode( String passcode )
    {
        this.passcode = passcode;
    }
    
    
    public ConnectionInfo getConnectionInfo()
    {
        return new ConnectionInfo ( host, ldapPort, krb5Port, useLdaps, 
            realm, principal, credentials, passcode );
    }
}

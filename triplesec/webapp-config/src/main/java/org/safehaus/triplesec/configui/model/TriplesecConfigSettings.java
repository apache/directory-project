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
package org.safehaus.triplesec.configui.model;


import java.io.Serializable;

/**
 * Model object for capturing the Triplesec configuration settings.
 */
public class TriplesecConfigSettings implements Serializable
{
    private static final long serialVersionUID = -2672319798571167870L;

    private String adminPassword;
    private String adminPassword2;
    private String primaryRealmName = "safehaus.org";
    private String smsProvider = "NMSI HTTP";
    private String smsUsername;
    private String smsPassword;
    private String smsAccountName = "trial";
    private String smsTransportUrl = "http://demo.safehaus.org/smstrial/smspush";
    private String smtpUsername;
    private String smtpPassword;
    private String smtpHost = "localhost";
    private String smtpSubject = "Triplesec Event";
    private String smtpFrom = "dev@safehaus.org";
    private String ldapCertFilePath;
    private String ldapCertPassword;
    private String presentationBaseUrl = "http://demo.safehaus.org";
    private String regRedirectUrl = "http://demo.safehaus.org/demo";
    private boolean enableDemo = true;
    private boolean enableLdap = true;
    private boolean enableHttp = true;
    private boolean allowAnonymousAccess = false;
    private boolean enableLdaps;
    private boolean smtpAuthenticate = false;
    private int httpPort = 8383;
    private int ldapPort = 10389;
    private int ldapsPort = 10636;
    private long clockSkew = 5;
    private long ticketLifetime = 1440;
    private long renewableLifetime = 10080;


    public int getHttpPort()
    {
        return httpPort;
    }


    public void setHttpPort( int httpPort )
    {
        this.httpPort = httpPort;
    }


    public String getAdminPassword()
    {
        return adminPassword;
    }


    public void setAdminPassword( String adminPassword )
    {
        this.adminPassword = adminPassword;
    }


    public String getAdminPassword2()
    {
        return adminPassword2;
    }


    public void setAdminPassword2( String adminPassword2 )
    {
        this.adminPassword2 = adminPassword2;
    }


    public boolean doPasswordsMatch()
    {
        return adminPassword.equals( adminPassword2 );
    }


    public boolean isEnableDemo()
    {
        return enableDemo;
    }


    public void setEnableDemo( boolean enableDemo )
    {
        this.enableDemo = enableDemo;
    }


    public boolean isEnableLdap()
    {
        return enableLdap;
    }


    public void setEnableLdap( boolean enableLdap )
    {
        this.enableLdap = enableLdap;
    }


    public boolean isAllowAnonymousAccess()
    {
        return allowAnonymousAccess;
    }


    public void setAllowAnonymousAccess( boolean allowAnonymousAccess )
    {
        this.allowAnonymousAccess = allowAnonymousAccess;
    }


    public int getLdapPort()
    {
        return ldapPort;
    }


    public void setLdapPort( int ldapPort )
    {
        this.ldapPort = ldapPort;
    }


    public boolean isEnableLdaps()
    {
        return enableLdaps;
    }


    public void setEnableLdaps(boolean enableLdaps)
    {
        this.enableLdaps = enableLdaps;
    }


    public String getLdapCertFilePath()
    {
        return ldapCertFilePath;
    }


    public void setLdapCertFilePath(String ldapCertFilePath)
    {
        this.ldapCertFilePath = ldapCertFilePath;
    }


    public String getLdapCertPassword()
    {
        return ldapCertPassword;
    }


    public void setLdapCertPassword(String ldapCertPassword)
    {
        this.ldapCertPassword = ldapCertPassword;
    }


    public int getLdapsPort()
    {
        return ldapsPort;
    }


    public void setLdapsPort( int ldapsPort )
    {
        this.ldapsPort = ldapsPort;
    }


    public String getPrimaryRealmName()
    {
        return primaryRealmName;
    }


    public void setPrimaryRealmName( String primaryRealmName )
    {
        this.primaryRealmName = primaryRealmName;
    }


    public long getClockSkew()
    {
        return clockSkew;
    }


    public void setClockSkew( long clockSkew )
    {
        this.clockSkew = clockSkew;
    }


    public long getTicketLifetime()
    {
        return ticketLifetime;
    }


    public void setTicketLifetime( long ticketLifetime )
    {
        this.ticketLifetime = ticketLifetime;
    }


    public long getRenewableLifetime()
    {
        return renewableLifetime;
    }


    public void setRenewableLifetime( long renewableLifetime )
    {
        this.renewableLifetime = renewableLifetime;
    }


    public String getSmsAccountName()
    {
        return smsAccountName;
    }


    public void setSmsAccountName(String smsAccountName)
    {
        this.smsAccountName = smsAccountName;
    }


    public String getSmsPassword()
    {
        return smsPassword;
    }


    public void setSmsPassword(String smsPassword)
    {
        this.smsPassword = smsPassword;
    }


    public String getSmsProvider()
    {
        return smsProvider;
    }


    public void setSmsProvider(String smsProvider)
    {
        this.smsProvider = smsProvider;
    }


    public String getSmsTransportUrl()
    {
        return smsTransportUrl;
    }


    public void setSmsTransportUrl(String smsTransportUrl)
    {
        this.smsTransportUrl = smsTransportUrl;
    }


    public String getSmsUsername()
    {
        return smsUsername;
    }


    public void setSmsUsername(String smsUsername)
    {
        this.smsUsername = smsUsername;
    }


    public boolean isSmtpAuthenticate()
    {
        return smtpAuthenticate;
    }


    public void setSmtpAuthenticate(boolean smtpAuthenticate)
    {
        this.smtpAuthenticate = smtpAuthenticate;
    }


    public String getSmtpFrom()
    {
        return smtpFrom;
    }


    public void setSmtpFrom(String smtpFrom)
    {
        this.smtpFrom = smtpFrom;
    }


    public String getSmtpHost()
    {
        return smtpHost;
    }


    public void setSmtpHost(String smtpHost)
    {
        this.smtpHost = smtpHost;
    }


    public String getSmtpPassword()
    {
        return smtpPassword;
    }


    public void setSmtpPassword(String smtpPassword)
    {
        this.smtpPassword = smtpPassword;
    }


    public String getSmtpSubject()
    {
        return smtpSubject;
    }


    public void setSmtpSubject(String smtpSubject)
    {
        this.smtpSubject = smtpSubject;
    }


    public String getSmtpUsername()
    {
        return smtpUsername;
    }


    public void setSmtpUsername(String smtpUsername)
    {
        this.smtpUsername = smtpUsername;
    }


    public void setEnableHttp( boolean enableHttp )
    {
        this.enableHttp = enableHttp;
    }


    public boolean isEnableHttp()
    {
        return enableHttp;
    }


    public String getPresentationBaseUrl()
    {
        return presentationBaseUrl;
    }


    public void setPresentationBaseUrl( String presentationBaseUrl )
    {
        this.presentationBaseUrl = presentationBaseUrl;
    }


    public String getRegRedirectUrl()
    {
        return regRedirectUrl;
    }


    public void setRegRedirectUrl(String regRedirectUrl)
    {
        this.regRedirectUrl = regRedirectUrl;
    }


    public String toString() {
        return "TriplesecConfigSettings{" +
                "adminPassword2='" + adminPassword2 + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                ", primaryRealmName='" + primaryRealmName + '\'' +
                ", smsProvider='" + smsProvider + '\'' +
                ", smsUsername='" + smsUsername + '\'' +
                ", smsPassword='" + smsPassword + '\'' +
                ", smsAccountName='" + smsAccountName + '\'' +
                ", smsTransportUrl='" + smsTransportUrl + '\'' +
                ", smtpUsername='" + smtpUsername + '\'' +
                ", smtpPassword='" + smtpPassword + '\'' +
                ", smtpHost='" + smtpHost + '\'' +
                ", smtpSubject='" + smtpSubject + '\'' +
                ", smtpFrom='" + smtpFrom + '\'' +
                ", ldapCertFilePath='" + ldapCertFilePath + '\'' +
                ", ldapCertPassword='" + ldapCertPassword + '\'' +
                ", enableDemo=" + enableDemo +
                ", enableLdap=" + enableLdap +
                ", allowAnonymousAccess=" + allowAnonymousAccess +
                ", enableLdaps=" + enableLdaps +
                ", smtpAuthenticate=" + smtpAuthenticate +
                ", adminPort=" + httpPort +
                ", ldapPort=" + ldapPort +
                ", ldapsPort=" + ldapsPort +
                ", clockSkew=" + clockSkew +
                ", ticketLifetime=" + ticketLifetime +
                ", renewableLifetime=" + renewableLifetime +
                '}';
    }
}

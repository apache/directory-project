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


import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;


public class AdminToolSettings
{
    private SmsConfiguration defaultSmsConfig;
    private SmtpConfiguration defaultSmtpConfig;
    private String adminToolPassword;
    private ConnectionInfo defaultConnectionInfo;
    private String settingsPassphrase;
    private boolean passcodePromptEnabled = true;
    private String presentationBaseUrl;
    
    
    public void setDefaultSmsConfig( SmsConfiguration defaultSmsConfig )
    {
        this.defaultSmsConfig = defaultSmsConfig;
    }
    
    
    public SmsConfiguration getDefaultSmsConfig()
    {
        return defaultSmsConfig;
    }


    public void setDefaultSmtpConfig( SmtpConfiguration defaultSmtpConfig )
    {
        this.defaultSmtpConfig = defaultSmtpConfig;
    }


    public SmtpConfiguration getDefaultSmtpConfig()
    {
        return defaultSmtpConfig;
    }


    public void setAdminToolPassword( String adminToolPassword )
    {
        this.adminToolPassword = adminToolPassword;
    }


    public String getAdminToolPassword()
    {
        return adminToolPassword;
    }


    public void setDefaultConnectionInfo( ConnectionInfo defaultConnectionInfo )
    {
        this.defaultConnectionInfo = defaultConnectionInfo;
    }


    public ConnectionInfo getDefaultConnectionInfo()
    {
        return defaultConnectionInfo;
    }


    public void setSettingsPassphrase( String settingsPassphrase )
    {
        this.settingsPassphrase = settingsPassphrase;
    }


    public String getSettingsPassphrase()
    {
        return settingsPassphrase;
    }


    public boolean isPasscodePromptEnabled()
    {
        return passcodePromptEnabled;
    }
    
    
    public void setPasscodePromptEnabled( boolean passcodePromptEnabled )
    {
        this.passcodePromptEnabled = passcodePromptEnabled;
    }


    public void setPresentationBaseUrl( String presentationBaseUrl )
    {
        this.presentationBaseUrl = presentationBaseUrl;
    }


    public String getPresentationBaseUrl()
    {
        return presentationBaseUrl;
    }
}

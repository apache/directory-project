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
package org.safehaus.triplesec.configuration;


/**
 * The SMTP server configuration.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class SmtpConfiguration
{
    private boolean authenticate = false;
    private String username;
    private String password;
    private String host = "localhost";
    private String subject = "Triplesec account activated";
    private String from = "dev@safehaus.org";
    
    
    public void setSmtpAuthenticate( boolean authenticate )
    {
        this.authenticate = authenticate;
    }
    
    
    public boolean isSmtpAuthenticate()
    {
        return authenticate;
    }


    public void setSmtpUsername( String username )
    {
        this.username = username;
    }


    public String getSmtpUsername()
    {
        return username;
    }


    public void setSmtpPassword( String password )
    {
        this.password = password;
    }


    public String getSmtpPassword()
    {
        return password;
    }


    public void setSmtpHost( String host )
    {
        this.host = host;
    }


    public String getSmtpHost()
    {
        return host;
    }


    public void setSmtpSubject( String subject )
    {
        this.subject = subject;
    }


    public String getSmtpSubject()
    {
        return subject;
    }


    public void setSmtpFrom( String from )
    {
        this.from = from;
    }


    public String getSmtpFrom()
    {
        return from;
    }
}

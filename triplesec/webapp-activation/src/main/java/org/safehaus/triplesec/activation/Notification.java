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
package org.safehaus.triplesec.activation;


import java.util.Date;


/**
 * A message to be delivered to the account owner requesting the activation of
 * their account.  ActivationMessages are delivered to the owner after some 
 * delay, hence the need to encapsulate the message and enqueue it for future
 * delivery.  An ActivationMessage generally contains the URL used to activate 
 * the account.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public abstract class Notification
{
    private final long timestamp;
    private final String message;
    private final String activationKey;
    
    
    protected Notification( String activationKey, String message )
    {
        this.timestamp = System.currentTimeMillis();
        this.message = message;
        this.activationKey = activationKey;
    }
    
    
    public long getTimestamp()
    {
        return timestamp;
    }
    
    
    public String getMessage()
    {
        return message;
    }
    
    
    public String getActivationKey()
    {
        return activationKey;
    }
    
    
    public int hashCode()
    {
        return activationKey.hashCode();
    }
    
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "\t    timestamp = " ).append( new Date( timestamp ) ).append( "\n" );
        buf.append( "\t      message = " ).append( message ).append( "\n" );
        buf.append( "\tactivationKey = " ).append( activationKey ).append( "\n" );
        return buf.toString();
    }
}

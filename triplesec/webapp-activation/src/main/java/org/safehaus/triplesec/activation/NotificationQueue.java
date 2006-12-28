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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * An activation message queue which allows the dequeue of messages based on age
 * or based on an activationKey.  Not synchronized.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class NotificationQueue
{
    LinkedList msgList = new LinkedList();
    Map msgMap = new HashMap();
    
    
    public void enqueue( Notification message )
    {
        msgList.addFirst( message );
        msgMap.put( message.getActivationKey(), message );
    }
    
    
    /**
     * Dequeues an array of ActivationMessages from the message queue based on age.
     * 
     * @param ageMillis remove all messages that have been in the queue for this 
     * time or more in milliseconds
     * @return an array of messages of this age with the oldest first
     */
    public Notification[] dequeue( long ageMillis )
    {
        List ready = null;
        
        if ( msgList.size() == 0 )
        {
            return new Notification[0];
        }
        
        ready = new ArrayList();
        while( msgList.size() > 0 )
        {
            Notification msg = ( Notification ) msgList.getLast();
            if ( System.currentTimeMillis() - msg.getTimestamp() >= ageMillis )
            {
                msgList.removeLast();
                msgMap.remove( msg.getActivationKey() );
                ready.add( msg );
            }
        }

        Notification[] array = new Notification[ready.size()];
        return ( Notification[] ) ready.toArray( array );
    }
    
    
    /**
     * Dequeues a specific message regardless of it's age from this message queue.
     * 
     * @param activationKey the activation key of the message to remove
     * @return the activation message with the key or null if none exists 
     */
    public Notification dequeue( String activationKey )
    {
        Notification msg = ( Notification ) msgMap.remove( activationKey );
        if ( msg == null )
        {
            return null;
        }
        msgList.remove( msg );
        return msg;
    }
    
    
    public boolean isEmpty()
    {
        return msgList.isEmpty();
    }
    
    
    public boolean available( long ageMillis )
    {
        if ( msgList.isEmpty() )
        {
            return false;
        }
        
        Notification msg = ( Notification ) msgList.getLast();
        if ( msg == null )
        {
            return false;
        }
        return System.currentTimeMillis() - msg.getTimestamp() >= ageMillis;
    }


    public long getWaitMillis()
    {
        if ( msgList.isEmpty() )
        {
            return Long.MAX_VALUE;
        }
        
        Notification msg = ( Notification ) msgList.getLast();
        if ( msg == null )
        {
            return Long.MAX_VALUE;
        }
        
        long waitMillis = 60000 - ( System.currentTimeMillis() - msg.getTimestamp() );
        if ( waitMillis < 0 )
        {
            return 0;
        }
        return waitMillis;
    }
}

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
package org.safehaus.triplesec.changelog.beta.model;

import java.util.Date;

/**
 * Represents a change-type LDAP operation with invocation time and
 * principle.
 * 
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public interface ChangeEvent
{
    /**
     * Returns the unique id of the change event.
     * 
     * @return The unique id of the change event
     */
    public int getEventId();
    
    
    /**
     * Returns the type of the change event as an integer.
     * 
     * @return The type of the change event as an integer
     */
    public int getEventType();
    
    /**
     * Returns the type of the change event as a string.
     * 
     * @return The type of the change event as a string
     */
    public String getEventTypeName();
    
    /**
     * Returns the distinguished name of the entry being affected.
     * 
     * @return The distinguished name of the entry being affected
     */
    public String getAffectedEntryName();
    
    /**
     * Returns a short form of the distinguished name of the entry being affected.
     * 
     * @return A short for of the distinguished name of the entry being affected
     */
    public String getAffectedEntryShortName();
    
    /**
     * Returns a detailed message about the change event.
     * 
     * @return Detailed message about the change event
     */
    public String getEventMessage();
    
    /**
     * Returns the time when the change event generated.
     * 
     * @return The time when the change event generated
     */
    public Date getEventTime();
    
    /**
     * Returns the name of the principle caused the change event
     * to be generated because of operation it invoked.
     * 
     * @return The name of the principle in effect
     */
    public String getPrincipalName();
}

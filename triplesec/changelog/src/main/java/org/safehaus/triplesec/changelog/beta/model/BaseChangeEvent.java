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

import org.safehaus.triplesec.changelog.beta.support.ChangeEventType;

/**
 * A base implementation for {@link org.safehaus.triplesec.changelog.beta.model.ChangeEvent}
 * 
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public class BaseChangeEvent implements ChangeEvent
{
    private int id;
    private int changeEventType;
    private String affectedEntryName;
    private String changeEventPrincipal;
    private Date changeEventTime;


    public BaseChangeEvent( int id, int changeEventType, String affectedEntryName, String changeEventPrincipal, Date changeEventTime)
    {
        this.id = id;
        this.changeEventType = changeEventType;
        this.affectedEntryName = affectedEntryName;
        this.changeEventPrincipal = changeEventPrincipal;
        this.changeEventTime = changeEventTime;
    }


    /* (non-Javadoc)
     * @see org.safehaus.triplesec.changelog.model.ChangeEvent#getEventMessage()
     */
    public String getEventMessage()
    {
        return "";
    }


    /* (non-Javadoc)
     * @see org.safehaus.triplesec.changelog.model.ChangeEvent#getEventTime()
     */
    public Date getEventTime()
    {
        return changeEventTime;
    }


    /* (non-Javadoc)
     * @see org.safehaus.triplesec.changelog.model.ChangeEvent#getEventType()
     */
    public int getEventType()
    {
        return changeEventType;
    }
    
    
    /* (non-Javadoc)
     * @see org.safehaus.triplesec.changelog.model.ChangeEvent#getNameOfAffectedEntry()
     */
    public String getAffectedEntryName()
    {
        return affectedEntryName;
    }


    /* (non-Javadoc)
     * @see org.safehaus.triplesec.changelog.model.ChangeEvent#getPrincipleName()
     */
    public String getPrincipalName()
    {
        return changeEventPrincipal;
    }


    public String getEventTypeName()
    {
        return ChangeEventType.getChangeEventTypeNameByIntEnum( getEventType() );
    }


    public int getEventId()
    {
        return id;
    }


    public String getAffectedEntryShortName()
    {
        if ( affectedEntryName.length() > 40  )
        {
            return affectedEntryName.subSequence(0, 35).toString() + "...";    
        }
        else
        {
            return affectedEntryName;
        }
    }

}

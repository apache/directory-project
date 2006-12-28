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
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public class DeleteChangeEvent extends BaseChangeEvent
{

    public DeleteChangeEvent( int id, String affectedEntryName, String changeEventPrincipal, Date changeEventTime )
    {
        super( id, ChangeEventType.DELETE_CHANGE_EVENT, affectedEntryName, changeEventPrincipal, changeEventTime );
    }
        

    public String getEventMessage()
    {
        return getAffectedEntryName();
    }

}

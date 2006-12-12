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
 * @author ersin
 *
 */
public class ModifyDnChangeEvent extends BaseChangeEvent
{
    private String newDn;
    private boolean deleteOldRdn;
    
    
    public ModifyDnChangeEvent( int id, String affectedEntryName, String changeEventPrincipal, Date changeEventTime, String newDn, boolean deleteOldRdn )
    {
        super( id, ChangeEventType.MODDN_CHANGE_EVENT, affectedEntryName, changeEventPrincipal, changeEventTime );
        this.newDn = newDn;
        this.deleteOldRdn = deleteOldRdn;
    }
    

    /**
     * @return Returns the deleteOldRdn.
     */
    public boolean isDeleteOldRdn()
    {
        return deleteOldRdn;
    }


    /**
     * @return Returns the newDn.
     */
    public String getNewDn()
    {
        return newDn;
    }


    /* (non-Javadoc)
     * @see org.safehaus.triplesec.changelog.model.BaseChangeEvent#getEventMessage()
     */
    public String getEventMessage()
    {
        return "dn moved to " + newDn;
    }

}

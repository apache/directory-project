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
package org.safehaus.triplesec.admin;


import java.util.Set;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.dao.GroupDao;


public class GroupModifier implements Constants
{
    private final GroupDao dao;
    private final String name;
    private final Group archetype;
    private final MultiValuedField members;
    private boolean persisted = false;


    public GroupModifier( GroupDao dao, Group archetype )
    {
        this.dao = dao;
        this.archetype = archetype;
        this.name = archetype.getName();
        this.members = new MultiValuedField( UNIQUE_MEMBER_ID, archetype.getMembers() );
    }
    
    
    public GroupModifier( GroupDao dao, String name, Set members )
    {
        this.dao = dao;
        this.name = name;
        this.members = new MultiValuedField( UNIQUE_MEMBER_ID, members );
        this.archetype = null;
    }


    private ModificationItem[] getModificationItems()
    {
        // new entries do not generate modification items
        if ( isNewEntry() )
        {
            return EMPTY_MODS;
        }
        
        if ( members.isUpdateNeeded() )
        {
            return new ModificationItem[] { members.getModificationItem() };
        }
        
        return EMPTY_MODS;
    }


    public boolean isNewEntry()
    {
        return archetype == null;
    }


    public boolean isUpdatableEntry()
    {
        return archetype != null;
    }
    
    
    public boolean isUpdateNeeded()
    {
        return members.isUpdateNeeded();
    }
    
    
    public boolean isValid()
    {
        return ! persisted;
    }
    
    
    public GroupModifier addMember( String member )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        
        members.addValue( member );
        return this;
    }
    
    
    public GroupModifier removeMember( String member )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        members.removeValue( member );
        return this;
    }
    
    
    public Group add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Group group = dao.add( name, members.getCurrentValues() );
        persisted = true;
        return group;
    }
    
    
    public Group rename( String newName ) throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        if ( isUpdateNeeded() )
        {
            throw new ModificationLossException( name + " has been modified. " +
                    "A rename operation will result in the loss of these modifications." );
        }
        
        Group group = dao.rename( newName, archetype );
        persisted = true;
        return group;
    }
    
    
    public Group modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Group group = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), name, 
            members.getCurrentValues(), getModificationItems() );
        persisted = true;
        return group;
    }
    
    
    public void delete() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        dao.delete( name );
        persisted = true;
    }
}

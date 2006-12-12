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


import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.dao.PermissionDao;



public class PermissionModifier implements Constants
{
    private final PermissionDao dao;
    private final Permission archetype;
    private final String applicationName;
    private final String name;
    private final SingleValuedField description;
    private boolean persisted = false;
    
    
    public PermissionModifier( PermissionDao dao, String applicationName, String name )
    {
        this.dao = dao;
        description = new SingleValuedField( DESCRIPTION_ID, null );
        archetype = null;
        this.name = name;
        this.applicationName = applicationName;
    }
    
    
    public PermissionModifier( PermissionDao dao, Permission archetype )
    {
        this.dao = dao;
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
        this.archetype = archetype;
        name = archetype.getName();
        applicationName = archetype.getApplicationName();
    }
    
    
    public PermissionModifier setDescription( String description )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        this.description.setValue( description );
        return this;
    }

    
    private ModificationItem[] getModificationItems()
    {
        // new entries do not generate modification items
        if ( isNewEntry() )
        {
            return EMPTY_MODS;
        }
        
        if ( description.isUpdateNeeded() )
        {
            return new ModificationItem[] { description.getModificationItem() };
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
        return description.isUpdateNeeded();
    }
    
    
    public boolean isValid()
    {
        return !persisted;
    }
    
    
    public Permission add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Permission perm = dao.add( applicationName, name, description.getCurrentValue() );
        persisted = true;
        return perm;
    }
    
    
    public Permission rename( String newPermName ) throws DataAccessException
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
        
        Permission perm = dao.rename( newPermName, archetype );
        persisted = true;
        return perm;
    }
    
    
    public Permission modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Permission perm = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), applicationName, 
            name, description.getCurrentValue(), getModificationItems() );
        persisted = true;
        return perm;
    }
    
    
    public void delete() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        dao.delete( applicationName, name );
        persisted = true;
    }
}

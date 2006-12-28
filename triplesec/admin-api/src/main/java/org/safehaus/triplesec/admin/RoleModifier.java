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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.dao.RoleDao;



public class RoleModifier implements Constants
{
    private final Role archetype;
    private final RoleDao dao;
    private final String name;
    private final String applicationName;
    private SingleValuedField description;
    private MultiValuedField<PermissionClass> permissionClasses;
    private boolean persisted = false;
    
    
    public RoleModifier( RoleDao dao, String applicationName, String name )
    {
        archetype = null;
        this.dao = dao;
        this.applicationName = applicationName;
        this.name = name;
        this.description = new SingleValuedField( DESCRIPTION_ID, null );
        this.permissionClasses = new MultiValuedField<PermissionClass>( PERM_CLASS_NAME_ID, Collections.EMPTY_SET );
    }
    
    
    public RoleModifier( RoleDao dao, Role archetype )
    {
        this.dao = dao;
        this.archetype = archetype;
        this.applicationName = archetype.getApplicationName();
        this.name = archetype.getName();
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
        this.permissionClasses = new MultiValuedField<PermissionClass>( PERM_CLASS_NAME_ID, archetype.getPermissionClasses() );
    }
    
    
    public RoleModifier setDescription( String description )
    {
        this.description.setValue( description );
        return this;
    }

    
    public RoleModifier addPermissionClass( PermissionClass permissionClass )
    {
        if ( permissionClass == null )
        {
            return this;
        }

        permissionClasses.addValue( permissionClass );
        return this;
    }
    
    
    public RoleModifier removePermissionClass( PermissionClass permissionClass )
    {
        if ( permissionClass == null )
        {
            return this;
        }

        permissionClasses.removeValue( permissionClass );
        return this;
    }
    
    
    public Role getArchetype()
    {
        return archetype;
    }
    
    
    private ModificationItem[] getModificationItems()
    {
        if ( ! isUpdateNeeded() )
        {
            return EMPTY_MODS;
        }
        
        List mods = new ArrayList();
        if ( permissionClasses.isUpdateNeeded() )
        {
            mods.add( permissionClasses.getModificationItem() );
        }
        if ( description.isUpdateNeeded() )
        {
            mods.add( description.getModificationItem() );
        }
        
        return ( ModificationItem[] ) mods.toArray( EMPTY_MODS );
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
        return permissionClasses.isUpdateNeeded() || description.isUpdateNeeded();
    }
    
    
    public boolean isValid()
    {
        return !persisted;
    }
    
    
    public Role add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Role role = dao.add( applicationName, name, description.getCurrentValue(), permissionClasses.getCurrentValues() );
        persisted = true;
        return role;
    }

    
    public Role rename( String newName ) throws DataAccessException
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
        
        Role role = dao.rename( newName, archetype );
        persisted = true;
        return role;
    }
    
    
    public Role modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Role role = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), applicationName, 
            name, description.getCurrentValue(), permissionClasses.getCurrentValues(), getModificationItems() );
        persisted = true;
        return role;
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

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

import org.safehaus.triplesec.admin.dao.ProfileDao;



public class ProfileModifier implements Constants
{
    private final ProfileDao dao;
    private final Profile archetype;
    private final String id;
    private final String applicationName;
    private final SingleValuedField description;
    private final SingleValuedField user;
    private final SingleValuedField disabled;
    private final MultiValuedField<PermissionClass> permissionClasses;
    private final MultiValuedField<String> roles;
    
    private boolean persisted = false;
    
    
    public ProfileModifier( ProfileDao dao, String applicationName, String id, String user )
    {
        archetype = null;
        this.dao = dao;
        this.applicationName = applicationName;
        this.id = id;
        this.description = new SingleValuedField( DESCRIPTION_ID, null );
        this.user = new SingleValuedField( USER_ID, user );
        this.permissionClasses = new MultiValuedField<PermissionClass>( PERM_CLASS_NAME_ID, Collections.EMPTY_SET );
        this.roles = new MultiValuedField<String>( ROLES_ID, Collections.EMPTY_SET );
        this.disabled = new SingleValuedField( SAFEHAUS_DISABLED_ID, "FALSE" );
    }
    
    
    public ProfileModifier( ProfileDao dao, Profile archetype )
    {
        this.dao = dao;
        this.archetype = archetype;
        this.applicationName = archetype.getApplicationName();
        this.id = archetype.getId();
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
        this.disabled = new SingleValuedField( SAFEHAUS_DISABLED_ID, String.valueOf( archetype.isDisabled() ) );
        this.user = new SingleValuedField( USER_ID, archetype.getUser() );
        this.permissionClasses = new MultiValuedField<PermissionClass>( PERM_CLASS_NAME_ID, archetype.getPermissionClasses() );
        this.roles = new MultiValuedField<String>( ROLES_ID, archetype.getRoles() );
    }
    
    
    public ProfileModifier setDisable( boolean disabled )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.disabled.setValue( String.valueOf( disabled ).toUpperCase() );
        return this;
    }
    
    
    public ProfileModifier setDescription( String description )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.description.setValue( description );
        return this;
    }

    
    public ProfileModifier setUser( String user )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.user.setValue( user );
        return this;
    }

    
    public ProfileModifier addPermissionClass( PermissionClass permissionClass )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        if ( permissionClass == null )
        {
            return this;
        }

        permissionClasses.addValue( permissionClass );
        return this;
    }
    
    
    public ProfileModifier removePermissionClass( PermissionClass permissionClass )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        if ( permissionClass == null )
        {
            return this;
        }

        permissionClasses.removeValue( permissionClass );
        return this;
    }
    
    
    public ProfileModifier addRole( String role )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        if ( role == null )
        {
            return this;
        }

        roles.addValue( role );
        return this;
    }
    
    
    public ProfileModifier removeRole( String role )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }

        if ( role == null )
        {
            return this;
        }

        roles.removeValue( role );
        return this;
    }
    
    //TODO changes to permissionClasses not tracked here!
    private ModificationItem[] getModificationItems()
    {
        if ( ! isUpdateNeeded() )
        {
            return EMPTY_MODS;
        }
        
        List mods = new ArrayList();
        if ( roles.isUpdateNeeded() )
        {
            mods.add( roles.getModificationItem() );
        }
        if ( description.isUpdateNeeded() )
        {
            mods.add( description.getModificationItem() );
        }
        if ( disabled.isUpdateNeeded() )
        {
            mods.add( disabled.getModificationItem() );
        }
        if ( user.isUpdateNeeded() )
        {
            mods.add( user.getModificationItem() );
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
        return disabled.isUpdateNeeded() || permissionClasses.isUpdateNeeded() ||
            roles.isUpdateNeeded() || description.isUpdateNeeded() || user.isUpdateNeeded();
    }


    public boolean isValid()
    {
        return !persisted;
    }
    
    
    public Profile add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Profile profile = dao.add( applicationName, id, user.getCurrentValue(), description.getCurrentValue(), 
            permissionClasses.getCurrentValues(), roles.getCurrentValues() );
        persisted = true;
        return profile;
    }

    
    public Profile rename( String newProfileId ) throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        
        if ( isUpdateNeeded() )
        {
            throw new ModificationLossException( id + " has been modified. " +
                    "A rename operation will result in the loss of these modifications." );
        }
        
        Profile profile = dao.rename( newProfileId, archetype );
        persisted = true;
        return profile;
    }
    
    
    public Profile modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        Profile profile = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), 
            applicationName, id, user.getCurrentValue(), description.getCurrentValue(), 
            permissionClasses.getCurrentValues(), roles.getCurrentValues(),
            parseBoolean( disabled.getCurrentValue().toLowerCase() ), getModificationItems() );
        persisted = true;
        return profile;
    }
    
    
    public void delete() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        dao.delete( applicationName, id );
        persisted = true;
    }

    
    private static boolean parseBoolean( String bool )
    {
        if ( bool.equals( "true" ) )
        {
            return true;
        }
        
        return false;
    }
}

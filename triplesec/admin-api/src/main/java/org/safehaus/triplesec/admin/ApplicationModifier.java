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
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.dao.ApplicationDao;
import org.safehaus.triplesec.admin.dao.ProfileDao;
import org.safehaus.triplesec.admin.dao.RoleDao;


public class ApplicationModifier implements Constants
{
    private final String name;
    private final SingleValuedField description;
    private final SingleValuedField password;
    private final RoleDao roleDao;
    private final ProfileDao profileDao;
    private final ApplicationDao dao;
    private final Application archetype;
    
    private boolean persisted;
    
    
    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------
    
    
    ApplicationModifier(ApplicationDao dao, String name,
            RoleDao roleDao, ProfileDao profileDao)
    {
        this.name = name;
        this.dao = dao;
        this.archetype = null;
        this.roleDao = roleDao;
        this.profileDao = profileDao;
        this.password = new SingleValuedField( PASSWORD_ID, null );
        this.description = new SingleValuedField( DESCRIPTION_ID, null );
    }
    
    
    ApplicationModifier( ApplicationDao dao, Application archetype )
    {
        this.name = archetype.getName();
        this.dao = dao;
        this.archetype = archetype;
        this.roleDao = archetype.getRoleDao();
        this.profileDao = archetype.getProfileDao();
        this.password = new SingleValuedField( PASSWORD_ID, archetype.getPassword() );
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
    }
    
    
    // -----------------------------------------------------------------------
    // Property mutators
    // -----------------------------------------------------------------------
    
    
    public ApplicationModifier setDescription( String description )
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        this.description.setValue( description );
        return this;
    }

    
    public ApplicationModifier setPassword( String userPassword )
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        this.password.setValue( userPassword );
        return this;
    }
    
    public RoleModifier newRole( String roleName )
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        return new RoleModifier( roleDao, name, roleName );
    }
    
    
    public ProfileModifier newProfile( String profileId, String user ) 
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        return new ProfileModifier( profileDao, name, profileId, user );
    }
    
    
    // -----------------------------------------------------------------------
    // Mutable Iterator access methods
    // -----------------------------------------------------------------------
    
    
    public Iterator roleIterator() throws DataAccessException
    {
        return roleDao.roleIterator( name );
    }
    
    
    public Iterator profileIterator() throws DataAccessException
    {
        return profileDao.profileIterator( name );
    }
    
    
    public Application getArchetype()
    {
        return archetype;
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
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        return description.isUpdateNeeded() || password.isUpdateNeeded();
    }
    
    
    public boolean isValid()
    {
        return !persisted;
    }
    
    
    private ModificationItem[] getModificationItems()
    {
        if ( ! isUpdateNeeded() )
        {
            return EMPTY_MODS;
        }
        
        List mods = new ArrayList();
        if ( password.isUpdateNeeded() )
        {
            mods.add( password.getModificationItem() );
        }
        if ( description.isUpdateNeeded() )
        {
            mods.add( description.getModificationItem() );
        }
        
        return ( ModificationItem[] ) mods.toArray( EMPTY_MODS );
    }


    public Application modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        
        if ( isNewEntry() )
        {
            throw new IllegalStateException( "This modifier cannot be used to modify an Application" );
        }

        Application app = dao.modify( archetype.getName(), getModificationItems() );
        persisted = true;
        return app;
    }   


    public void delete() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        
        if ( isNewEntry() )
        {
            throw new IllegalStateException( "This modifier cannot be used to delete an Application" );
        }

        dao.delete( archetype );
        persisted = true;
    }   


    public Application add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        
        if ( isUpdatableEntry() )
        {
            throw new IllegalStateException( "This modifier cannot create/add a new Application" );
        }

        Application app = dao.add( name, description.getCurrentValue(), password.getCurrentValue() );
        persisted = true;
        return app;
    }
    
    
    public Application rename( String newName ) throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( "This modifier has persisted changes and is no longer valid." );
        }
        
        if ( isNewEntry() )
        {
            throw new IllegalStateException( "This modifier cannot be used to rename " +
                    "a new Application before an add operation" );
        }

        if ( isUpdateNeeded() )
        {
            throw new ModificationLossException( name + " has been modified. " +
                    "A rename operation will result in the loss of these modifications." );
        }
        
        Application app = dao.rename( archetype, newName );
        persisted = true;
        return app;
    }
}

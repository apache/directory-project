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
import java.util.List;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.dao.ExternalUserDao;


public class ExternalUserModifier implements Constants
{
    private SingleValuedField referral;
    private final SingleValuedField disabled;
    
    private final ExternalUserDao dao;
    private final String id;
    private final ExternalUser archetype;
    private final SingleValuedField description;
    private boolean persisted = false;
    
    
    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    
    public ExternalUserModifier( ExternalUserDao dao, ExternalUser archetype )
    {
        this.id = archetype.getId();
        this.dao = dao;
        this.archetype = archetype;
        this.referral = new SingleValuedField( REF_ID, archetype.getReferral() );
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
        this.disabled = new SingleValuedField( KRB5_DISABLED_ID, String.valueOf( archetype.isDisabled() ).toUpperCase() );
    }


    public ExternalUserModifier( ExternalUserDao dao, String id, String referral )
    {
        this.id = id;
        this.dao = dao;
        this.archetype = null;
        this.referral = new SingleValuedField( REF_ID, referral );
        this.description = new SingleValuedField( DESCRIPTION_ID, null );
        this.disabled = new SingleValuedField( KRB5_DISABLED_ID, null );
    }
    
    
    // -----------------------------------------------------------------------
    // Mutators
    // -----------------------------------------------------------------------

    
    public ExternalUserModifier setDisabled( boolean disabled )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.disabled.setValue( String.valueOf( disabled ).toUpperCase() );
        return this;
    }
    
    
    public ExternalUserModifier setReferral( String referral )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.referral.setValue( referral );
        return this;
    }
    
   
    public ExternalUserModifier setDescription( String description )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.description.setValue( description );
        return this;
    }


    // -----------------------------------------------------------------------
    // Modifier Check Methods
    // -----------------------------------------------------------------------

    
    public boolean isUpdateNeeded()
    {
        return description.isUpdateNeeded() || referral.isUpdateNeeded() || disabled.isUpdateNeeded();
    }


    public boolean isNewEntry()
    {
        return archetype == null;
    }


    public boolean isUpdatableEntry()
    {
        return archetype != null;
    }
    
    
    public boolean isValid()
    {
        return ! persisted;
    }
    
    
    // -----------------------------------------------------------------------
    // Modifier Methods
    // -----------------------------------------------------------------------

    
    public ExternalUser add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        ExternalUser user = dao.add( id, description.getCurrentValue(), referral.getCurrentValue() );
        persisted = true;
        return user;
    }
    
    
    public ExternalUser rename( String newName ) throws DataAccessException
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
        
        ExternalUser user = dao.rename( newName, archetype );
        persisted = true;
        return user;
    }
    
    
    public ExternalUser modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        ExternalUser user = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), id, 
            description.getCurrentValue(), referral.getCurrentValue(), 
            parseBoolean( disabled.getCurrentValue().toLowerCase() ), getModificationItems() );
        persisted = true;
        return user;
    }
    
    
    public void delete() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        dao.delete( id );
        persisted = true;
    }

    
    private ModificationItem[] getModificationItems()
    {
        // new entries do not generate modification items
        if ( isNewEntry() )
        {
            return EMPTY_MODS;
        }

        if ( isUpdateNeeded() )
        {
            List mods = new ArrayList();
            if ( description.isUpdateNeeded() )
            {
                mods.add( description.getModificationItem() );
            }
            if ( referral.isUpdateNeeded() )
            {
                mods.add( referral.getModificationItem() );
            }
            if ( disabled.isUpdateNeeded() )
            {
                mods.add( disabled.getModificationItem() );
            }
            ModificationItem[] modArray = new ModificationItem[mods.size()];
            return ( ModificationItem[] ) mods.toArray( modArray );
        }
        return EMPTY_MODS;
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

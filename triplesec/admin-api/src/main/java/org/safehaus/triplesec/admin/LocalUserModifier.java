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

import org.safehaus.triplesec.admin.dao.LocalUserDao;


public class LocalUserModifier implements Constants
{
    private final String id;
    private final LocalUser archetype;
    private final LocalUserDao dao;
    private final SingleValuedField disabled;
    private final SingleValuedField firstName;
    private final SingleValuedField lastName;
    private final SingleValuedField password;
    private final SingleValuedField description;

    private final SingleValuedField address1;
    private final SingleValuedField address2;
    private final SingleValuedField city;
    private final SingleValuedField stateProvRegion;
    private final SingleValuedField zipPostalCode;
    private final SingleValuedField country;
    private final SingleValuedField company;
    private final SingleValuedField email;

    private boolean persisted = false;
    
    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    
    public LocalUserModifier( LocalUserDao dao, LocalUser archetype )
    {
        this.id = archetype.getId();
        this.dao = dao;
        this.archetype = archetype;
        this.firstName = new SingleValuedField( GIVENNAME_ID, archetype.getFirstName() );
        this.lastName = new SingleValuedField( SURNAME_ID, archetype.getLastName() );
        this.password = new SingleValuedField( PASSWORD_ID, archetype.getPassword() );
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
        this.disabled = new SingleValuedField( KRB5_DISABLED_ID, String.valueOf( archetype.isDisabled() ).toUpperCase() );
        this.address1 = new SingleValuedField( STREET_ID, archetype.getAddress1() );
        this.address2 = new SingleValuedField( POSTAL_ADDRESS_ID, archetype.getAddress2() );
        this.city = new SingleValuedField( LOCALITY_NAME_ID, archetype.getCity() );
        this.stateProvRegion = new SingleValuedField( STATE_PROVINCE_ID, archetype.getStateProvRegion() );
        this.zipPostalCode = new SingleValuedField( ZIP_POSTAL_CODE_ID, archetype.getZipPostalCode() );
        this.country = new SingleValuedField( COUNTRY_ID, archetype.getCountry() );
        this.company = new SingleValuedField( ORGANIZATION_ID, archetype.getCompany() );
        this.email = new SingleValuedField( EMAIL_ID, archetype.getEmail() );
    }


    public LocalUserModifier( LocalUserDao dao, String id, String firstName, String lastName, String password )
    {
        this.id = id;
        this.dao = dao;
        this.archetype = null;
        this.firstName = new SingleValuedField( GIVENNAME_ID, firstName );
        this.lastName = new SingleValuedField( SURNAME_ID, lastName );
        this.password = new SingleValuedField( PASSWORD_ID, password );
        this.description = new SingleValuedField( DESCRIPTION_ID, null );
        this.disabled = new SingleValuedField( KRB5_DISABLED_ID, "FALSE" );
        this.address1 = new SingleValuedField( STREET_ID, null );
        this.address2 = new SingleValuedField( POSTAL_ADDRESS_ID, null );
        this.city = new SingleValuedField( LOCALITY_NAME_ID, null );
        this.stateProvRegion = new SingleValuedField( STATE_PROVINCE_ID, null );
        this.zipPostalCode = new SingleValuedField( ZIP_POSTAL_CODE_ID, null );
        this.country = new SingleValuedField( COUNTRY_ID, null );
        this.company = new SingleValuedField( ORGANIZATION_ID, null );
        this.email = new SingleValuedField( EMAIL_ID, null );
    }


    // -----------------------------------------------------------------------
    // Mutators
    // -----------------------------------------------------------------------

    
    public LocalUserModifier setEmail( String email )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.email.setValue( email );
        return this;
    }
    
    
    public LocalUserModifier setAddress1( String address1 )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.address1.setValue( address1 );
        return this;
    }
    
    
    public LocalUserModifier setAddress2( String address2 )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.address2.setValue( address2 );
        return this;
    }
    
    
    public LocalUserModifier setCity( String city )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.city.setValue( city );
        return this;
    }
    
    
    public LocalUserModifier setStateProvRegion( String stateProvRegion )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.stateProvRegion.setValue( stateProvRegion );
        return this;
    }
    
    
    public LocalUserModifier setZipPostalCode( String zipPostalCode )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.zipPostalCode.setValue( zipPostalCode );
        return this;
    }
    
    
    public LocalUserModifier setCountry( String country )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.country.setValue( country );
        return this;
    }
    
    
    public LocalUserModifier setCompany( String company )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.company.setValue( company );
        return this;
    }
    
    
    public LocalUserModifier setDisabled( boolean disabled )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.disabled.setValue( String.valueOf( disabled ).toUpperCase() );
        return this;
    }

    
    public LocalUserModifier setFirstName( String firstName )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.firstName.setValue( firstName );
        return this;
    }
    
   
    public LocalUserModifier setLastName( String lastName )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.lastName.setValue( lastName );
        return this;
    }
    
   
    public LocalUserModifier setPassword( String password )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.password.setValue( password );
        return this;
    }
    
   
    public LocalUserModifier setDescription( String description )
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
    
    
    public boolean isUpdateNeeded()
    {
        return 
            description.isUpdateNeeded() || 
            firstName.isUpdateNeeded() || 
            lastName.isUpdateNeeded() || 
            password.isUpdateNeeded() || 
            disabled.isUpdateNeeded() ||
            address1.isUpdateNeeded() ||
            address2.isUpdateNeeded() ||
            city.isUpdateNeeded() ||
            stateProvRegion.isUpdateNeeded() ||
            zipPostalCode.isUpdateNeeded() ||
            country.isUpdateNeeded() ||
            company.isUpdateNeeded() ||
            email.isUpdateNeeded();
            
    }


    // -----------------------------------------------------------------------
    // Modifier Methods
    // -----------------------------------------------------------------------

    
    public LocalUser add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        LocalUser user = dao.add( id, description.getCurrentValue(), firstName.getCurrentValue(), 
            lastName.getCurrentValue(), password.getCurrentValue(), address1.getCurrentValue(), 
            address2.getCurrentValue(), city.getCurrentValue(), stateProvRegion.getCurrentValue(), 
            zipPostalCode.getCurrentValue(), country.getCurrentValue(), company.getCurrentValue(), 
            email.getCurrentValue(), parseBoolean( disabled.getCurrentValue().toLowerCase() ) );
        persisted = true;
        return user;
    }
    
    
    public LocalUser rename( String newName ) throws DataAccessException
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
        
        LocalUser user = dao.rename( newName, archetype );
        persisted = true;
        return user;
    }
    
    
    public LocalUser modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        LocalUser user = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), id, 
            description.getCurrentValue(), password.getCurrentValue(), firstName.getCurrentValue(),  
            lastName.getCurrentValue(), address1.getCurrentValue(), 
            address2.getCurrentValue(), city.getCurrentValue(), stateProvRegion.getCurrentValue(), 
            zipPostalCode.getCurrentValue(), country.getCurrentValue(), company.getCurrentValue(),
            email.getCurrentValue(), parseBoolean( disabled.getCurrentValue().toLowerCase() ), 
            getModificationItems() );
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
            if ( firstName.isUpdateNeeded() )
            {
                mods.add( firstName.getModificationItem() );
            }
            if ( lastName.isUpdateNeeded() )
            {
                mods.add( lastName.getModificationItem() );
            }
            if ( password.isUpdateNeeded() )
            {
                mods.add( password.getModificationItem() );
            }
            if ( disabled.isUpdateNeeded() )
            {
                mods.add( disabled.getModificationItem() );
            }
            if ( address1.isUpdateNeeded() )
            {
                mods.add( address1.getModificationItem() );
            }
            if ( address2.isUpdateNeeded() )
            {
                mods.add( address2.getModificationItem() );
            }
            if ( city.isUpdateNeeded() )
            {
                mods.add( city.getModificationItem() );
            }
            if ( stateProvRegion.isUpdateNeeded() )
            {
                mods.add( stateProvRegion.getModificationItem() );
            }
            if ( zipPostalCode.isUpdateNeeded() )
            {
                mods.add( zipPostalCode.getModificationItem() );
            }
            if ( country.isUpdateNeeded() )
            {
                mods.add( country.getModificationItem() );
            }
            if ( company.isUpdateNeeded() )
            {
                mods.add( company.getModificationItem() );
            }
            if ( email.isUpdateNeeded() )
            {
                mods.add( email.getModificationItem() );
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

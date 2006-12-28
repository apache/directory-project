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

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.dao.HauskeysUserDao;


public class HauskeysUserModifier implements Constants
{
    private final String id;
    private final HauskeysUser archetype;
    private final HauskeysUserDao dao;

    private final SingleValuedField firstName;
    private final SingleValuedField disabled;
    private final SingleValuedField lastName;
    private final SingleValuedField password;
    private final SingleValuedField description;
    private final SingleValuedField mobile;
    private final SingleValuedField email;
    private final SingleValuedField notifyBy;
    private final SingleValuedField mobileCarrier;
    private final SingleValuedField tokenPin;
    private final SingleValuedField midletName;
    private final SingleValuedField failuresInEpoch;
    private final SingleValuedField activationKey;
    private final SingleValuedField realm;
    private final SingleValuedField secret;
    private final SingleValuedField label;
    private final SingleValuedField movingFactor;
    
    private final SingleValuedField address1;
    private final SingleValuedField address2;
    private final SingleValuedField city;
    private final SingleValuedField stateProvRegion;
    private final SingleValuedField zipPostalCode;
    private final SingleValuedField country;
    private final SingleValuedField company;

    private boolean persisted = false;
    
    
    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    
    public HauskeysUserModifier( HauskeysUserDao dao, HauskeysUser archetype )
    {
        this.id = archetype.getId();
        this.dao = dao;
        this.archetype = archetype;
        this.firstName = new SingleValuedField( GIVENNAME_ID, archetype.getFirstName() );
        this.lastName = new SingleValuedField( SURNAME_ID, archetype.getLastName() );
        this.password = new SingleValuedField( PASSWORD_ID, archetype.getPassword() );
        this.description = new SingleValuedField( DESCRIPTION_ID, archetype.getDescription() );
        this.mobile = new SingleValuedField( MOBILE_ID, archetype.getMobile() );
        this.email = new SingleValuedField( EMAIL_ID, archetype.getEmail() );
        this.notifyBy = new SingleValuedField( NOTIFY_BY_ID, archetype.getNotifyBy() );
        this.mobileCarrier = new SingleValuedField( MOBILE_CARRIER_ID, archetype.getMobileCarrier() );
        this.tokenPin = new SingleValuedField( TOKEN_PIN_ID, archetype.getTokenPin() );
        this.midletName = new SingleValuedField( MIDLE_NAME_ID, archetype.getMidletName() );
        this.failuresInEpoch = new SingleValuedField( FAILURES_IN_EPOCH_ID, archetype.getFailuresInEpoch() );
        this.activationKey = new SingleValuedField( ACTIVATION_KEY_ID, archetype.getActivationKey() );
        this.realm = new SingleValuedField( REALM_ID, archetype.getRealm() );
        this.secret = new SingleValuedField( SECRET_ID, archetype.getSecret() );
        this.label = new SingleValuedField( LABEL_ID, archetype.getLabel() );
        this.movingFactor = new SingleValuedField( MOVING_FACTOR_ID, archetype.getMovingFactor() );
        this.address1 = new SingleValuedField( STREET_ID, archetype.getAddress1() );
        this.address2 = new SingleValuedField( POSTAL_ADDRESS_ID, archetype.getAddress2() );
        this.city = new SingleValuedField( LOCALITY_NAME_ID, archetype.getCity() );
        this.stateProvRegion = new SingleValuedField( STATE_PROVINCE_ID, archetype.getStateProvRegion() );
        this.zipPostalCode = new SingleValuedField( ZIP_POSTAL_CODE_ID, archetype.getZipPostalCode() );
        this.country = new SingleValuedField( COUNTRY_ID, archetype.getCountry() );
        this.company = new SingleValuedField( ORGANIZATION_ID, archetype.getCompany() );
        this.disabled = new SingleValuedField( KRB5_DISABLED_ID, String.valueOf( archetype.isDisabled() ).toUpperCase() );
    }


    public HauskeysUserModifier( HauskeysUserDao dao, String id, String firstName, String lastName, 
        String password )
    {
        this.id = id;
        this.dao = dao;
        this.archetype = null;
        this.firstName = new SingleValuedField( GIVENNAME_ID, firstName );
        this.lastName = new SingleValuedField( SURNAME_ID, lastName );
        this.disabled = new SingleValuedField( KRB5_DISABLED_ID, "FALSE" );

        this.password = new SingleValuedField( PASSWORD_ID, password );
        this.description = new SingleValuedField( DESCRIPTION_ID, null );
        this.mobile = new SingleValuedField( MOBILE_ID, null );
        this.email = new SingleValuedField( EMAIL_ID, null );
        this.notifyBy = new SingleValuedField( NOTIFY_BY_ID, null );
        this.mobileCarrier = new SingleValuedField( MOBILE_CARRIER_ID, null );
        this.tokenPin = new SingleValuedField( TOKEN_PIN_ID, null );
        this.midletName = new SingleValuedField( MIDLE_NAME_ID, null );
        this.failuresInEpoch = new SingleValuedField( FAILURES_IN_EPOCH_ID, null );
        this.activationKey = new SingleValuedField( ACTIVATION_KEY_ID, null );
        this.realm = new SingleValuedField( REALM_ID, null );
        this.secret = new SingleValuedField( SECRET_ID, null );
        this.label = new SingleValuedField( LABEL_ID, null );
        this.movingFactor = new SingleValuedField( MOVING_FACTOR_ID, null );
        this.address1 = new SingleValuedField( STREET_ID, null );
        this.address2 = new SingleValuedField( POSTAL_ADDRESS_ID, null );
        this.city = new SingleValuedField( LOCALITY_NAME_ID, null );
        this.stateProvRegion = new SingleValuedField( STATE_PROVINCE_ID, null );
        this.zipPostalCode = new SingleValuedField( ZIP_POSTAL_CODE_ID, null );
        this.country = new SingleValuedField( COUNTRY_ID, null );
        this.company = new SingleValuedField( ORGANIZATION_ID, null );
    }


    // -----------------------------------------------------------------------
    // Mutators
    // -----------------------------------------------------------------------

    
    public HauskeysUserModifier setAddress1( String address1 )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.address1.setValue( address1 );
        return this;
    }
    
    
    public HauskeysUserModifier setAddress2( String address2 )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.address2.setValue( address2 );
        return this;
    }
    
    
    public HauskeysUserModifier setCity( String city )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.city.setValue( city );
        return this;
    }
    
    
    public HauskeysUserModifier setStateProvRegion( String stateProvRegion )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.stateProvRegion.setValue( stateProvRegion );
        return this;
    }
    
    
    public HauskeysUserModifier setZipPostalCode( String zipPostalCode )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.zipPostalCode.setValue( zipPostalCode );
        return this;
    }
    
    
    public HauskeysUserModifier setCountry( String country )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.country.setValue( country );
        return this;
    }
    
    
    public HauskeysUserModifier setCompany( String company )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.company.setValue( company );
        return this;
    }
    
    
    public HauskeysUserModifier setDisabled( boolean disabled )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.disabled.setValue( String.valueOf( disabled ).toUpperCase() );
        return this;
    }

    
    public HauskeysUserModifier setFirstName( String firstName )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.firstName.setValue( firstName );
        return this;
    }
    
   
    public HauskeysUserModifier setLastName( String lastName )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.lastName.setValue( lastName );
        return this;
    }
    
   
    public HauskeysUserModifier setPassword( String password )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.password.setValue( password );
        return this;
    }
    
   
    public HauskeysUserModifier setDescription( String description )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.description.setValue( description );
        return this;
    }
    
   
    public HauskeysUserModifier setMobile( String mobile )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.mobile.setValue( mobile );
        return this;
    }
    
   
    public HauskeysUserModifier setEmail( String email )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.email.setValue( email );
        return this;
    }
    
   
    public HauskeysUserModifier setNotifyBy( String notifyBy )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.notifyBy.setValue( notifyBy );
        return this;
    }
    
   
    public HauskeysUserModifier setMobileCarrier( String mobileCarrier )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.mobileCarrier.setValue( mobileCarrier );
        return this;
    }
    
   
    public HauskeysUserModifier setTokenPin( String tokenPin )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.tokenPin.setValue( tokenPin );
        return this;
    }
    
   
    public HauskeysUserModifier setMidletName( String midletName )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.midletName.setValue( midletName );
        return this;
    }
    
   
    public HauskeysUserModifier setFailuresInEpoch( String failuresInEpoch )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.failuresInEpoch.setValue( failuresInEpoch );
        return this;
    }
    
   
    public HauskeysUserModifier setActivationKey( String activationKey )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.activationKey.setValue( activationKey );
        return this;
    }
    
   
    public HauskeysUserModifier setRealm( String realm )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.realm.setValue( realm );
        return this;
    }
    
   
    public HauskeysUserModifier setSecret( String secret )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.secret.setValue( secret );
        return this;
    }

    
    public HauskeysUserModifier setLabel( String label )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.label.setValue( label );
        return this;
    }

    
    public HauskeysUserModifier setMovingFactor( String movingFactor )
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        this.movingFactor.setValue( movingFactor );
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
            disabled.isUpdateNeeded() ||
            description.isUpdateNeeded() || 
            firstName.isUpdateNeeded() || 
            lastName.isUpdateNeeded() || 
            password.isUpdateNeeded() ||
            mobile.isUpdateNeeded() ||
            email.isUpdateNeeded() ||
            notifyBy.isUpdateNeeded() ||
            mobileCarrier.isUpdateNeeded() ||
            tokenPin.isUpdateNeeded() ||
            midletName.isUpdateNeeded() ||
            failuresInEpoch.isUpdateNeeded() ||
            activationKey.isUpdateNeeded() ||
            realm.isUpdateNeeded() ||
            secret.isUpdateNeeded() ||
            label.isUpdateNeeded() ||
            movingFactor.isUpdateNeeded() ||
            address1.isUpdateNeeded() ||
            address2.isUpdateNeeded() ||
            city.isUpdateNeeded() ||
            stateProvRegion.isUpdateNeeded() ||
            zipPostalCode.isUpdateNeeded() ||
            country.isUpdateNeeded() ||
            company.isUpdateNeeded();
    }


    // -----------------------------------------------------------------------
    // Modifier Methods
    // -----------------------------------------------------------------------

    
    public HauskeysUser add() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        HauskeysUser user = dao.add( id, description.getCurrentValue(), firstName.getCurrentValue(),
            lastName.getCurrentValue(), password.getCurrentValue(), mobile.getCurrentValue(), 
            email.getCurrentValue(), notifyBy.getCurrentValue(), mobileCarrier.getCurrentValue(), 
            tokenPin.getCurrentValue(), midletName.getCurrentValue(), failuresInEpoch.getCurrentValue(),
            activationKey.getCurrentValue(), realm.getCurrentValue(), secret.getCurrentValue(), 
            label.getCurrentValue(), movingFactor.getCurrentValue(), address1.getCurrentValue(), 
            address2.getCurrentValue(), city.getCurrentValue(), stateProvRegion.getCurrentValue(), 
            zipPostalCode.getCurrentValue(), country.getCurrentValue(), company.getCurrentValue(), 
            parseBoolean( disabled.getCurrentValue().toLowerCase() ) );
        persisted = true;
        return user;
    }
    
    
    public HauskeysUser rename( String newName ) throws DataAccessException
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
        
        HauskeysUser user = dao.rename( newName, archetype );
        persisted = true;
        return user;
    }
    
    
    public HauskeysUser modify() throws DataAccessException
    {
        if ( persisted )
        {
            throw new IllegalStateException( INVALID_MSG );
        }
        HauskeysUser user = dao.modify( archetype.getCreatorsName(), archetype.getCreateTimestamp(), id, 
            description.getCurrentValue(), firstName.getCurrentValue(), lastName.getCurrentValue(), 
            password.getCurrentValue(), mobile.getCurrentValue(), email.getCurrentValue(), 
            notifyBy.getCurrentValue(), mobileCarrier.getCurrentValue(), tokenPin.getCurrentValue(), 
            midletName.getCurrentValue(), failuresInEpoch.getCurrentValue(), activationKey.getCurrentValue(), 
            realm.getCurrentValue(), secret.getCurrentValue(), label.getCurrentValue(), 
            movingFactor.getCurrentValue(), address1.getCurrentValue(), 
            address2.getCurrentValue(), city.getCurrentValue(), stateProvRegion.getCurrentValue(), 
            zipPostalCode.getCurrentValue(), country.getCurrentValue(), company.getCurrentValue(), 
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
            if ( mobile.isUpdateNeeded() )
            {
                mods.add( mobile.getModificationItem() );
            }
            if ( email.isUpdateNeeded() )
            {
                mods.add( email.getModificationItem() );
            }
            if ( notifyBy.isUpdateNeeded() )
            {
                mods.add( notifyBy.getModificationItem() );
            }
            if ( mobileCarrier.isUpdateNeeded() )
            {
                mods.add( mobileCarrier.getModificationItem() );
            }
            if ( tokenPin.isUpdateNeeded() )
            {
                mods.add( tokenPin.getModificationItem() );
            }
            if ( midletName.isUpdateNeeded() )
            {
                mods.add( midletName.getModificationItem() );
            }
            if ( failuresInEpoch.isUpdateNeeded() )
            {
                mods.add( failuresInEpoch.getModificationItem() );
            }
            if ( activationKey.isUpdateNeeded() )
            {
                mods.add( activationKey.getModificationItem() );
            }
            if ( realm.isUpdateNeeded() )
            {
                mods.add( realm.getModificationItem() );
            }
            if ( secret.isUpdateNeeded() )
            {
                mods.add( secret.getModificationItem() );
            }
            if ( label.isUpdateNeeded() )
            {
                mods.add( label.getModificationItem() );
            }
            if ( movingFactor.isUpdateNeeded() )
            {
                mods.add( movingFactor.getModificationItem() );
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
            if ( disabled.isUpdateNeeded() )
            {
                mods.add( disabled.getModificationItem() );
            }
            
            // if first or last name has changed modify the common name
            if ( firstName.isUpdateNeeded() || lastName.isUpdateNeeded() )
            {
                mods.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute( "cn", 
                    firstName.getCurrentValue() + " " + lastName.getCurrentValue() ) ) );
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

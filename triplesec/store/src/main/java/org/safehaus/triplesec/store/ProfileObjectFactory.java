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
package org.safehaus.triplesec.store;


import java.util.Hashtable;

import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import org.apache.directory.server.kerberos.shared.store.KerberosAttribute;
import org.apache.directory.shared.ldap.util.StringTools;
import org.safehaus.profile.BaseServerProfileModifier;


/**
 * An ObjectFactory that resusitates objects from directory attributes.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ProfileObjectFactory implements DirObjectFactory
{
    private static boolean parseBoolean( String bool )
    {
        if ( bool.toLowerCase().equals( "true" ) )
        {
            return true;
        }
        
        return false;
    }
    
    
    public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable environment, Attributes attrs ) throws NamingException
    {
        if ( attrs == null || attrs.get( "objectClass" ) == null || ! attrs.get( "objectClass" ).contains( "safehausProfile" ) )
        {
            return null;
        }

        BaseServerProfileModifier modifier = new BaseServerProfileModifier();
        modifier.setUserId( ( String ) attrs.get( "safehausUid" ).get() );
        modifier.setRealm( ( String ) attrs.get( "safehausRealm" ).get() );
        modifier.setLabel( ( String ) attrs.get( "safehausLabel" ).get() );
        modifier.setTokenPin( ( String ) attrs.get( "safehausTokenPin" ).get() );
        modifier.setFactor( Long.parseLong( ( String ) attrs.get( "safehausFactor" ).get() ) );
        
        if ( attrs.get( KerberosAttribute.ACCOUNT_DISABLED ) != null )
        {
            modifier.setDisabled( parseBoolean( ( ( String ) 
                attrs.get( KerberosAttribute.ACCOUNT_DISABLED ).get() ).toLowerCase() ) );
        }

        Object secret = attrs.get( "safehausSecret" ).get();
        if ( secret instanceof String )
        {
            modifier.setSecret( StringTools.getBytesUtf8( ( String ) secret ) );
        }
        else
        {
            modifier.setSecret( ( byte[] ) secret );
        }

        Object password = attrs.get( "userPassword" ).get();
        if ( password instanceof String )
        {
            modifier.setPassword( StringTools.getBytesUtf8( ( String ) password ) );
        }
        else
        {
            modifier.setPassword( ( byte[] ) password );
        }
        
        modifier.setFailuresInEpoch( Integer.parseInt( ( String ) attrs.get( "safehausFailuresInEpoch" ).get() ) );
        modifier.setResynchCount( Integer.parseInt( ( String ) attrs.get( "safehausResynchCount" ).get() ) );

        if ( attrs.get( "safehausInfo" ) != null )
        {
            modifier.setInfo( ( String ) attrs.get( "safehausInfo" ).get() );
        }

        if ( attrs.get( "safehausActivationKey" ) != null )
        {
            modifier.setActivationKey( ( String ) attrs.get( "safehausActivationKey" ).get() );
        }

        return modifier.getServerProfile();
    }


    public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable environment ) throws Exception
    {
        throw new UnsupportedOperationException( "Attributes required to resusitate an OTP account!" );
    }
}

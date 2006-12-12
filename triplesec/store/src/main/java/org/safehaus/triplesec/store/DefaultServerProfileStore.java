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


import java.util.ArrayList;

import javax.naming.directory.*;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.OperationNotSupportedException;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.message.LockableAttributesImpl;
import org.apache.directory.shared.ldap.message.LockableAttributeImpl;

import org.safehaus.profile.ServerProfile;


/**
 * The default Safehaus store implementation which is backed by the ApacheDS.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class DefaultServerProfileStore implements ServerProfileStore
{
    /** Empty array of modification items so we do not create on every time */
    private static final ModificationItem[] EMPTY = new ModificationItem[0];

    /** temporary property to lookup alternate monitor */
    public static final String MONITOR_PROP = "org.safehaus.store.monitor";

    // ------------------------------------------------------------------------
    // Members
    // ------------------------------------------------------------------------

    /** the context under which users are created */
    private DirContext userContext;

    /** the StoreMonitor used to report notable events to */
    private StoreMonitor monitor = new StoreMonitorAdapter();

    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------

    /**
     * Creates the embedded ApacheDS principal store.
     *
     * @param userContext the context under which users are created.
     */
    public DefaultServerProfileStore( DirContext userContext ) throws NamingException
    {
        this.userContext = userContext;
    }


    // ------------------------------------------------------------------------
    // ServerProfileStore methods
    // ------------------------------------------------------------------------


    public void init() throws NamingException
    {
    }


    private ProfileObjectFactory objectFactory = new ProfileObjectFactory();
    
    public ServerProfile getProfile( KerberosPrincipal principal ) throws NamingException
    {
        Attributes attributes = new LockableAttributesImpl();
        attributes.put( PRINCIPAL_ATTR, principal.getName() );
        SearchResult result = null;
        Attributes attrs = null;

        NamingEnumeration list = userContext.search( "", attributes );
        if ( list.hasMore() )
        {
            result = ( SearchResult ) list.next();
            attrs = result.getAttributes();
        }
        list.close();
        
        if ( attrs == null || result == null )
        {
            monitor.storeFailure( this, principal, "principal not in store" );
            return null;
        }
        
        Object obj = objectFactory.getObjectInstance( null, null, null, null, attrs );
        if ( obj instanceof ServerProfile )
        {
            monitor.profileAccessed( this, ( ServerProfile ) obj );
            return ( ServerProfile ) obj;
        }

        monitor.storeFailure( this, principal, "failed to recognize principal type" );
        return null;
    }


    private SearchResult getProfileEntry( KerberosPrincipal principal ) throws NamingException
    {
        Attributes attributes = new LockableAttributesImpl();
        attributes.put( PRINCIPAL_ATTR, principal.getName() );
        SearchResult result = null;
        NamingEnumeration list = userContext.search( "", attributes );
        if ( list.hasMore() )
        {
            result = ( SearchResult ) list.next();
        }
        list.close();
        return result;
    }


    public boolean hasProfile( KerberosPrincipal principal ) throws NamingException
    {
        Attributes attributes = new LockableAttributesImpl();
        attributes.put( PRINCIPAL_ATTR, principal.getName() );
        SearchResult result = null;
        Attributes attrs = null;
        NamingEnumeration list = userContext.search( "", attributes );

        if ( list.hasMore() )
        {
            result = ( SearchResult ) list.next();
            attrs = result.getAttributes();
        }
        list.close();

        if ( attrs == null || result == null )
        {
            return false;
        }

        return true;
    }


    public void add( ServerProfile profile ) throws NamingException
    {
        userContext.bind( "uid=" + profile.getUserId(), profile );
        monitor.profileAdded( this, profile );
    }


    public void delete( KerberosPrincipal principal ) throws NamingException
    {
        String msg = "delete in org.safehaus.triplesec.store.DefaultServerProfileStore not implemented!";
        throw new NotImplementedException( msg );
    }


    public void update( KerberosPrincipal oldPrincipal, ServerProfile updated ) throws NamingException
    {
        String oldId = oldPrincipal.getName();
        oldId = oldId.split( "@" )[0];
        String oldRealm = oldPrincipal.getName();
        oldRealm = oldRealm.split( "@" )[1];

        if ( ! oldId.equals( updated.getUserId() ) || ! oldRealm.equals( updated.getRealm() ) )
        {
            String msg = "Attempt to move or rename existing profile not yet supported!";
            OperationNotSupportedException onse = new OperationNotSupportedException( msg );
            monitor.storeFailure( this, onse );
            throw onse;
        }

        ArrayList list = new ArrayList();  // list of modification items
        SearchResult result = getProfileEntry( oldPrincipal );
        Attributes original = result.getAttributes();

        if ( updated.getFactor() != getLong( "safehausFactor", original ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausFactor" );
            attr.add( Long.toString( updated.getFactor() ) );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( updated.getResynchCount() != getInt( "safehausResynchCount", original ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausResynchCount" );
            attr.add( Integer.toString( updated.getResynchCount() ) );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( updated.getFailuresInEpoch() != getInt( "safehausFailuresInEpoch", original ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausFailuresInEpoch" );
            attr.add( Integer.toString( updated.getFailuresInEpoch() ) );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( ! updated.getUserId().equals( getString( "safehausUid", original ) ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausUid" );
            attr.add( updated.getUserId() );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( ! updated.getRealm().equals( getString( "safehausRealm", original ) ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausRealm" );
            attr.add( updated.getRealm() );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( ! updated.getLabel().equals( getString( "safehausLabel", original ) ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausLabel" );
            attr.add( updated.getLabel() );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( ! updated.getSecret().equals( getString( "safehausSecret", original ) ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausSecret" );
            attr.add( updated.getSecret() );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( updated.getInfo() != null && ! updated.getInfo().equals( getString( "safehausInfo", original ) ) )
        {
            Attribute attr = new LockableAttributeImpl( "safehausInfo" );
            attr.add( updated.getInfo() );
            list.add( new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr ) );
        }

        if ( result.getObject() instanceof DirContext )
        {
            DirContext entryCtx = ( DirContext ) result.getObject();
            entryCtx.modifyAttributes( "", ( ModificationItem[] ) list.toArray( EMPTY ) );
            return;
        }

        LdapDN base = new LdapDN( userContext.getNameInNamespace() );
        LdapDN dn = new LdapDN( result.getName() );
        LdapDN rdn = ( LdapDN ) dn.getSuffix( base.size() );
        ModificationItem[] mods = ( ModificationItem[] ) list.toArray( EMPTY );
        userContext.modifyAttributes( rdn, mods );
        monitor.profileUpdated( this, updated, mods );
    }


    public void setMonitor( StoreMonitor monitor )
    {
        this.monitor = monitor;
    }


    // ------------------------------------------------------------------------
    // private utility methods
    // ------------------------------------------------------------------------


    private long getLong( String id, Attributes attrs ) throws NamingException
    {
        Attribute attr = attrs.get( id );
        if ( attr == null || attr.size() == 0 )
        {
            throw new NamingException( "Attribute not found or does not have a value!" );
        }
        return Long.parseLong( ( String ) attr.get() );
    }


    private int getInt( String id, Attributes attrs ) throws NamingException
    {
        Attribute attr = attrs.get( id );
        if ( attr == null || attr.size() == 0 )
        {
            throw new NamingException( "Attribute not found or does not have a value!" );
        }
        return Integer.parseInt( ( String ) attr.get() );
    }


    private String getString( String id, Attributes attrs ) throws NamingException
    {
        Attribute attr = attrs.get( id );

        if ( attr == null || attr.size() == 0 )
        {
            throw new NamingException( "Attribute not found or does not have a value!" );
        }

        if ( attr.get() instanceof String )
        {
            return ( String ) attr.get();
        }
        else if ( attr.get() instanceof byte[] )
        {
            return new String( ( byte[] ) attr.get() );
        }

        return attr.get().toString();
    }
}

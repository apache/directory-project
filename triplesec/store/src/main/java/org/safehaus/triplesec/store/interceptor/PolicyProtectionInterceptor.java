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
package org.safehaus.triplesec.store.interceptor;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;

import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.FilterParserImpl;
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.server.core.configuration.InterceptorConfiguration;
import org.apache.directory.server.core.interceptor.BaseInterceptor;
import org.apache.directory.server.core.interceptor.Interceptor;
import org.apache.directory.server.core.interceptor.NextInterceptor;
import org.apache.directory.server.core.DirectoryServiceConfiguration;
import org.apache.directory.server.core.invocation.InvocationStack;
import org.apache.directory.server.core.partition.PartitionNexusProxy;


/**
 * An ApacheDS {@link Interceptor} that prevents users from deleting
 * (or renaming) policy entries (permissions and roles) used by roles and profiles.  
 *
 * @author Trustin Lee
 * @version $Rev: 956 $, $Date: 2006-09-21 10:10:21 -0400 (Thu, 21 Sep 2006) $
 */
public class PolicyProtectionInterceptor extends BaseInterceptor
{
    private DirectoryServiceConfiguration factoryConfiguration;
    private ApplicationAciManager aciManager = null;
    

    public PolicyProtectionInterceptor()
    {
    }

    
    public void init( DirectoryServiceConfiguration factoryCfg, InterceptorConfiguration cfg ) throws NamingException
    {
        factoryConfiguration = factoryCfg;
        aciManager = new ApplicationAciManager( factoryCfg.getGlobalRegistries().getAttributeTypeRegistry() );
    }

    
    public void add( NextInterceptor next, LdapDN name, Attributes attrs ) throws NamingException
    {
        boolean policyEntry = false;
        boolean isApplication = false;

        Attribute attr = getObjectClass( attrs );
        NamingEnumeration ocList = attr.getAll();
        try
        {
            while( ocList.hasMore() )
            {
                String value = String.valueOf( ocList.next() );
                if( "policyPermission".equalsIgnoreCase( value ) )
                {
                    checkNewPolicyEntry( next, name, "2.5.4.11=permissions" );
                    policyEntry = true;
                }
                else if( "policyRole".equalsIgnoreCase( value ) )
                {
                    checkNewPolicyEntry( next, name, "2.5.4.11=roles" );
                    policyEntry = true;
                }
                else if( "policyProfile".equalsIgnoreCase( value ) )
                {
                    checkNewPolicyEntry( next, name, "2.5.4.11=profiles" );
                    policyEntry = true;
                }
                else if( "policyApplication".equalsIgnoreCase( value ) )
                {
                    isApplication = true;
                }
            }
        }
        finally
        {
            ocList.close();
        }

        if( !policyEntry )
        {
            checkNewNonPolicyEntry( next, name );
        }
        else
        {
            // Check if all grants, denials, and roles are valid.
            LdapDN baseName = ( LdapDN ) name.clone();
            baseName.remove( baseName.size() -1 );
            baseName.remove( baseName.size() -1 );
            NamingEnumeration attrList = attrs.getAll();
            try
            {
                while( attrList.hasMore() )
                {
                    attr = ( Attribute ) attrList.next();
                    checkAttributeAddition( next, baseName, attr );
                }
            }
            finally
            {
                ocList.close();
            }
        }

        next.add( name, attrs );

        if( isApplication )
        {
            aciManager.appAdded( name );
        }
    }

    
    public void delete( NextInterceptor next, LdapDN name ) throws NamingException
    {
        boolean isApplication = isPolicyApplication( name );

        LdapDN baseName = getBaseName( next, name );
        if( baseName == null )
        {
            next.delete( name );

            if ( isApplication )
            {
                aciManager.appRemoved( name );
            }
            return;
        }

        checkNotInUse( next, baseName, name );
        next.delete( name );
        if ( isApplication )
        {
            aciManager.appRemoved( name );
        }
    }

    
    public void modify( NextInterceptor next, LdapDN name, int modOp, Attributes attrs ) throws NamingException
    {
        LdapDN baseName = getBaseName( next, name );
        if( baseName == null )
        {
            next.modify( name, modOp, attrs );
            return;
        }

        NamingEnumeration e = attrs.getAll();
        try
        {
            while( e.hasMore() )
            {
                Attribute attr = ( Attribute ) e.next();
                if( attr != null )
                {
                    switch( modOp )
                    {
                    case DirContext.ADD_ATTRIBUTE:
                    case DirContext.REPLACE_ATTRIBUTE:
                        checkAttributeAddition( next, baseName, attr );
                        break;
                    case DirContext.REMOVE_ATTRIBUTE:
                        checkAttributeRemoval( attr );
                        break;
                    }
                }
            }
        }
        finally
        {
            e.close();
        }

        next.modify( name, modOp, attrs );
    }

    
    public void modify( NextInterceptor next, LdapDN name, ModificationItem[] modItems ) throws NamingException
    {
        LdapDN baseName = getBaseName( next, name );
        if( baseName == null )
        {
            next.modify( name, modItems );
            return;
        }

        for( int i = modItems.length - 1; i >= 0; i-- )
        {
            Attribute attr = modItems[ i ].getAttribute();
            switch( modItems[ i ].getModificationOp() ) {
            case DirContext.ADD_ATTRIBUTE:
            case DirContext.REPLACE_ATTRIBUTE:
                checkAttributeAddition( next, baseName, attr );
                break;
            case DirContext.REMOVE_ATTRIBUTE:
                checkAttributeRemoval( attr );
                break;
            }
        }

        next.modify( name, modItems );
    }

    
    public void modifyRn( NextInterceptor next, LdapDN name, String newRN, boolean deleteOldRN ) throws NamingException
    {
        PartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
        Attributes entry = proxy.lookup( name, ApplicationAciManager.LOOKUP_BYPASS );
        Attribute oc = entry.get( "objectClass" );
        boolean isApplication = false;

        for ( int ii = 0; ii < oc.size(); ii++ )
        {
            String item = ( String ) oc.get( ii );
            if ( item.equalsIgnoreCase( "policyApplication" ) )
            {
                isApplication = true;
            }
        }

        // calculate the new name
        LdapDN newNameUpDn = ( LdapDN ) name.clone();
        newNameUpDn.remove( name.size() - 1 );
        newNameUpDn.add( newRN );
        LdapDN rdn = new LdapDN( newRN );
        LdapDN newDn = ( LdapDN ) name.clone();
        newDn.remove( name.size() - 1 );
        newDn.add( rdn.get( 0 ) );

        LdapDN baseName = getBaseName( next, name );
        if( baseName == null )
        {
            if ( isApplication )
            {
                // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
                aciManager.removeApplicationSubentry( proxy, name );
            }
            next.modifyRn( name, newRN, deleteOldRN );
            if ( isApplication )
            {
                aciManager.addApplicationSubentry( proxy, newDn );
            }

            return;
        }

        checkModification( next, baseName, name );

        if ( isApplication )
        {
            // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
            aciManager.removeApplicationSubentry( proxy, name );
        }
        next.modifyRn( name, newRN, deleteOldRN );
        if ( isApplication )
        {
            aciManager.addApplicationSubentry( proxy, newDn );
        }
    }

    public void move( NextInterceptor next, LdapDN name, LdapDN newParentName, String newRN, boolean deleteOldRN ) throws NamingException
    {
        PartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
        Attributes entry = proxy.lookup( name, ApplicationAciManager.LOOKUP_BYPASS );
        Attribute oc = entry.get( "objectClass" );
        boolean isApplication = false;

        for ( int ii = 0; ii < oc.size(); ii++ )
        {
            String item = ( String ) oc.get( ii );
            if ( item.equalsIgnoreCase( "policyApplication" ) )
            {
                isApplication = true;
            }
        }

        // calculate the new name
        LdapDN newNameUpDn = ( LdapDN ) newParentName.clone();
        newNameUpDn.add( newRN );
        LdapDN rdn = new LdapDN( newRN );
        LdapDN newDn = ( LdapDN ) newParentName.clone();
        newDn.add( rdn.get( 0 ) );

        LdapDN baseName = getBaseName( next, name );
        if( baseName == null )
        {
            if ( isApplication )
            {
                // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
                aciManager.removeApplicationSubentry( proxy, name );
            }
            next.move( name, newParentName, newRN, deleteOldRN );
            if ( isApplication )
            {
                aciManager.addApplicationSubentry( proxy, newDn );
            }
            return;
        }

        checkModification( next, baseName, name );
        if ( isApplication )
        {
            // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
            aciManager.removeApplicationSubentry( proxy, name );
        }
        next.move( name, newParentName, newRN, deleteOldRN );
        if ( isApplication )
        {
            aciManager.addApplicationSubentry( proxy, newDn );
        }
    }

    public void move( NextInterceptor next, LdapDN name, LdapDN newParentName ) throws NamingException
    {
        PartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
        Attributes entry = proxy.lookup( name, ApplicationAciManager.LOOKUP_BYPASS );
        Attribute oc = entry.get( "objectClass" );
        boolean isApplication = false;

        for ( int ii = 0; ii < oc.size(); ii++ )
        {
            String item = ( String ) oc.get( ii );
            if ( item.equalsIgnoreCase( "policyApplication" ) )
            {
                isApplication = true;
            }
        }

        // calculate the new name
        LdapDN newDn = ( LdapDN ) newParentName.clone();
        newDn.add( name.get( name.size() - 1 ) );

        LdapDN baseName = getBaseName( next, name );
        if( baseName == null )
        {
            if ( isApplication )
            {
                // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
                aciManager.removeApplicationSubentry( proxy, name );
            }
            next.move( name, newParentName );
            if ( isApplication )
            {
                aciManager.addApplicationSubentry( proxy, newDn );
            }
            return;
        }

        checkModification( next, baseName, name );
        if ( isApplication )
        {
            // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
            aciManager.removeApplicationSubentry( proxy, name );
        }
        next.move( name, newParentName );
        if ( isApplication )
        {
            aciManager.addApplicationSubentry( proxy, newDn );
        }
    }

    private LdapDN getBaseName( NextInterceptor next, LdapDN name ) throws NamingException
    {
        if( name.size() >= 3 )
        {
            Attributes attrs = next.lookup( name );
            Attribute attr = getObjectClass( attrs );
            NamingEnumeration e = attr.getAll();
            try
            {
                while( e.hasMore() )
                {
                    String value = String.valueOf( e.next() );
                    if( "policyPermission".equalsIgnoreCase( value ) ||
                            "policyProfile".equalsIgnoreCase( value ) ||
                            "policyRole".equalsIgnoreCase( value ) ) 
                    {
                        LdapDN retVal = ( LdapDN ) name.clone();
                        retVal.remove( retVal.size() -1 );
                        retVal.remove( retVal.size() -1 );
                        
                        return retVal;
                    }
                    if( "policyApplication".equalsIgnoreCase( value ) )
                    {
                        return name;
                    }
                }
            }
            finally
            {
                e.close();
            }
        }

        if( name.size() >= 2 )
        {
            // Try the parent node in case we add group entries.
            try
            {
                name = ( LdapDN ) name.clone();
                name.remove( name.size() - 1 );
                Attributes attrs = next.lookup( name );
                Attribute attr = getObjectClass( attrs );
                NamingEnumeration e = attr.getAll();
                try
                {
                    while( e.hasMore() )
                    {
                        String value = String.valueOf( e.next() );
                        if( "policyApplication".equalsIgnoreCase( value ) )
                        {
                            return name;
                        }
                    }
                }
                finally
                {
                    e.close();
                }
            }
            catch( NamingException e )
            {
                // Ignore silently
            }
        }

        return null;
    }
    

    /**
     * Checks to see if a policy entry should be added under a parent.
     * 
     * @param next
     * @param name
     * @param parentName
     * @throws NamingException
     */
    private void checkNewPolicyEntry( NextInterceptor next, LdapDN name, String parentName ) throws NamingException
    {
        LdapDN parentDn = ( LdapDN ) name.clone();
        parentDn.remove( parentDn.size() -1 );

        if( name.size() < 3 )
        {
            throw new SchemaViolationException( "Name is too short: " + name );
        }

        
        if( !parentName.equalsIgnoreCase( parentDn.getRdn().toString() ) )
        {
            throw new SchemaViolationException( "Parent entry for policyPermissions must be '" +
                parentName + "': " + name );
        }

        parentDn.remove( parentDn.size() -1 );
        if( !isPolicyApplication( parentDn ) )
        {
            throw new SchemaViolationException( "Grandparent must be a policyApplication." );
        }
    }
    
    
    private static final String[] OBJECT_CLASS_ATTRS = { "objectClass" };
    private boolean isPolicyApplication( LdapDN dn ) throws NamingException
    {
        PartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
        Attributes entry = proxy.lookup( dn, OBJECT_CLASS_ATTRS, PartitionNexusProxy.LOOKUP_BYPASS );
        Attribute oc = getObjectClass( entry );

        NamingEnumeration list = oc.getAll();
        try
        {
            while( list.hasMore() )
            {
                if( "policyApplication".equalsIgnoreCase( String.valueOf( list.next() ) ) )
                {
                    return true;
                }
            }
        }
        finally {
            list.close();
        }
        
        return false;
    }

    
    private void checkNewNonPolicyEntry( NextInterceptor next, LdapDN name ) throws NamingException
    {
        if( name.size() < 3 )
        {
            return;
        }

        if( isEntityGroup( name ) )
        {
            // Strip the name once more; this will prevent this method
            // from throwing an exception when these entries are added
            // just beneath policyApplication entry.
            name = ( LdapDN ) name.getPrefix( 1 );
        }

        name = ( LdapDN ) name.clone();
        do
        {
            name.remove( name.size() - 1 );
            try
            {
                Attributes entry = next.lookup( name );
                Attribute attr = getObjectClass( entry );
                NamingEnumeration e = attr.getAll();
                try
                {
                    while( e.hasMore() )
                    {
                        if( "policyApplication".equalsIgnoreCase( String.valueOf( e.next() ) ) )
                        {
                            throw new SchemaViolationException( "Non-policy entries cannot reside under policyApplication." );
                        }
                    }
                }
                finally
                {
                    e.close();
                }
            }
            catch( SchemaViolationException e )
            {
                throw e;
            }
            catch( Exception e )
            {
                // Ignore silently.
            }
        }
        while( name.size() > 1 );
    }
    

    private void checkAttributeAddition( NextInterceptor next, LdapDN baseName, Attribute attr ) 
        throws NamingException, SchemaViolationException
    {
        
        // If the attribute is a permission
        if( "grants".equalsIgnoreCase( attr.getID() ) ||
            "denials".equalsIgnoreCase( attr.getID() ) )
        {
            NamingEnumeration e = attr.getAll();
            try
            {
                while( e.hasMore() )
                {
                    String value = String.valueOf( e.next() );
                    LdapDN name = ( LdapDN ) baseName.clone();
                    
                    // ou=permissions
                    name.add( "2.5.4.11=permissions" );
                    // permName=
                    name.add( "1.2.6.1.4.1.22555.1.1.1.3.201=" + value );
                    if( !next.hasEntry( name ) ) {
                        throw new SchemaViolationException( "No such permission: " + value );
                    }
                }
            }
            finally {
                e.close();
            }
        }
        
        // If the attribute is a role
        if( "roles".equalsIgnoreCase( attr.getID() ) ) {
            NamingEnumeration e = attr.getAll();
            try
            {
                while( e.hasMore() )
                {
                    String value = String.valueOf( e.next() );
                    LdapDN name = ( LdapDN ) baseName.clone();
                    
                    // ou = roles
                    name.add( "2.5.4.11=roles" );
                    
                    // roleName=
                    name.add( "1.2.6.1.4.1.22555.1.1.1.3.204=" + value );
                    if( !next.hasEntry( name ) ) {
                        throw new SchemaViolationException( "No such role: " + value );
                    }
                }
            }
            finally {
                e.close();
            }
        }
    }

    
    private void checkAttributeRemoval( Attribute attr ) throws NamingException, SchemaViolationException
    {
        if( !"objectClass".equalsIgnoreCase( attr.getID() ) )
        {
            return;
        }

        NamingEnumeration e = attr.getAll();
        try
        {
            while( e.hasMore() )
            {
                String value = String.valueOf( e.next() );
                if( "policyPermission".equalsIgnoreCase( value ) ||
                        "policyRole".equalsIgnoreCase( value ) ||
                        "policyProfile".equalsIgnoreCase( value ) )
                {
                    throw new SchemaViolationException( "Removing policy objectClasses are not allowed." );
                }
            }
        }
        finally
        {
            e.close();
        }
    }
    
    
    private static final String PERMNAME_ATTR_OID = "1.2.6.1.4.1.22555.1.1.1.3.201";
    private static final String ROLENAME_ATTR_OID = "1.2.6.1.4.1.22555.1.1.1.3.204";
    
    private void checkNotInUse( NextInterceptor next, LdapDN baseName, LdapDN name ) 
        throws NamingException, SchemaViolationException
    {
        String nameType = NamespaceTools.getRdnAttribute( name.get( name.size() - 1 ) );
        String nameValue = NamespaceTools.getRdnValue( name.get( name.size() - 1 ) );

        // Prepare a search control.
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
        ctrl.setReturningAttributes( new String[]
        {
                "roles", "grants", "denials"
        } );

        // Get an appropriate filter.
        ExprNode filter = null;
        if( PERMNAME_ATTR_OID.equals( nameType ) )
        {
            try
            {
                filter = new FilterParserImpl().parse(
                        "(|" +
                        "(grants=" + nameValue + ")" +
                        "(denials=" + nameValue + ")" +
                        ")" );
            }
            catch( Exception e )
            {
                throw ( NamingException ) new NamingException().initCause( e );
            }
        }
        else if( ROLENAME_ATTR_OID.equals( nameType ) )
        {
            try
            {
                filter = new FilterParserImpl().parse(
                        "(roles=" + nameValue + ")" );
            }
            catch( Exception e )
            {
                throw ( NamingException ) new NamingException().initCause( e );
            }
        }

        // If it is able to find an appropriate filter, 
        if( filter != null )
        {
            // execute search
            NamingEnumeration e = next.search( baseName, factoryConfiguration.getEnvironment(),
                    filter, ctrl );

            // throw an exception if search returned more than 0 usage.
            try
            {
                if( e.hasMore() )
                {
                    throw new SchemaViolationException( "Policy entry in use: " + name );
                }
            }
            finally
            {
                e.close();
            }
        }
    }

    
    private void checkModification( NextInterceptor next, LdapDN baseName, LdapDN name ) 
        throws SchemaViolationException, NamingException
    {
        if( isEntityGroup( name ) )
        {
            throw new SchemaViolationException( "Entity groups are not allowed to move: " + name );
        }
        checkNotInUse( next, baseName, name );
    }


    private static final String OU_ATTR_OID = "2.5.4.11";
    private static boolean isEntityGroup( LdapDN name )
    {
        String rn = name.get( name.size() - 1 );
        if( ! OU_ATTR_OID.equalsIgnoreCase( NamespaceTools.getRdnAttribute( rn ).trim() ) )
        {
            return false;
        }

        rn = NamespaceTools.getRdnValue( rn ).trim();

        return ( rn.equalsIgnoreCase( "permissions" ) ||
                 rn.equalsIgnoreCase( "roles" ) ||
                 rn.equalsIgnoreCase( "profiles" ) );
    }

    
    private static Attribute getObjectClass( Attributes attrs ) throws NamingException
    {
        NamingEnumeration e = attrs.getAll();
        try
        {
            while( e.hasMore() )
            {
                Attribute attr = ( Attribute ) e.next();
                if( "objectClass".equalsIgnoreCase( attr.getID() ) )
                {
                    return attr;
                }
            }
        }
        finally
        {
            e.close();
        }

        return null;
    }
}

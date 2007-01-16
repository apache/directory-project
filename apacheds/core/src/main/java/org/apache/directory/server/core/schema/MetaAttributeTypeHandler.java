/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.server.core.schema;


import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.directory.server.constants.MetaSchemaConstants;
import org.apache.directory.server.schema.bootstrap.Schema;
import org.apache.directory.server.schema.registries.AttributeTypeRegistry;
import org.apache.directory.server.schema.registries.Registries;
import org.apache.directory.shared.ldap.exception.LdapInvalidNameException;
import org.apache.directory.shared.ldap.exception.LdapOperationNotSupportedException;
import org.apache.directory.shared.ldap.message.AttributeImpl;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.util.NamespaceTools;


/**
 * A handler for operations peformed to add, delete, modify, rename and 
 * move schema normalizers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MetaAttributeTypeHandler extends AbstractSchemaChangeHandler
{
    private final SchemaPartitionDao dao;
    private final AttributeTypeRegistry attributeTypeRegistry;

    

    public MetaAttributeTypeHandler( Registries targetRegistries, PartitionSchemaLoader loader, SchemaPartitionDao dao ) 
        throws NamingException
    {
        super( targetRegistries, loader );
        
        this.dao = dao;
        this.attributeTypeRegistry = targetRegistries.getAttributeTypeRegistry();
    }


    protected void modify( LdapDN name, Attributes entry, Attributes targetEntry ) throws NamingException
    {
        String oldOid = getOid( entry );
        AttributeType at = factory.getAttributeType( targetEntry, targetRegistries );
        Schema schema = getSchema( name );
        
        if ( ! schema.isDisabled() )
        {
            attributeTypeRegistry.unregister( oldOid );
            attributeTypeRegistry.register( schema.getSchemaName(), at );
        }
    }


    public void add( LdapDN name, Attributes entry ) throws NamingException
    {
        LdapDN parentDn = ( LdapDN ) name.clone();
        parentDn.remove( parentDn.size() - 1 );
        checkNewParent( parentDn );
        
        AttributeType at = factory.getAttributeType( entry, targetRegistries );
        Schema schema = getSchema( name );
        
        if ( ! schema.isDisabled() )
        {
            attributeTypeRegistry.register( schema.getSchemaName(), at );
        }
        else
        {
            registerOids( at );
        }
    }


    public void delete( LdapDN name, Attributes entry ) throws NamingException
    {
        AttributeType at = factory.getAttributeType( entry, targetRegistries );
        Set<SearchResult> dependees = dao.listAttributeTypeDependees( at );
        if ( dependees != null && dependees.size() > 0 )
        {
            throw new LdapOperationNotSupportedException( "The attributeType with OID " + at.getOid() 
                + " cannot be deleted until all entities" 
                + " using this attributeType have also been deleted.  The following dependees exist: " 
                + getOids( dependees ), 
                ResultCodeEnum.UNWILLING_TO_PERFORM );
        }
        
        Schema schema = getSchema( name );
        
        if ( ! schema.isDisabled() )
        {
            attributeTypeRegistry.unregister( at.getOid() );
        }
        unregisterOids( at.getOid() );
    }


    public void rename( LdapDN name, Attributes entry, String newRdn ) throws NamingException
    {
        AttributeType oldAt = factory.getAttributeType( entry, targetRegistries );
        Set<SearchResult> dependees = dao.listAttributeTypeDependees( oldAt );
        if ( dependees != null && dependees.size() > 0 )
        {
            throw new LdapOperationNotSupportedException( "The attributeType with OID " + oldAt.getOid()
                + " cannot be deleted until all entities" 
                + " using this attributeType have also been deleted.  The following dependees exist: " 
                + getOids( dependees ), 
                ResultCodeEnum.UNWILLING_TO_PERFORM );
        }

        Schema schema = getSchema( name );
        Attributes targetEntry = ( Attributes ) entry.clone();
        String newOid = NamespaceTools.getRdnValue( newRdn );
        targetEntry.put( new AttributeImpl( MetaSchemaConstants.M_OID_AT, newOid ) );
        AttributeType at = factory.getAttributeType( targetEntry, targetRegistries );

        if ( ! schema.isDisabled() )
        {
            attributeTypeRegistry.unregister( oldAt.getOid() );
            attributeTypeRegistry.register( schema.getSchemaName(), at );
        }
        else
        {
            registerOids( at );
        }
        
        unregisterOids( oldAt.getOid() );
    }


    public void move( LdapDN oriChildName, LdapDN newParentName, String newRn, boolean deleteOldRn, Attributes entry ) 
        throws NamingException
    {
        checkNewParent( newParentName );
        AttributeType oldAt = factory.getAttributeType( entry, targetRegistries );
        Set<SearchResult> dependees = dao.listAttributeTypeDependees( oldAt );
        if ( dependees != null && dependees.size() > 0 )
        {
            throw new LdapOperationNotSupportedException( "The attributeType with OID " + oldAt.getOid()
                + " cannot be deleted until all entities" 
                + " using this attributeType have also been deleted.  The following dependees exist: " 
                + getOids( dependees ), 
                ResultCodeEnum.UNWILLING_TO_PERFORM );
        }

        Schema oldSchema = getSchema( oriChildName );
        Schema newSchema = getSchema( newParentName );
        Attributes targetEntry = ( Attributes ) entry.clone();
        String newOid = NamespaceTools.getRdnValue( newRn );
        targetEntry.put( new AttributeImpl( MetaSchemaConstants.M_OID_AT, newOid ) );
        AttributeType at = factory.getAttributeType( targetEntry, targetRegistries );

        if ( ! oldSchema.isDisabled() )
        {
            attributeTypeRegistry.unregister( oldAt.getOid() );
        }
        unregisterOids( oldAt.getOid() );

        if ( ! newSchema.isDisabled() )
        {
            attributeTypeRegistry.register( newSchema.getSchemaName(), at );
        }
        else
        {
            registerOids( at );
        }
    }


    public void move( LdapDN oriChildName, LdapDN newParentName, Attributes entry ) 
        throws NamingException
    {
        checkNewParent( newParentName );
        AttributeType oldAt = factory.getAttributeType( entry, targetRegistries );
        Set<SearchResult> dependees = dao.listAttributeTypeDependees( oldAt );
        if ( dependees != null && dependees.size() > 0 )
        {
            throw new LdapOperationNotSupportedException( "The attributeType with OID " + oldAt.getOid() 
                + " cannot be deleted until all entities" 
                + " using this attributeType have also been deleted.  The following dependees exist: " 
                + getOids( dependees ), 
                ResultCodeEnum.UNWILLING_TO_PERFORM );
        }

        Schema oldSchema = getSchema( oriChildName );
        Schema newSchema = getSchema( newParentName );
        
        AttributeType at = factory.getAttributeType( entry, targetRegistries );
        
        if ( ! oldSchema.isDisabled() )
        {
            attributeTypeRegistry.unregister( oldAt.getOid() );
        }
        
        if ( ! newSchema.isDisabled() )
        {
            attributeTypeRegistry.register( newSchema.getSchemaName(), at );
        }
    }
    
    
    private void checkNewParent( LdapDN newParent ) throws NamingException
    {
        if ( newParent.size() != 3 )
        {
            throw new LdapInvalidNameException( 
                "The parent dn of a attributeType should be at most 3 name components in length.", 
                ResultCodeEnum.NAMING_VIOLATION );
        }
        
        Rdn rdn = newParent.getRdn();
        if ( ! targetRegistries.getOidRegistry().getOid( rdn.getType() ).equals( OU_OID ) )
        {
            throw new LdapInvalidNameException( "The parent entry of a attributeType should be an organizationalUnit.", 
                ResultCodeEnum.NAMING_VIOLATION );
        }
        
        if ( ! ( ( String ) rdn.getValue() ).equalsIgnoreCase( "attributeTypes" ) )
        {
            throw new LdapInvalidNameException( 
                "The parent entry of a attributeType should have a relative name of ou=attributeTypes.", 
                ResultCodeEnum.NAMING_VIOLATION );
        }
    }
}
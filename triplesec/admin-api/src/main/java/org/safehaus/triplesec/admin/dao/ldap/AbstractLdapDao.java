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
package org.safehaus.triplesec.admin.dao.ldap;


import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.DataAccessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractLdapDao implements LdapDao, Constants
{
    protected final Logger log;
    protected final DirContext ctx;
    protected final String realm;
    protected final String baseUrl;
    protected final String principalName;

    
    public AbstractLdapDao( DirContext ctx ) throws DataAccessException
    {
        this.ctx = ctx;
        log = LoggerFactory.getLogger( getClass() );
        String name = null;
        String principal = null;
        String tmpRealm = null;
        try
        {
            name = ctx.getNameInNamespace();
            String principalDn = ( String ) ctx.getEnvironment().get( Context.SECURITY_PRINCIPAL );
            if ( principalDn.equalsIgnoreCase( "uid=admin,ou=system" ) )
            {
                principal = "admin";
            }
            else
            {
                principal = ( String ) new LdapDN( principalDn ).getRdn().getValue();
            }
            tmpRealm = inferRealm( name );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to get name in namespace for base context.";
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
        finally
        {
            baseUrl = name;
            principalName = principal;
            realm = tmpRealm;
        }
    }

    
    private String inferRealm( String dn ) throws NamingException
    {
        if ( dn == null || dn.equals( "" ) )
        {
            return "";
        }
        
        StringBuffer buf = new StringBuffer();
        LdapDN ldapName = new LdapDN( dn );
        for ( int ii = ldapName.size() - 1; ii >= 0 ; ii-- )
        {
            String comp = ldapName.get( ii );
            if ( NamespaceTools.getRdnAttribute( comp ).equalsIgnoreCase( DOMAIN_COMPONENT_ID ) )
            {
                buf.append( NamespaceTools.getRdnValue( comp ) );
                
                if ( ii != 0 )
                {
                    buf.append( "." );
                }
            }
        }
        
        return buf.toString();
    }
    
    
    public void deleteEntry( String rdn )
    {
        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( NamingException e )
        {
            log.error( "Failed to delete " + rdn + " under " + baseUrl, e );
        }
    }
}

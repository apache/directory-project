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


import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.DateUtils;


public class LdapUtils
{
    public static String getSingleValued( String id, Attributes attrs ) throws NamingException
    {
        if ( attrs.get( id ) == null )
        {
            return null;
        }
        
        Object obj = attrs.get( id ).get();
        if ( obj instanceof String )
        {
            return ( String ) obj;
        }
        
        try
        {
            return new String( ( byte[] ) obj, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            IllegalStateException ise = new IllegalStateException( "Encoding not supported" );
            ise.initCause( e );
            throw ise;
        }
    }
    
    
    public static String getPrincipal( String id, Attributes attrs ) throws NamingException
    {
        if ( attrs.get( id ) == null )
        {
            return null;
        }
        
        return ( String ) new LdapDN( getSingleValued( id, attrs ) ).getRdn().getValue();
    }


    public static Date getDate( String id, Attributes attrs ) throws NamingException
    {
        if ( attrs.get( id ) == null )
        {
            return null;
        }
        
        return DateUtils.getDate( getSingleValued( id, attrs ) );
    }
    
    
    public static boolean getBoolean( String id, Attributes attrs, boolean defaultValue ) throws NamingException
    {
        if ( attrs.get( id ) == null )
        {
            return defaultValue;
        }
        
        return parseBoolean ( ( ( String ) attrs.get( id ).get() ).toLowerCase() );
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

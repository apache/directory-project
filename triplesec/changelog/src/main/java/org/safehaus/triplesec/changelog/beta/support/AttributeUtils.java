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
package org.safehaus.triplesec.changelog.beta.support;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.server.core.schema.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.util.Base64;
import org.safehaus.triplesec.changelog.beta.model.StringAttribute;

public class AttributeUtils
{
    
    public static List attributesToStringAttributeList( Attributes attributes, AttributeTypeRegistry registry ) throws NamingException
    {
        List stringAttributes = new ArrayList();
        NamingEnumeration attributeEnum = attributes.getAll();

        while ( attributeEnum.hasMore() )
        {            
            StringAttribute strAttribute = attributeToStringAttribute( (Attribute) attributeEnum.next(), registry );
            stringAttributes.add( strAttribute );
        }
        return stringAttributes;
        
    }


    public static StringAttribute attributeToStringAttribute( Attribute attr, AttributeTypeRegistry registry ) throws NamingException
    {
        
        String id = ( String ) attr.getID();
        int size = attr.size();
        
        boolean isBinary = ! registry.lookup( id ).getSyntax().isHumanReadible();
        
        StringAttribute strAttribute = new StringAttribute( id );

        if ( isBinary )
        {
            for ( int ii = 0; ii < size; ii++ )
            {
                Object value = attr.get( ii );
                String encoded;
                if ( value instanceof String )
                {
                    encoded = ( String ) value;
                    try
                    {
                        encoded = new String( Base64.encode( encoded.getBytes( "UTF-8" ) ) );
                    }
                    catch ( UnsupportedEncodingException e )
                    {
                        //log.error( "Cannot convert to UTF-8: " + encoded, e );
                    }
                }
                else
                {
                    encoded = new String( Base64.encode( ( byte[] ) attr.get( ii ) ) );
                }
                strAttribute.addValue( encoded );
            }
        }
        else
        {
            for ( int ii = 0; ii < size; ii++ )
            {
                strAttribute.addValue( (String) attr.get( ii ) );
            }
        }

        return strAttribute;
        
    }


}

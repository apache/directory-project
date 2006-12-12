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

import javax.naming.directory.DirContext;

/**
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public abstract class AttributeModificationType
{
	public static final int ADD_ATTRIBUTE_MODIFICATION = DirContext.ADD_ATTRIBUTE;
	public static final int REMOVE_ATTRIBUTE_MODIFICATION = DirContext.REMOVE_ATTRIBUTE;
	public static final int REPLACE_ATTRIBUTE_MODIFICATION = DirContext.REPLACE_ATTRIBUTE;

	public static String getAttributeModificationTypeNameByIntEnum( int intType )
	{
		switch ( intType )
		{
			case ADD_ATTRIBUTE_MODIFICATION:
				return "add";
			case REMOVE_ATTRIBUTE_MODIFICATION:
				return "remove";
			case REPLACE_ATTRIBUTE_MODIFICATION:
				return "replace";
			default:
                throw new IllegalArgumentException( "Unmatched Attribute Modification Type: " + intType );
        }
	}
    
	public static int getIntEnumAttributeModicafitionTypeByName( String name )
    {
        if ( name.equals( "add" ) )
        {
            return ADD_ATTRIBUTE_MODIFICATION;
        }
        else if ( name.equals( "remove" ) )
        {
            return REMOVE_ATTRIBUTE_MODIFICATION;
        }
        else if ( name.equals( "replace" ) )
        {
            return REPLACE_ATTRIBUTE_MODIFICATION;
        }
        else
        {
            throw new IllegalArgumentException( "Unmatched Attribute Modification Type Name: " + name );
        }
    }
    
    private AttributeModificationType()
    {
    }
}

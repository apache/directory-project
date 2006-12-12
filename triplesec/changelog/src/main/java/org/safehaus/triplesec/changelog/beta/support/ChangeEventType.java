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

/**
 * An enumaration type for representing the actual type of a 
 * {@link org.safehaus.sandbox.triplesec.change.ChangeOperation}.
 * 
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public abstract class ChangeEventType
{    
    /**
     * Static Enumeration Members
     */
    public static final int ADD_CHANGE_EVENT    = 0;
    public static final int DELETE_CHANGE_EVENT = 1;
    public static final int MODIFY_CHANGE_EVENT = 2;
    public static final int MODRDN_CHANGE_EVENT = 3;
    public static final int MODDN_CHANGE_EVENT  = 4;
    
    public static String getChangeEventTypeNameByIntEnum( int intType )
    {
        switch ( intType )
        {
            case ADD_CHANGE_EVENT:
                return "add";
            case DELETE_CHANGE_EVENT:
                return "delete";
            case MODIFY_CHANGE_EVENT:
                return "modify";
            case MODRDN_CHANGE_EVENT:
                return "modrdn";
            case MODDN_CHANGE_EVENT:
                return "moddn";
            default:
                throw new IllegalArgumentException( "Unmatched Change Event Type: " + intType );
        }
    }
    
        
    public static int getIntEnumChangeEventTypeByName( String name )
    {
        
        if ( name.equals( "add" ) )
        {
            return ADD_CHANGE_EVENT;
        }
        else if ( name.equals( "delete" ) )
        {
            return DELETE_CHANGE_EVENT;
        }
        else if ( name.equals( "modify" ) )
        {
            return MODIFY_CHANGE_EVENT;
        }
        else if ( name.equals( "modrdn" ) )
        {
            return MODRDN_CHANGE_EVENT;
        }
        else if ( name.equals( "moddn" ) )
        {
            return MODDN_CHANGE_EVENT;
        }
        else
        {
            throw new IllegalArgumentException( "Unmatched Change Event Type Name: " + name );
        }
    }
    
    private ChangeEventType()
    {
    }
}

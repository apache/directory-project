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


import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;


public class SingleValuedField
{
    private final String id;    
    private final String initial;
    private String current;
    
    
    public SingleValuedField( String id, String initial )
    {
        this.id = id;
        this.initial = initial;
        this.current = initial;
    }
    
    
    public String getInitialValue()
    {
        return initial;
    }


    public String getCurrentValue()
    {
        return current;
    }


    public boolean isUpdateNeeded()
    {
        if ( initial != null )
        {
            return ! initial.equals( current );
        }
        
        return current != null;
    }
    
    
    public boolean setValue( String value )
    {
        if ( current != null && current.equals( value ) )
        {
            return false;
        }
        
        // ignore replacing null with emtpy string which causes unnecesary noise
        if ( current == null && value != null && value.length() == 0 )
        {
            return false;
        }
        
        current = value;
        return true;
    }
    
    
    public ModificationItem getModificationItem()
    {
        if ( initial == current )
        {
            return null;
        }
        
        if ( initial == null && current != null )
        {
            return new ModificationItem( DirContext.ADD_ATTRIBUTE, new BasicAttribute( id, current ) );
        }
        
        if ( initial != null && current == null )
        {
            return new ModificationItem( DirContext.REMOVE_ATTRIBUTE, new BasicAttribute( id ) );
        }
        
        if ( ! initial.equals( current ) ) 
        {
            return new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute( id, current ) );
        }
        
        return null;
    }
}

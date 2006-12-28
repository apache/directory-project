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


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;


public class MultiValuedField<T>
{
    private final String id;
    private final Set<T> initial;
    private Set<T> added;
    private Set<T> deleted;
    private Set<T> current;

    
    public MultiValuedField( String id, Set<T> initial )
    {
        this.id = id;
        this.initial = Collections.unmodifiableSet( new HashSet<T>( initial ) );
        this.current = new HashSet<T>( initial );
        this.deleted = new HashSet<T>();
        this.added = new HashSet<T>();
    }


    public boolean addValue( T value )
    {
        // if we have the value then exit and return false
        if ( current.contains( value ) )
        {
            return false;
        }

        // if the value is not present but was deleted before then undelete it
        if ( deleted.contains( value ) )
        {
            deleted.remove( value );
        }
        // if value was not deleted before then add to added
        else
        {
            added.add( value );
        }

        return current.add( value );
    }


    public boolean removeValue( T value )
    {
        // if we don't have the value then return false
        if ( ! current.contains( value ) )
        {
            return false;
        }
        
        // if the value was added before then we unadd it 
        if ( added.contains( value ) )
        {
            added.remove( value );
        }
        // if the value was not added before then we delete it
        else
        {
            deleted.add( value );
        }
        
        return current.remove( value );
    }
    
    
    public boolean isUpdateNeeded()
    {
        return added.size() + deleted.size() > 0;
    }
    
    
    public Set<T> getInitialValues()
    {
        return initial;
    }
    
    
    public Set<T> getCurrentValues()
    {
        return Collections.unmodifiableSet( current );
    }
    
    
    public ModificationItem getModificationItem()
    {
        if ( ! isUpdateNeeded() )
        {
            return null;
        }
        
        BasicAttribute attr = new BasicAttribute( id );
        if ( added.size() == 0 && deleted.size() > 0 )
        {
            for (T aDeleted : deleted) {
                attr.add(aDeleted);
            }
            return new ModificationItem( DirContext.REMOVE_ATTRIBUTE, attr );
        }
        
        if ( added.size() > 0 && deleted.size() == 0 )
        {
            for (T anAdded : added) {
                attr.add(anAdded);
            }
            return new ModificationItem( DirContext.ADD_ATTRIBUTE, attr );
        }

        for (T aCurrent : current) {
            attr.add(aCurrent);
        }
        return new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr );
    }
}

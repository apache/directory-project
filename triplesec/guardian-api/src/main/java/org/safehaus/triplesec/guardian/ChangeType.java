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
package org.safehaus.triplesec.guardian;


/**
 * The enumerated type for changes to guardian entities.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$, $Date$
 */
public class ChangeType
{
    /** the change type representing the addition of a guardian entity */
    public static final ChangeType ADD = new ChangeType( "Add", 1 );
    /** the change type representing the deletion of a guardian entity */
    public static final ChangeType DEL = new ChangeType( "Delete", 2 );
    /** the change type representing the modification of a guardian entity */
    public static final ChangeType MODIFY = new ChangeType( "Modify", 4 );
    /** the change type representing the name change of a guardian entity */
    public static final ChangeType RENAME = new ChangeType( "Rename", 8 );

    /** the name of this ChangeType enumeration value */
    private final String name;
    /** the value of this ChangeType enumeration value */
    private final int value;
    

    /**
     * Create a change type enumeration.
     * 
     * @param name the name of this ChangeType enumeration value
     * @param value the value of this ChangeType enumeration value
     */
    private ChangeType( String name, int value )
    {
        this.name = name;
        this.value = value;
    }
    
    
    /**
     * Gets the name of this ChangeType enumeration value.
     * 
     * @return the name of this ChangeType enumeration value
     */
    public String getName()
    {
        return name;
    }
    
    
    /**
     * Gets the primitive int value of this ChangeType enumeration value.
     *
     * @return the primitive int value of this ChangeType enumeration value
     */
    public int getValue()
    {
        return value;
    }
}

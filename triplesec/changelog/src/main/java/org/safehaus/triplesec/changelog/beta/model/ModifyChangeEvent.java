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
package org.safehaus.triplesec.changelog.beta.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.safehaus.triplesec.changelog.beta.support.AttributeModificationType;
import org.safehaus.triplesec.changelog.beta.support.ChangeEventType;

/**
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public class ModifyChangeEvent extends BaseChangeEvent
{
    private List addedAttributes = new ArrayList();
    private List removedAttributes = new ArrayList();
    private List replacedAttributes = new ArrayList();
    private Map modifications = new HashMap();
    
    /** Refecence types used for as keys for the attribute modification map */
    private static Integer ADD_ATTRIBUTE_MODIFICATION_OBJECT = new Integer( AttributeModificationType.ADD_ATTRIBUTE_MODIFICATION );
    private static Integer REMOVE_ATTRIBUTE_MODIFICATION_OBJECT = new Integer( AttributeModificationType.REMOVE_ATTRIBUTE_MODIFICATION );
    private static Integer REPLACE_ATTRIBUTE_MODIFICATION_OBJECT = new Integer( AttributeModificationType.REPLACE_ATTRIBUTE_MODIFICATION );

    public ModifyChangeEvent( int id, String affectedEntryName, String changeEventPrincipal, Date changeEventTime )
    {
        super( id, ChangeEventType.MODIFY_CHANGE_EVENT, affectedEntryName, changeEventPrincipal, changeEventTime );
        
        modifications.put( ADD_ATTRIBUTE_MODIFICATION_OBJECT, addedAttributes );
        modifications.put( REMOVE_ATTRIBUTE_MODIFICATION_OBJECT, removedAttributes );
        modifications.put( REPLACE_ATTRIBUTE_MODIFICATION_OBJECT, replacedAttributes );
    }
    
    public void addModificationAttribute( int modType, StringAttribute attribute )
    {
        switch ( modType )
        {
            case AttributeModificationType.ADD_ATTRIBUTE_MODIFICATION:
                addedAttributes.add( attribute );
                break;
            case AttributeModificationType.REMOVE_ATTRIBUTE_MODIFICATION:
                removedAttributes.add( attribute );
                break;
            case AttributeModificationType.REPLACE_ATTRIBUTE_MODIFICATION:
                replacedAttributes.add( attribute );
                break;
            default:
                throw new IllegalArgumentException( "Unmatched Attribute Modification Type: " + modType );
        }
    }
    
    public void addModificationAttributes( int modType, List stringAttributes )
    {
        Iterator it = stringAttributes.iterator();
        while ( it.hasNext() )
        {
            addModificationAttribute( modType, (StringAttribute) it.next() );
        }
    }
    
    public List getAddedAttributes()
    {
        return addedAttributes;
    }
    
    public List getRemovedAttributes()
    {
        return removedAttributes;
    }
    
    public List getReplacedAttributes()
    {
        return replacedAttributes;
    }
    
    public boolean anyAddedAttributeExists()
    {
        return addedAttributes.size() > 0;
    }
    
    public boolean anyRemovedAttributeExists()
    {
        return removedAttributes.size() > 0;
    }
    
    public boolean anyReplacedAttributeExists()
    {
        return replacedAttributes.size() > 0;
    }

    public String getEventMessage()
    {
        StringBuffer buffer = new StringBuffer();
        
        if ( anyAddedAttributeExists() )
        {
            buffer.append( "added:: " );
            buffer.append( getAddedAttributes().toString() );
            buffer.append( "\n" );
        }
        if ( anyRemovedAttributeExists() )
        {
            buffer.append( "removed:: " );
            buffer.append( getRemovedAttributes().toString() );
            buffer.append( "\n" );
        }
        if ( anyReplacedAttributeExists() )
        {
            buffer.append( "replaced:: " );
            buffer.append( getReplacedAttributes().toString() );
            buffer.append( "\n" );
        }
        
        return buffer.toString();
    }
    
}

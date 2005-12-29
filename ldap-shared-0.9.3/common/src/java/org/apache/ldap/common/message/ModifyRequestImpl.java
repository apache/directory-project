/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.common.message ;


import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Collections ;
import java.util.Iterator;

import javax.naming.directory.ModificationItem ;


/**
 * Lockable ModifyRequest implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ModifyRequestImpl
    extends AbstractRequest implements ModifyRequest
{
    static final long serialVersionUID = -505803669028990304L;
    /** Dn of the entry to modify or PDU's <b>object</b> field */
    private String name ;
    /** Sequence of modifications or PDU's <b>modification</b> seqence field */
    private ArrayList mods = new ArrayList() ;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a Lockable ModifyRequest implementing object used to modify the
     * attributes of an entry.
     *
     * @param id the sequential message identifier
     */
    public ModifyRequestImpl( final int id )
    {
        super( id, TYPE, true ) ;
    }


    // ------------------------------------------------------------------------
    // ModifyRequest Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets an immutable Collection of modification items representing the
     * atomic changes to perform on the candidate entry to modify.
     *
     * @return an immutatble Collection of ModificationItem instances.
     * @see <{javax.naming.directory.ModificationItem}>
     */
    public Collection getModificationItems()
    {
        return Collections.unmodifiableCollection( mods ) ;
    }


    /**
     * Gets the distinguished name of the entry to be modified by this request.
     * This property represents the PDU's <b>object</b> field.
     *
     * @return the DN of the modified entry.
     */
    public String getName()
    {
        return name ;
    }


    /**
     * Sets the distinguished name of the entry to be modified by this request.
     * This property represents the PDU's <b>object</b> field.
     *
     * @param name the DN of the modified entry.
     */
    public void setName( String name )
    {
        lockCheck( "Attempt to alter object name of locked ModifyRequest!" ) ;
        this.name = name ;
    }


    /**
     * Adds a ModificationItem to the set of modifications composing this modify
     * request.
     *
     * @param mod a ModificationItem to add.
     */
    public void addModification( ModificationItem mod )
    {
        lockCheck( "Attempt to add modification to locked ModifyRequest!" ) ;
        mods.add( mod ) ;
    }


    /**
     * Removes a ModificationItem to the set of modifications composing this
     * modify request.
     *
     * @param mod a ModificationItem to remove.
     */
    public void removeModification( ModificationItem mod )
    {
        lockCheck( "Attempt to remove modification to locked ModifyRequest!" ) ;
        mods.remove( mod ) ;
    }


    // ------------------------------------------------------------------------
    // SingleReplyRequest Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the protocol response message type for this request which produces
     * at least one response.
     *
     * @return the message type of the response.
     */
    public MessageTypeEnum getResponseType()
    {
        return RESP_TYPE ;
    }


    /**
     * Checks to see if ModifyRequest stub equals another by factoring in checks
     * for the name and modification items of the request.
     *
     * @param obj the object to compare this ModifyRequest to
     * @return true if obj equals this ModifyRequest, false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( ! super.equals( obj ) )
        {
            return false;
        }

        ModifyRequest req = ( ModifyRequest ) obj;

        if ( name != null && req.getName() == null )
        {
            return false;
        }

        if ( name == null && req.getName() != null )
        {
            return false;
        }

        if ( name != null && req.getName() != null )
        {
            if ( ! name.equals( req.getName() ) )
            {
                return false;
            }
        }

        if ( req.getModificationItems().size() != mods.size() )
        {
            return false;
        }

        Iterator list = req.getModificationItems().iterator();
        for ( int ii = 0; ii < mods.size(); ii++ )
        {
            ModificationItem item = ( ModificationItem ) list.next();
            if ( ! equals( ( ModificationItem ) mods.get( ii ), item ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Checks to see if two ModificationItems are equal by factoring in the
     * modification operation as well as the attribute of each item.
     *
     * @param item0 the first ModificationItem to compare
     * @param item1 the second ModificationItem to compare
     * @return true if the ModificationItems are equal, false otherwise
     */
    private boolean equals( ModificationItem item0, ModificationItem item1 )
    {
        if ( item0 == item1 )
        {
            return true;
        }

        if ( item0.getModificationOp() != item1.getModificationOp() )
        {
            return false;
        }

        // compare attribute id's at the very least
        if ( ! item0.getAttribute().getID().equals(
                item1.getAttribute().getID() ) )
        {
            return false;
        }

        // looks like we have another ordering issue - this time the order of
        // attribute values does not match.  Until this is resolved we'll
        // comment it out - NOT A GOOD PRACTICE AT ALL!!!!!
//        if ( ! item0.getAttribute().equals( item1.getAttribute() ) )
//        {
//            return false;
//        }

        return true;
    }
}

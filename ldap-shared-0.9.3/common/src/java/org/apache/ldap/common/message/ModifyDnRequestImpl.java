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


/**
 * Lockable ModifyDNRequest implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ModifyDnRequestImpl
    extends AbstractRequest implements ModifyDnRequest
{
    static final long serialVersionUID = 1233507339633051696L;
    /** PDU's modify Dn candidate <b>entry</b> distinguished name property */
    private String m_name ;
    /** PDU's <b>newrdn</b> relative distinguished name property */
    private String m_newRdn ;
    /** PDU's <b>newSuperior</b> distinguished name property */
    private String m_newSuperior ;
    /** PDU's <b>deleteOldRdn</b> flag */
    private boolean m_deleteOldRdn = false ;


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------


    /**
     * Creates a Lockable ModifyDnRequest implementing object used to perform
     * a dn change on an entry potentially resulting in an entry move.
     *
     * @param id the seq id of this message
     */
    public ModifyDnRequestImpl( final int id )
    {
        super( id, TYPE, true ) ;
    }


    // -----------------------------------------------------------------------
    // ModifyDnRequest Interface Method Implementations
    // -----------------------------------------------------------------------


    /**
     * Gets the flag which determines if the old Rdn attribute is to be removed
     * from the entry when the new Rdn is used in its stead.  This property
     * corresponds to the <b>deleteoldrdn</p> PDU field.
     *
     * @return true if the old rdn is to be deleted, false if it is not
     */
    public boolean getDeleteOldRdn()
    {
        return m_deleteOldRdn ;
    }


    /**
     * Sets the flag which determines if the old Rdn attribute is to be removed
     * from the entry when the new Rdn is used in its stead.  This property
     * corresponds to the <b>deleteoldrdn</p> PDU field.
     *
     * @param deleteOldRdn true if the old rdn is to be deleted, false if it
     * is not
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
        lockCheck(
            "Attempt to toggle deleteOldRdn flag of locked ModifyDnRequest!" ) ;
        m_deleteOldRdn = deleteOldRdn ;
    }


    /**
     * Gets whether or not this request is a DN change resulting in a move
     * operation.  Setting the newSuperior property to a non-null name, toggles
     * this flag.
     *
     * @return true if the newSuperior property is <b>NOT</b> null, false
     * otherwise.
     */
    public boolean isMove()
    {
        return m_newSuperior != null ;
    }


    /**
     * Gets the entry's distinguished name representing the <b>entry</b> PDU
     * field.
     *
     * @return the distinguished name of the entry.
     */
    public String getName()
    {
        return m_name ;
    }


    /**
     * Sets the entry's distinguished name representing the <b>entry</b> PDU
     * field.
     *
     * @param name the distinguished name of the entry.
     */
    public void setName( String name )
    {
        lockCheck( "Attempt to alter entry name on locked ModifyDnRequest!" ) ;
        m_name = name ;
    }


    /**
     * Gets the new relative distinguished name for the entry which represents
     * the PDU's <b>newrdn</b> field.
     *
     * @return the relative dn with one component
     */
    public String getNewRdn()
    {
        return m_newRdn ;
    }


    /**
     * Sets the new relative distinguished name for the entry which represents
     * the PDU's <b>newrdn</b> field.
     *
     * @param newRdn the relative dn with one component
     */
    public void setNewRdn( String newRdn )
    {
        lockCheck( "Atttempt to alter the newRdn of locked ModifyDnRequest!" ) ;
        m_newRdn = newRdn ;
    }


    /**
     * Gets the optional distinguished name of the new superior entry where the
     * candidate entry is to be moved.  This property corresponds to the PDU's
     * <b>newSuperior</b> field.  May be null representing a simple Rdn change
     * rather than a move operation.
     *
     * @return the dn of the superior entry the candidate entry is moved under.
     */
    public String getNewSuperior()
    {
        return m_newSuperior ;
    }


    /**
     * Sets the optional distinguished name of the new superior entry where the
     * candidate entry is to be moved.  This property corresponds to the PDU's
     * <b>newSuperior</b> field.  May be null representing a simple Rdn change
     * rather than a move operation.  Setting this property to a non-null value
     * toggles the move flag obtained via the <code>isMove</code> method.
     *
     * @param newSuperior the dn of the superior entry the candidate entry
     * for DN modification is moved under.
     */
    public void setNewSuperior( String newSuperior )
    {
        m_newSuperior = newSuperior ;
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
     * Checks to see of an object equals this ModifyDnRequest stub.  The
     * equality presumes all ModifyDnRequest specific properties are the same.
     *
     * @param obj the object to compare with this stub
     * @return true if the obj is equal to this stub, false otherwise
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

        ModifyDnRequest req = ( ModifyDnRequest ) obj;

        if ( m_name != null && req.getName() == null )
        {
            return false;
        }

        if ( m_name == null && req.getName() != null )
        {
            return false;
        }

        if ( m_name != null && req.getName() != null )
        {
            if ( ! m_name.equals( req.getName() ) )
            {
                return false;
            }
        }

        if ( m_deleteOldRdn != req.getDeleteOldRdn() )
        {
            return false;
        }

        if ( m_newRdn != null && req.getNewRdn() == null )
        {
            return false;
        }

        if ( m_newRdn == null && req.getNewRdn() != null )
        {
            return false;
        }

        if ( m_newRdn != null && req.getNewRdn() != null )
        {
            if ( ! m_newRdn.equals( req.getNewRdn() ) )
            {
                return false;
            }
        }

        if ( m_newSuperior != null && req.getNewSuperior() == null )
        {
            return false;
        }

        if ( m_newSuperior == null && req.getNewSuperior() != null )
        {
            return false;
        }

        if ( m_newSuperior != null && req.getNewSuperior() != null )
        {
            if ( ! m_newSuperior.equals( req.getNewSuperior() ) )
            {
                return false;
            }
        }

        return true;
    }
}

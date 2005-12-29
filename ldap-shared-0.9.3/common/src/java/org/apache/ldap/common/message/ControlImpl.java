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


import org.apache.ldap.common.Lockable ;
import org.apache.ldap.common.AbstractLockable ;


/**
 * Lockable Control implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class ControlImpl extends AbstractLockable implements Control
{
    /** Unique object identifier for this control */
    private String m_oid ;
    /** Control ASN.1 encoded parameters */
    private byte [] m_value ;
    /** Flag for control criticality */
    private boolean m_isCritical ;
    private String id;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a non-root Lockable Control implementation whose state is
     * overriden by a parent Lockable.
     *
     * @param a_parent the overriding parent Lockable.
     */
    public ControlImpl( final Lockable a_parent )
    {
        super( a_parent, false ) ;
    }


    /**
     * Creates a non-root Lockable Control implementation whose state is
     * overriden by a parent Lockable.
     */
    public ControlImpl()
    {
        super() ;
    }


    // ------------------------------------------------------------------------
    // Control Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Determines whether or not this control is critical for the correct
     * operation of a request or response message.  The default for this value
     * should be false.
     *
     * @return true if the control is critical false otherwise.
     */
    public boolean isCritical()
    {
        return m_isCritical ;
    }


    /**
     * Sets the criticil flag which determines whether or not this control is
     * critical for the correct operation of a request or response message.  The
     * default for this value should be false.
     *
     * @param a_isCritical true if the control is critical false otherwise.
     */
    public void setCritical( boolean a_isCritical )
    {
        lockCheck( "Attempt to alter criticality flag of locked Control!" ) ;
        m_isCritical = a_isCritical ;
    }


    /**
     * Gets the OID of the Control to identify the control type.
     *
     * @return the OID of this Control.
     */
    public String getType()
    {
        return m_oid ;
    }


    /**
     * Sets the OID of the Control to identify the control type.
     *
     * @param a_oid the OID of this Control.
     */
    public void setType( String a_oid )
    {
        lockCheck( "Attempt to alter OID of locked Control!" ) ;
        m_oid = a_oid ;
    }


    /**
     * Gets the ASN.1 BER encoded value of the control which would have its own
     * custom ASN.1 defined structure based on the nature of the control.
     *
     * @return ASN.1 BER encoded value as binary data.
     */
    public byte [] getValue()
    {
        return m_value ;
    }


    /**
     * Sets the ASN.1 BER encoded value of the control which would have its own
     * custom ASN.1 defined structure based on the nature of the control.
     *
     * @param a_value ASN.1 BER encoded value as binary data.
     */
    public void setValue( byte [] a_value )
    {
        lockCheck( "Attempt to alter encoded values of locked Control!" ) ;
        m_value = a_value ;
    }


    /**
      * Retrieves the object identifier assigned for the LDAP control.
      *
      * @return The non-null object identifier string.
      */
    public String getID()
    {
        return id;
    }
}

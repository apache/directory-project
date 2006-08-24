/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.asn1new.ldap.pojo;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.tlv.Length;
import org.apache.asn1new.ber.tlv.UniversalTag;
import org.apache.asn1new.ber.tlv.Value;
import org.apache.asn1new.primitives.OctetString;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.apache.asn1new.ldap.codec.primitives.LdapDN;
import org.apache.asn1new.ldap.codec.primitives.LdapString;


/**
 * A CompareRequest Message. Its syntax is :
 * CompareRequest ::= [APPLICATION 14] SEQUENCE {
 *              entry           LDAPDN,
 *              ava             AttributeValueAssertion }
 * 
 * AttributeValueAssertion ::= SEQUENCE {
 *              attributeDesc   AttributeDescription,
 *              assertionValue  AssertionValue }
 * 
 * AttributeDescription ::= LDAPString
 * 
 * AssertionValue ::= OCTET STRING
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareRequest extends LdapMessage
{
    //~ Instance fields ----------------------------------------------------------------------------

    /** The entry to be compared */
    private LdapDN entry;

    /** The attribute to be compared */
    private LdapString attributeDesc;

    /** The value to be compared */
    private OctetString assertionValue;
    
    /** The compare request length */
    private transient int compareRequestLength;
    
    /** The attribute value assertion length */
    private transient int avaLength;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new CompareRequest object.
     */
    public CompareRequest()
    {
        super( );
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the message type
     *
     * @return Returns the type.
     */
    public int getMessageType()
    {
        return LdapConstants.COMPARE_REQUEST;
    }

    /**
     * Get the entry to be compared
     *
     * @return Returns the entry.
     */
    public String getEntry()
    {
        return ( ( entry == null ) ? "" : entry.getString() );
    }

    /**
     * Set the entry to be compared
     *
     * @param entry The entry to set.
     */
    public void setEntry( LdapDN entry )
    {
        this.entry = entry;
    }

    /**
     * Get the assertion value
     *
     * @return Returns the assertionValue.
     */
    public OctetString getAssertionValue()
    {
        return assertionValue;
    }

    /**
     * Set the assertion value
     *
     * @param assertionValue The assertionValue to set.
     */
    public void setAssertionValue( OctetString assertionValue )
    {
        this.assertionValue = assertionValue;
    }

    /**
     * Get the attribute description
     *
     * @return Returns the attributeDesc.
     */
    public String getAttributeDesc()
    {
        return ( ( attributeDesc == null ) ? "" : attributeDesc.getString() );
    }

    /**
     * Set the attribute description
     *
     * @param attributeDesc The attributeDesc to set.
     */
    public void setAttributeDesc( LdapString attributeDesc )
    {
        this.attributeDesc = attributeDesc;
    }

    /**
     * Compute the CompareRequest length
     * 
     * CompareRequest :
     * 
     * 0x6E L1 
     *  |
     *  +--> 0x04 L2 entry
     *  +--> 0x30 L3 (ava)
     *        |
     *        +--> 0x04 L4 attributeDesc
     *        +--> 0x04 L5 assertionValue
     * 
     * L3 = Length(0x04) + Length(L4) + L4
     *      + Length(0x04) + Length(L5) + L5
     * 
     * Length(CompareRequest) = Length(0x6E) + Length(L1) + L1
     *                          + Length(0x04) + Length(L2) + L2
     *                          + Length(0x30) + Length(L3) + L3
     * @return DOCUMENT ME!
    */
    public int computeLength()
    {

        // The entry
        compareRequestLength = 1 + Length.getNbBytes( entry.getNbBytes() ) + entry.getNbBytes();

        // The attribute value assertion
        avaLength =
            1 + Length.getNbBytes( attributeDesc.getNbBytes() ) + attributeDesc.getNbBytes() +
            1 + Length.getNbBytes( assertionValue.getNbBytes() ) + assertionValue.getNbBytes();

        compareRequestLength += 1 + Length.getNbBytes( avaLength ) + avaLength;

        return 1 + Length.getNbBytes( compareRequestLength ) + compareRequestLength;
    }

    /**
     * Encode the CompareRequest message to a PDU.
     * 
     * CompareRequest :
     * 
     * 0x6E LL
     *   0x04 LL entry
     *   0x30 LL attributeValueAssertion
     *     0x04 LL attributeDesc
     *     0x04 LL assertionValue
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( "Cannot put a PDU in a null buffer !" );
        }

        try 
        {
            // The CompareRequest Tag
            buffer.put( LdapConstants.COMPARE_REQUEST_TAG );
            buffer.put( Length.getBytes( compareRequestLength ) ) ;
            
            // The entry
            Value.encode( buffer, entry.getBytes() );

            // The attributeValueAssertion sequence Tag
            buffer.put( UniversalTag.SEQUENCE_TAG );
            buffer.put( Length.getBytes( avaLength ) ) ;
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException("The PDU buffer size is too small !"); 
        }

        // The attributeDesc
        Value.encode( buffer, attributeDesc.getString() );
        
        // The assertionValue
        Value.encode( buffer, assertionValue );

        return buffer;
    }

    /**
     * Get a String representation of a Compare Request
     *
     * @return A Compare Request String 
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        sb.append( "    Compare request\n" );
        sb.append( "        Entry : '" ).append( entry.toString() ).append( "'\n" );
        sb.append( "        Attribute description : '" ).append( attributeDesc.toString() ).append(
            "'\n" );
        sb.append( "        Attribute value : '" ).append( assertionValue.toString() ).append(
            "'\n" );

        return sb.toString();
    }
}
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

import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.tlv.Length;
import org.apache.asn1new.ber.tlv.UniversalTag;
import org.apache.asn1new.ber.tlv.Value;
import org.apache.asn1new.primitives.OctetString;
import org.apache.asn1new.util.StringUtils;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.apache.asn1new.ldap.codec.primitives.LdapDN;
import org.apache.asn1new.ldap.codec.primitives.LdapString;

import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;


/**
 * A SearchResultEntry Message. Its syntax is :
 *   SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
 *       objectName      LDAPDN,
 *       attributes      PartialAttributeList }
 * 
 *   PartialAttributeList ::= SEQUENCE OF SEQUENCE {
 *       type    AttributeDescription,
 *       vals    SET OF AttributeValue }
 * 
 *   AttributeDescription ::= LDAPString
 * 
 *   AttributeValue ::= OCTET STRING
 * 
 * It contains an entry, with all its attributes, and all the attributes
 * values. If a search request is submited, all the results are sent one
 * by one, followed by a searchResultDone message.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEntry extends LdapMessage
{
    //~ Instance fields ----------------------------------------------------------------------------

    /** The DN of the returned entry */
    private LdapDN objectName;

    /** The attributes list. It contains javax.naming.directory.Attribute */
    private Attributes partialAttributeList;

    /** The current attribute being decoded */
    private transient Attribute currentAttributeValue;
    
    /** The search result entry length */
    private transient int searchResultEntryLength;
    
    /** The partial attributes length */
    private transient int attributesLength;
    
    /** The list of all attributes length */
    private transient List attributeLength;
    
    /** The list of all vals length */
    private transient List valsLength;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SearchResultEntry object.
     */
    public SearchResultEntry()
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
        return LdapConstants.SEARCH_RESULT_ENTRY;
    }

    /**
     * Get the entry DN
     *
     * @return Returns the objectName.
     */
    public String getObjectName()
    {
        return ( ( objectName == null ) ? null : objectName.getString() );
    }

    /**
     * Set the entry DN
     *
     * @param objectName The objectName to set.
     */
    public void setObjectName( LdapDN objectName )
    {
        this.objectName = objectName;
    }

    /**
     * Get the entry's attributes
     *
     * @return Returns the partialAttributeList.
     */
    public Attributes getPartialAttributeList()
    {
        return partialAttributeList;
    }

    /**
     * Initialize the partial Attribute list.
     *
     */
    public void setPartialAttributeList(Attributes partialAttributeList)
    {

        this.partialAttributeList = (Attributes)partialAttributeList;
    }

    /**
     * Initialize the partial Attribute list if needed, otherwise add the current
     * Attribute Value to the list.
     *
     */
    public void addPartialAttributeList()
    {

        if ( currentAttributeValue == null )
        {
            partialAttributeList = new BasicAttributes( true );
        }
    }

    /**
     * Create a new attributeValue
     * @param type The attribute's name
     */
    public void addAttributeValues( LdapString type )
    {
        currentAttributeValue = new BasicAttribute( StringUtils.lowerCase( type.getString() ) );

        partialAttributeList.put( currentAttributeValue );
    }

    /**
     * Add a new value to the current attribute
     * @param value
     */
    public void addAttributeValue( OctetString value )
    {
        currentAttributeValue.add( value );
    }

    /**
     * Compute the SearchResultEntry length
     * 
     * SearchResultEntry :
     * 
     * 0x64 L1
     *  |
     *  +--> 0x04 L2 objectName
     *  +--> 0x30 L3 (attributes)
     *        |
     *        +--> 0x30 L4-1 (partial attributes list)
     *        |     |
     *        |     +--> 0x04 L5-1 type
     *        |     +--> 0x31 L6-1 (values)
     *        |           |
     *        |           +--> 0x04 L7-1-1 value
     *        |           +--> ...
     *        |           +--> 0x04 L7-1-n value
     *        |
     *        +--> 0x30 L4-2 (partial attributes list)
     *        |     |
     *        |     +--> 0x04 L5-2 type
     *        |     +--> 0x31 L6-2 (values)
     *        |           |
     *        |           +--> 0x04 L7-2-1 value
     *        |           +--> ...
     *        |           +--> 0x04 L7-2-n value
     *        |
     *        +--> ...
     *        |
     *        +--> 0x30 L4-m (partial attributes list)
     *              |
     *              +--> 0x04 L5-m type
     *              +--> 0x31 L6-m (values)
     *                    |
     *                    +--> 0x04 L7-m-1 value
     *                    +--> ...
     *                    +--> 0x04 L7-m-n value
     * 
     */
    public int computeLength()
    {
        // The entry
        searchResultEntryLength = 1 + Length.getNbBytes( objectName.getNbBytes() ) + objectName.getNbBytes();
        
        // The attributes sequence
        attributesLength = 0;
        
        if ( ( partialAttributeList != null ) && ( partialAttributeList.size() != 0 ) )
        {
            NamingEnumeration attributes = partialAttributeList.getAll();
            attributeLength = new LinkedList();
            valsLength = new LinkedList();
            
            // Compute the attributes length
            while ( attributes.hasMoreElements() )
            {
                Attribute attribute = (Attribute)attributes.nextElement();
                int localAttributeLength = 0;
                int localValuesLength = 0;
                
                // Get the type length
                int idLength = attribute.getID().getBytes().length;
                localAttributeLength = 1 + Length.getNbBytes( idLength ) + idLength;
                
                // The values
                try
                {
	                NamingEnumeration values = attribute.getAll();
	                
	                if ( values.hasMoreElements() )
	                {
                        localValuesLength = 0;
	                    
		                while ( values.hasMoreElements() )
		                {
		                    Object value = (Object)values.next();
		                    
		                    if ( value instanceof String )
		                    {
		                    	String stringValue = (String)value;
                                
                                try
                                {
                                    int stringLength = stringValue.getBytes( "UTF-8" ).length;
                                    localValuesLength += 1 + Length.getNbBytes( stringLength ) + stringLength;
                                }
                                catch ( UnsupportedEncodingException uee )
                                {
                                    // Should not be possible. The encoding of the Attribute value
                                    // will check that this value is valid, and if not, it will throw 
                                    // an exception.
                                    // The allocated length will be set to a null length value
                                    // in order to avoid an exception thrown while encoding the 
                                    // Attribute value. 
                                    localValuesLength += 1 + 1;
                                }
		                    }
		                    else if ( value instanceof OctetString )
		                    {
		                    	OctetString octetStringValue = (OctetString)value;
	                            localValuesLength += 1 + Length.getNbBytes( octetStringValue.getNbBytes() ) + octetStringValue.getNbBytes();
		                    }
		                    else
		                    {
		                    	byte[] binaryValue = (byte[])value;
	                            localValuesLength += 1 + Length.getNbBytes( binaryValue.length ) + binaryValue.length;
		                    }
		                    
		                }

                        localAttributeLength += 1 + Length.getNbBytes( localValuesLength ) + localValuesLength; 
	                }
	                
                }
                catch (NamingException ne)
                {
                    return 0;
                }
                
                // add the attribute length to the attributes length
                attributesLength += 1 + Length.getNbBytes( localAttributeLength ) + localAttributeLength;
                
                attributeLength.add( new Integer( localAttributeLength ) );
                valsLength.add( new Integer( localValuesLength ) );
            }
        }
        
        searchResultEntryLength += 1 + Length.getNbBytes( attributesLength ) + attributesLength;

        // Return the result.
        return 1 + Length.getNbBytes( searchResultEntryLength ) + searchResultEntryLength;
    }
    
    /**
     * Encode the SearchResultEntry message to a PDU.
     * 
     * SearchResultEntry :
     * 
     * 0x64 LL
     *   0x04 LL objectName
     *   0x30 LL attributes
     *     0x30 LL partialAttributeList
     *       0x04 LL type
     *       0x31 LL vals
     *         0x04 LL attributeValue
     *         ... 
     *         0x04 LL attributeValue
     *     ... 
     *     0x30 LL partialAttributeList
     *       0x04 LL type
     *       0x31 LL vals
     *         0x04 LL attributeValue
     *         ... 
     *         0x04 LL attributeValue 
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
            // The SearchResultEntry Tag
            buffer.put( LdapConstants.SEARCH_RESULT_ENTRY_TAG );
            buffer.put( Length.getBytes( searchResultEntryLength ) ) ;
            
            // The objectName
            Value.encode( buffer, objectName.getBytes() );
            
            // The attributes sequence
            buffer.put( UniversalTag.SEQUENCE_TAG );
            buffer.put( Length.getBytes( attributesLength ) ) ;

            // The partial attribute list
            if ( ( partialAttributeList != null ) && ( partialAttributeList.size() != 0 ) )
            {
                NamingEnumeration attributes = partialAttributeList.getAll();
                int attributeNumber = 0;
                
                // Compute the attributes length
                while ( attributes.hasMoreElements() )
                {
                    Attribute attribute = (Attribute)attributes.nextElement();
                    
                    // The partial attribute list sequence
                    buffer.put( UniversalTag.SEQUENCE_TAG );
                    int localAttributeLength = ( (Integer)attributeLength.get( attributeNumber ) ).intValue();
                    buffer.put( Length.getBytes( localAttributeLength ) );

                    // The attribute type
                    Value.encode( buffer, attribute.getID() );
                    
                    // The values
                    buffer.put( UniversalTag.SET_TAG );
                    int localValuesLength = ( (Integer)valsLength.get( attributeNumber ) ).intValue();
                    buffer.put( Length.getBytes( localValuesLength ) );
                    
                    try
                    {
                        NamingEnumeration values = attribute.getAll();
                        
                        if ( values.hasMoreElements() )
                        {
                            while ( values.hasMoreElements() )
                            {
                                Object value = values.next();
                                
                                if ( value instanceof String )
                                {
                                	Value.encode( buffer, (String)value );
                                }
                                else if ( value instanceof OctetString )
                                {
                                	Value.encode( buffer, (OctetString)value );
                                }
                                else
                                {
                                	Value.encode( buffer, (byte[])value );
                                }
                            }
                        }
                    }
                    catch (NamingException ne)
                    {
                        throw new EncoderException("Cannot enumerate the values");
                    }
                    
                    // Go to the next attribute number;
                    attributeNumber++;
                }
            }
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException("The PDU buffer size is too small !"); 
        }

        return buffer;
    }

    /**
     * Returns the Search Result Entry string
     *
     * @return The Search Result Entry string 
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        sb.append( "    Search Result Entry\n" );
        sb.append( "        Object Name : '" ).append( objectName.toString() ).append( "'\n" );
        sb.append( "        Attributes\n" );

        if ( ( partialAttributeList == null ) || ( partialAttributeList.size() == 0 ) )
        {
            sb.append( "            No attributes\n" );
        }
        else
        {

            NamingEnumeration attributes = partialAttributeList.getAll();

            while ( attributes.hasMoreElements() )
            {

                Attribute attribute = ( Attribute ) attributes.nextElement();

                sb.append( "            Name : '" ).append( attribute.getID() ).append( "'\n" );

                try
                {

                    NamingEnumeration values = attribute.getAll();

                    if ( values.hasMoreElements() )
                    {
                        sb.append( "            Values\n" );

                        while ( values.hasMore() )
                        {
                        	Object value = values.nextElement();
                        	sb.append( "                '" );
                        	
                            if (value instanceof String)
                            {
                                sb.append( (String)value );
                            }
                            else if (value instanceof OctetString)
                            {
                                sb.append( StringUtils.dumpBytes( ( (OctetString)value ).getValue() ) );
                            }
                            else
                            {
                                sb.append( StringUtils.dumpBytes( (byte[])value ) );
                            }

                            sb.append("'\n" );
                        }
                    }
                    else
                    {
                        sb.append( "            No Values\n" );
                    }
                }
                catch ( NamingException ne )
                {
                    sb.append( "            Error while reading attribute.\n " );
                }
            }
        }

        return sb.toString();
    }
}

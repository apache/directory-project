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
import org.apache.asn1new.primitives.OctetString;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A ldapObject which stores the Simple authentication for a BindRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SimpleAuthentication  extends LdapAuthentication
{
	/** The logger */
    private static Logger log = LoggerFactory.getLogger( SimpleAuthentication.class );

    //~ Instance fields ----------------------------------------------------------------------------

    /** The simple authentication password */
    private OctetString simple;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the simple password
     *
     * @return The password
     */
    public OctetString getSimple()
    {
        return simple;
    }

    /**
     * Set the simple password
     *
     * @param simple The simple password
     */
    public void setSimple( OctetString simple )
    {
        this.simple = simple;
    }

    /**
     * Compute the Simple authentication length
     * 
     * Simple authentication :
     * 
     * 0x80 L1 simple
     * 
     * L1 = Length(simple)
     * 
     * Length(Simple authentication) = Length(0x80) + Length(L1) + Length(simple)
     */
    public int computeLength()
    {
    	int length = 1 + Length.getNbBytes( simple.getNbBytes() ) + simple.getNbBytes();

    	if ( log.isDebugEnabled() )
    	{
    		log.debug( "Simple Authentication length : " + length );
    	}

    	return length;
    }
    
    /**
     * Encode the simple authentication to a PDU.
     * 
     * SimpleAuthentication :
     * 
     * 0x80 LL simple
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
        	log.error( "Cannot put a PDU in a null buffer !" );
            throw new EncoderException( "Cannot put a PDU in a null buffer !" );
        }

        try 
        {
            // The simpleAuthentication Tag
            buffer.put( (byte)LdapConstants.BIND_REQUEST_SIMPLE_TAG );
            buffer.put( Length.getBytes( simple.getNbBytes() ) ) ;
            buffer.put( simple.getValue() ) ;
        }
        catch ( BufferOverflowException boe )
        {
        	log.error( "The PDU buffer size is too small !" );
            throw new EncoderException("The PDU buffer size is too small !"); 
        }

        return buffer;
    }

    /**
     * Return the simple authentication as a string
     *
     * @return The simple authentication string.
     */
    public String toString()
    {
        return ( ( simple == null ) ? "null" : simple.toString() );
    }
}
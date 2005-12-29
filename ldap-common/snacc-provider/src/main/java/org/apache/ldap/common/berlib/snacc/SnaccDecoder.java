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

/*
 * $Id: SnaccDecoder.java,v 1.1 2003/04/22 14:57:59 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.berlib.snacc ;


import java.io.InputStream ;

import org.apache.ldap.common.message.spi.Provider ;
import org.apache.ldap.common.message.spi.ProviderDecoder ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitor;

import com.ibm.asn1.BERDecoder ;
import com.ibm.asn1.ASN1Exception ;


/**
 * Snacc4J provider implementation of a the decoder SPI.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class SnaccDecoder implements ProviderDecoder
{
    /** Provider owning this decoder SPI implementation */
	private final Provider m_provider ;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates an instance of a Snacc4J Decoder implementation.
     *
     * @param a_provider the owning provider.
     */
	SnaccDecoder( final Provider a_provider )
    {
        m_provider = a_provider ;
    }


    // ------------------------------------------------------------------------
    // ProviderObject Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the Provider that this Decoder implementation is part of.
     *
     * @return the owning provider.
     */
    public Provider getProvider()
    {
        return m_provider ;
    }


    // ------------------------------------------------------------------------
    // Decoder Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Decodes a PDU from an input stream into a Snacc4J compiler generated stub
     * envelope: org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage.
     *
     * @param a_lock lock object used to exclusively read from the input stream
     * @param a_in the input stream to read and decode PDU bytes from
     * @return org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage snacc stub instance
     */
    public Object decode( Object a_lock, InputStream a_in ) throws ProviderException
    {
		LDAPMessage l_message = new LDAPMessage() ;
        BERDecoder l_decoder = new BERDecoder( a_in ) ;

        if( a_lock == null ) {
            // Log potentially dangerous synchronization situation that may
            // result.

            try
            {
				l_message.decode( l_decoder ) ;
			}
            catch( ASN1Exception e )
            {
				//getLogger().debug("Decoder failure: ", e) ;
				ProviderException pe =  new ProviderException( m_provider,
					"Snacc decoder failure!" ) ;
				pe.addThrowable( e ) ;
				throw pe ;
			}

            return l_message ;
        }

		try
        {
			// Synchronize on the input lock object to prevent concurrent reads
			synchronized(a_lock)
            {
				l_message.decode( l_decoder ) ;

				// Notify/awaken threads waiting to read from input stream
				a_lock.notifyAll() ;
			}
		}
        catch( ASN1Exception e )
        {
			//getLogger().debug("Decoder failure: ", e) ;
			ProviderException pe =  new ProviderException( m_provider,
                "Snacc decoder failure!" ) ;
            pe.addThrowable( e ) ;
            throw pe ;
		}

        return l_message ;
    }


    public void decode( Object encoded ) throws DecoderException
    {
        throw new UnsupportedOperationException( "not supported by provider " );
    }


    public void setCallback( DecoderCallback cb )
    {
        // @todo does nothing - should log with warning though
    }


    public void setDecoderMonitor( DecoderMonitor monitor )
    {
        // @todo does nothing - should log with warning though
    }
}

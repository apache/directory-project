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
package org.apache.ldap.common.berlib.snacc;


import com.ibm.asn1.ASN1Exception;
import com.ibm.asn1.BEREncoder;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1.codec.stateful.EncoderCallback;
import org.apache.asn1.codec.stateful.EncoderMonitor;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage;
import org.apache.ldap.common.message.spi.Provider;
import org.apache.ldap.common.message.spi.ProviderEncoder;
import org.apache.ldap.common.message.spi.ProviderException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


/**
 * Snacc4J provider implementation of a the encoder SPI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SnaccEncoder implements ProviderEncoder
{
    /** Provider owning this encoder SPI implementation */
	private final Provider provider;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates an instance of a Snacc4J EncoderSpi implementation.
     *
     * @param provider the owning provider.
     */
	SnaccEncoder( final Provider provider )
    {
        this.provider = provider;
    }


    // ------------------------------------------------------------------------
    // ProviderObject Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the Provider that this EncoderSpi implementation is part of.
     *
     * @return the owning provider.
     */
    public Provider getProvider()
    {
        return this.provider;
    }


    // ------------------------------------------------------------------------
    // ProviderEncoder Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Encodes a compiler stub specific ASN.1 message envelope containment tree
     * onto an output stream.
     *
     * @param lock lock object used to exclusively write to the output stream
     * @param out the OutputStream to encode the message envelope onto.
     * @param obj the top-level message envelope stub instance, i.e. for the
     * Snacc4J service provider this would be an instance of the LDAPMessage
     * compiler generated stub class.
     * @throws ProviderException to indicate an error while attempting to encode
     * the message envelope onto the output stream.  Provider specific
     * exceptions encountered while encoding can be held within this subclass
     * of MultiException.
     */
    public void encodeBlocking( Object lock, OutputStream out, Object obj )
    {
        byte [] buf = encodeBlockingByteArray( obj );

        // If lock is null perform unsynchronized write.
        if( lock == null )
        {
            // Log some warnings here about synch dangers.

            try
            {
				out.write( buf );
				out.flush();
            }
            catch( IOException ioe )
            {
                ProviderException pe = new ProviderException( provider , "" );
                pe.addThrowable( ioe );
                throw pe;
            }

            return;
        }

        // Lock exists so we do synchronized thread safe write.
		synchronized( lock )
        {
            try
            {
				out.write( buf );
				out.flush();
            }
            catch( IOException ioe )
            {
                ProviderException pe = new ProviderException( provider , "" );
                pe.addThrowable( ioe );
                throw pe;
            }

			lock.notifyAll();
		}
    }


    /**
     * Encodes a compiler stub specific ASN.1 message envelope containment tree
     * into byte array.
     *
     * @param obj the top-level message envelope stub instance, i.e. for the
     * Snacc4J service provider this would be an instance of the LDAPMessage
     * compiler generated stub class.
     * @throws ProviderException to indicate an error while attempting to encode
     * the message envelope into a byte buffer.  Provider specific exceptions
     * encountered while encoding can be held within this subclass of
     * MultiException.
     */
    public byte[] encodeBlockingByteArray( Object obj ) throws ProviderException
    {
        byte [] buf = null;
        BEREncoder encoder = null;
        LDAPMessage message = null;

        try
        {
            message = (LDAPMessage) obj;
        }
        catch( ClassCastException cce )
        {
            ProviderException pe = new ProviderException( provider,
                "Supplied message envelope object not recognized as a Snacc4J"
                + " LDAPv3 envelope stub!" );
            pe.addThrowable( cce );
            throw pe;
        }

        // Initialize encoder and begin encoding process.
        encoder = new BEREncoder();

        try
        {
	        message.encode( encoder );
	        buf = encoder.toByteArray();
        }
        catch( ASN1Exception e )
        {
            ProviderException pe = new ProviderException( provider,
                "Snacc4J BEREncoder failed to encode message!" );
            pe.addThrowable( e );
            throw pe;
        }

        return buf;
    }


    public ByteBuffer encodeBlocking( Object obj ) throws ProviderException
    {
        return ByteBuffer.wrap( encodeBlockingByteArray( obj ) );
    }


    // ------------------------------------------------------------------------
    // StatefulEncoder interfaces
    // ------------------------------------------------------------------------


    public void encode( Object obj ) throws EncoderException
    {
        throw new UnsupportedOperationException( "this encoder is not stateful" );
    }


    public void setCallback( EncoderCallback cb )
    {
    }


    public void setEncoderMonitor( EncoderMonitor monitor )
    {
    }
}

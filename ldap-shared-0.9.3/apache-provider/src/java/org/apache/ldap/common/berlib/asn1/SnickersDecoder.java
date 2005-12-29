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
package org.apache.ldap.common.berlib.asn1;


import org.apache.asn1.ber.digester.BERDigester;
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitor;
import org.apache.asn1.codec.stateful.StatefulDecoder;
import org.apache.ldap.common.berlib.asn1.decoder.LdapDigesterFactory;
import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.spi.Provider;
import org.apache.ldap.common.message.spi.ProviderDecoder;
import org.apache.ldap.common.message.spi.ProviderException;

import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * A Snickers based LDAP PDU decoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a> $Rev$
 */
public class SnickersDecoder implements ProviderDecoder
{
    private final Provider provider;
    private final BERDigester digester;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates an instance of a Snickers Decoder implementation.
     *
     * @param provider the owning provider.
     */
    public SnickersDecoder( Provider provider )
    {
        this.provider = provider;
        LdapDigesterFactory factory = LdapDigesterFactory.getSingleton();
        digester = factory.create();
    }


    /**
     * Gets the Provider that this Decoder implementation is part of.
     *
     * @return the owning provider.
     */
    public Provider getProvider()
    {
        return provider ;
    }


    // ------------------------------------------------------------------------
    // Decoder Interface Method Implementations
    // ------------------------------------------------------------------------


    public void decode( Object encoded ) throws DecoderException
    {
        ByteBuffer buf = null;

        if ( encoded instanceof ByteBuffer )
        {
            buf = ( ByteBuffer ) encoded;
        }
        else if ( encoded instanceof byte[] )
        {
            buf = ByteBuffer.wrap( ( byte[] ) encoded );
        }
        else
        {
            throw new DecoderException( "Expected either a byte[] or " +
                    "ByteBuffer argument but got a " + encoded.getClass() );
        }

        digester.decode( buf );
    }


    public void setCallback( DecoderCallback cb )
    {
        digester.setCallback( cb );
    }


    public void setDecoderMonitor( DecoderMonitor monitor )
    {
        digester.setDecoderMonitor( monitor );
    }


    /**
     * Decodes a PDU from an input stream into a Snickers compiler generated
     * stub envelope.
     *
     * @param lock lock object used to exclusively read from the input stream
     * @param in the input stream to read and decode PDU bytes from
     * @return return decoded stub
     */
    public Object decode( Object lock, InputStream in )
        throws ProviderException
    {
        LdapDigesterFactory factory = LdapDigesterFactory.getSingleton();

        // @todo we should probably pool digesters for performance
        BERDigester digesterTmp = factory.create();
        DigesterCallback dcb = new DigesterCallback();
        digesterTmp.setCallback( dcb );

        if( lock == null )
        {
            digest( digesterTmp, in );
            return dcb.getMessage() ;
        }

		try
        {
			// Synchronize on the input lock object to prevent concurrent reads
			synchronized ( lock )
            {
				digest( digesterTmp, in );

				// Notify/awaken threads waiting to read from input stream
				lock.notifyAll() ;
			}
		}
        catch( Exception e )
        {
			//getLogger().debug("Decoder failure: ", e) ;
			ProviderException pe =  new ProviderException( provider,
                "Snickers decoder failure!" ) ;
            pe.addThrowable( e ) ;
            throw pe ;
		}

        return dcb.getMessage() ;
    }


    /**
     * Feeds the bytes within the input stream to the digester to generate the
     * resultant decoded Message.
     *
     * @param in
     * @throws ProviderException
     */
    private void digest( BERDigester digesterTmp, InputStream in )
            throws ProviderException
    {
        byte[] buf = null;

        try
        {
            int amount = -1;
            while( in.available() > 0 )
            {
                buf = new byte[in.available()];

                if ( ( amount = in.read( buf ) ) == -1 )
                {
                    break;
                }

                digesterTmp.decode( ByteBuffer.wrap( buf, 0, amount ) );
            }
        }
        catch( Exception e )
        {
            //getLogger().debug("Decoder failure: ", e) ;
            ProviderException pe =  new ProviderException( provider,
                "Snickers decoder failure!" ) ;
            pe.addThrowable( e ) ;
            throw pe ;
        }
    }


    class DigesterCallback implements DecoderCallback
    {
        /** the message we recieved via the callback */
        private Message msg;

        /**
         * Callback to deliver a fully decoded object.
         *
         * @param decoder the stateful decoder driving the callback
         * @param decoded the object that was decoded
         */
        public void decodeOccurred( StatefulDecoder decoder, Object decoded )
        {
            msg = ( Message ) decoded;
        }


        /**
         * Gets and clears the message reference to the most recently delivered
         * (decoded) message.  If no Message was decoded and the message is null
         * then an exception is raised.
         *
         * @return the last decoded Message
         * @throws ProviderException if nothin was decoded since the last call
         */
        Message getMessage() throws ProviderException
        {
            if ( msg == null )
            {
                throw new ProviderException( provider,
                        "Callback did not receive a message as expected from " +
                        "the Snickers BERDigester" );
            }

            Message tmp = msg;
            msg = null;
            return tmp;
        }
    }
}

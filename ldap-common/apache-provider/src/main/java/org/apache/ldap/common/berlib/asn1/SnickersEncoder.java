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

import org.apache.asn1.ber.digester.rules.ByteAccumulator;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1.codec.stateful.EncoderCallback;
import org.apache.asn1.codec.stateful.EncoderMonitor;
import org.apache.asn1.codec.stateful.StatefulEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.SnickersLdapEncoder;
import org.apache.ldap.common.message.spi.Provider;
import org.apache.ldap.common.message.spi.ProviderEncoder;
import org.apache.ldap.common.message.spi.ProviderException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;


/**
 * Snickers LDAP BER provider's encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SnickersEncoder implements ProviderEncoder
{
    private final Provider provider;
    private final OutputCallback outCb;
    private final AccumulatorCallback accCb;
    private final SnickersLdapEncoder encoder;


    public SnickersEncoder( Provider provider )
    {
        this.provider = provider;
        outCb = new OutputCallback();
        accCb = new AccumulatorCallback();
        encoder = new SnickersLdapEncoder();
        encoder.setCallback( outCb );
    }


    public void encodeBlocking( Object lock, OutputStream out, Object obj )
            throws ProviderException
    {
        synchronized( encoder )
        {
            outCb.attach( out );
            encoder.setCallback( outCb );

            try
            {
                encoder.encode( obj );
            }
            catch ( EncoderException e )
            {
                ProviderException pe = new ProviderException( provider,
                        "Snickers encoder failed to encode object: " + obj );
                throw pe;
            }
        }
    }


    public ByteBuffer encodeBlocking( Object obj ) throws ProviderException
    {
        synchronized( encoder )
        {
            encoder.setCallback( accCb );

            try
            {
                encoder.encode( obj );
            }
            catch ( EncoderException e )
            {
                ProviderException pe = new ProviderException( provider,
                        "Snickers encoder failed to encode object: " + obj );
                throw pe;
            }

            return accCb.getEncoded() ;
        }
    }


    public byte[] encodeToArray( Object obj ) throws ProviderException
    {
        synchronized( encoder )
        {
            encoder.setCallback( accCb );

            try
            {
                encoder.encode( obj );
            }
            catch ( EncoderException e )
            {
                ProviderException pe = new ProviderException( provider,
                        "Snickers encoder failed to encode object: " + obj );
                throw pe;
            }

            return BufferUtils.getArray( accCb.getEncoded() );
        }
    }


    /**
     * Gets the Provider associated with this SPI implementation object.
     *
     * @return Provider.
     */
    public Provider getProvider()
    {
        return provider;
    }


    public void encode( Object obj ) throws EncoderException
    {
        this.encoder.encode( obj );
    }


    public void setCallback( EncoderCallback cb )
    {
        this.encoder.setCallback( cb );
    }


    public void setEncoderMonitor( EncoderMonitor monitor )
    {
        encoder.setEncoderMonitor( monitor );
    }


    class AccumulatorCallback implements EncoderCallback
    {
        ByteAccumulator accumulator = new ByteAccumulator();

        /**
         * Callback to deliver a fully encoded object.
         *
         * @param encoder the stateful encoder driving the callback
         * @param encoded the object that was encoded
         */
        public void encodeOccurred( StatefulEncoder encoder, Object encoded )
        {
            if ( encoded instanceof ByteBuffer[] )
            {
                ByteBuffer[] buffers = ( ByteBuffer[] ) encoded;

                for ( int ii = 0; ii < buffers.length; ii++ )
                {
                    accumulator.fill( buffers[ii] );
                }

                return;
            }

            accumulator.fill( ( ByteBuffer ) encoded );
        }


        ByteBuffer getEncoded()
        {
            return accumulator.drain();
        }
    }


    class OutputCallback implements EncoderCallback
    {
        private WritableByteChannel channel = null;


        /**
         * Callback to deliver a fully encoded object.
         *
         * @param encoder the stateful encoder driving the callback
         * @param encoded the object that was encoded
         */
        public void encodeOccurred( StatefulEncoder encoder, Object encoded )
        {
            try
            {
                channel.write( ( ByteBuffer ) encoded );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }


        void attach( WritableByteChannel channel )
        {
            this.channel = channel;
        }


        void attach( OutputStream out )
        {
            this.channel = Channels.newChannel( out ) ;
        }
    }
}

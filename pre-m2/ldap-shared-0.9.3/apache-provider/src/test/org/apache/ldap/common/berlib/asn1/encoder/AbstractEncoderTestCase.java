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
package org.apache.ldap.common.berlib.asn1.encoder;


import junit.framework.TestCase;
import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.DeterminateLengthVisitor;
import org.apache.asn1.ber.TupleEncodingVisitor;
import org.apache.asn1.codec.stateful.EncoderCallback;
import org.apache.asn1.codec.stateful.StatefulEncoder;

import java.nio.ByteBuffer;


/**
 * A testcase base class used for encoder test cases.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public abstract class AbstractEncoderTestCase extends TestCase
        implements EncoderCallback
{
    /** the default buffer size for accumulator when none is provided */
    private static final int DEFAULT_BUFSZ = 128;
    /** collects/accumulates the chunks emitted from the encoder */
    private ByteBuffer accumulator;
    /** the max buffer size for the accumulator */
    private final int bufsz;


    /**
     * Creates a test case with the default buffer size for the accumulator.
     */
    public AbstractEncoderTestCase()
    {
        super();
        bufsz = DEFAULT_BUFSZ;
    }


    /**
     * Creates a test case with a name using the default buffer size for the
     * accumulator.
     *
     * @param name the name of the test case
     */
    public AbstractEncoderTestCase( String name )
    {
        super( name );
        bufsz = DEFAULT_BUFSZ;
    }


    /**
     * Creates a test case with a name and a maximum buffer size.
     *
     * @param name the name of the test case
     * @param bufsz the max size of the buffer
     */
    public AbstractEncoderTestCase( String name, int bufsz )
    {
        super( name );
        this.bufsz = bufsz;
    }


    /**
     *
     *
     * @throws Exception
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        accumulator = ByteBuffer.wrap( new byte[bufsz] );
    }


    /**
     * Sets the accumulator to null.
     *
     * @throws Exception - from super call
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        accumulator = null;
    }


    /**
     * Callback to deliver a fully encoded object.
     *
     * @param encoder the stateful encoder driving the callback
     * @param encoded the object that was encoded
     */
    public void encodeOccurred( StatefulEncoder encoder, Object encoded )
    {
        ByteBuffer[] buffers = ( ByteBuffer [] ) encoded;

        for ( int ii = 0; ii < buffers.length; ii++ )
        {
            accumulator.put( buffers[ii] );
        }
    }


    protected byte[] getEncoded()
    {
        byte[] encoded = new byte[accumulator.position()];
        System.arraycopy( accumulator.array(), 0, encoded,  0, accumulator.position() );
        return encoded;
    }


    /**
     * Encodes a tuple tree into the accumulator's byte buffer using the
     * determinate length form.
     *
     * @param node the node to be encoded.
     */
    protected void encode( DefaultMutableTupleNode node )
    {
        DeterminateLengthVisitor visitor = new DeterminateLengthVisitor();
        node.accept( visitor );

        TupleEncodingVisitor encoder = new TupleEncodingVisitor();
        encoder.setCallback( this );
        node.accept( encoder );
        encoder.flush();
    }
}

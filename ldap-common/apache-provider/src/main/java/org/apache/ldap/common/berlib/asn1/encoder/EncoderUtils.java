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


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.PrimitiveUtils;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.util.StringTools;

import java.nio.ByteBuffer;


/**
 * Common utilities used by encoders for encoding primitive types into
 * TupleNodes.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class EncoderUtils
{
    /**
     * Encodes a Java String into a TupleNode.  The tag of the Tuple contained
     * within the returned TupleNode defaults to a UNIVERSAL OCTET_STRING.
     *
     * @param octets the String value to encode
     * @return the TupleNode containing the Tuple for the integer value
     */
    public static TupleNode encode( String octets )
    {
        return encode( UniversalTag.OCTET_STRING, octets );
    }


    /**
     * Encodes a Java String into a TupleNode.
     *
     * @param tag the tag enumeration to use for the Tuple
     * @param octets the String value to encode
     * @return the TupleNode containing the Tuple for the string value
     */
    public static TupleNode encode( TagEnum tag, String octets )
    {
        Tuple t = new Tuple();
        t.setTag( tag, true );
        ByteBuffer chunk = ByteBuffer.wrap( StringTools.getBytesUtf8( octets ) );
        t.setLength( chunk.remaining() );
        t.setLastValueChunk( chunk );
        return new DefaultMutableTupleNode( t );
    }


    /**
     * Encodes a Java byte[] into a TupleNode.  The tag of the Tuple contained
     * within the returned TupleNode defaults to a UNIVERSAL OCTET_STRING.
     *
     * @param octets the byte[] to encode
     * @return the TupleNode containing the Tuple for the byte[]
     */
    public static TupleNode encode( byte[] octets )
    {
        return encode( UniversalTag.OCTET_STRING, octets );
    }


    /**
     * Encodes a Java byte[] into a TupleNode.
     *
     * @param tag the tag enumeration to use for the Tuple
     * @param octets the byte[] to encode
     * @return the TupleNode containing the Tuple for the byte[] value
     */
    public static TupleNode encode( TagEnum tag, byte[] octets )
    {
        Tuple t = new Tuple();
        t.setTag( tag, true );
        ByteBuffer chunk = ByteBuffer.wrap( octets );
        t.setLength( chunk.remaining() );
        t.setLastValueChunk( chunk );
        return new DefaultMutableTupleNode( t );
    }


    /**
     * Encodes a primitive Java integer into a TupleNode.  The tag of the Tuple
     * contained within the returned TupleNode defaults to a UNIVERSAL INTEGER.
     *
     * @param intval the integer value to encode
     * @return the TupleNode containing the Tuple for the integer value
     */
    public static TupleNode encode( int intval )
    {
        return encode( UniversalTag.INTEGER, intval );
    }


    /**
     * Encodes a primitive Java integer into a TupleNode.
     *
     * @param tag the tag enumeration to use for the Tuple
     * @param intval the integer value to encode
     * @return the TupleNode containing the Tuple for the integer value
     */
    public static TupleNode encode( TagEnum tag, int intval )
    {
        Tuple t = new Tuple();
        t.setTag( tag, true );
        ByteBuffer chunk = ByteBuffer.wrap(
                PrimitiveUtils.encodeInt( intval ) );
        t.setLength( chunk.remaining() );
        t.setLastValueChunk( chunk );
        return new DefaultMutableTupleNode( t );
    }


    /**
     * Encodes a boolean value into a TupleNode.  The tag defaults in this case
     * to a UNIVERSAL BOOLEAN.
     *
     * @param bool the true or false value to encode
     * @return the TupleNode containing the Tuple for the boolean value
     */
    public static TupleNode encode( boolean bool )
    {
        return encode( UniversalTag.BOOLEAN, bool );
    }


    /**
     * Encodes a boolean value into a TupleNode.
     *
     * @param tag the tag used for the boolean value
     * @param bool the boolean value
     * @return the TupleNode containing the Tuple for the boolean value
     */
    public static TupleNode encode( TagEnum tag, boolean bool )
    {
        Tuple t = new Tuple();
        t.setTag( tag, true );
        ByteBuffer chunk = ByteBuffer.wrap(
                PrimitiveUtils.encodeBoolean( bool ) );
        t.setLength( chunk.remaining() );
        t.setLastValueChunk( chunk );
        return new DefaultMutableTupleNode( t );
    }
}

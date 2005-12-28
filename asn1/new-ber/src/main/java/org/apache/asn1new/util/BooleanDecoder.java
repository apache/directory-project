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
package org.apache.asn1new.util;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.ber.tlv.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Parse and decode a Boolean value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BooleanDecoder
{
    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( BooleanDecoder.class );

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Parse a byte buffer and send back a booelan.
     * 
     * @param value The byte buffer to parse
     *
     * @return A boolean.
     *
     * @throws DecoderException Thrown if the byte stream does not contains a boolean
     */
    public static boolean parse( Value value ) throws BooleanDecoderException
    {
        byte[] bytes  = value.getData();

        if ( bytes.length != 1 )
        {
            throw new BooleanDecoderException(
                "The value is not 1 byte long. This is not allowed for a boolean" );
        }

        if ( ( bytes[0] != 0 ) && ( bytes[0] != 255 ) )
        {
            log.warn( "A boolean must be encoded with a 0x00 or a 0xFF value" );
        }

        return bytes[0] != 0;
    }
}

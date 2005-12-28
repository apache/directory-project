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
package org.apache.asn1.ber ;


import java.nio.ByteBuffer;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.AbstractStatefulDecoder;


/**
 * A BER TLV Tag component decoder.  This decoder delivers a Tag instance
 * to its callback.  For efficiency the same Tag object is reused.  Callback
 * implementations should not copy the handle to the Tag object delivered but
 * should copy the data if they need it over the long term.
 *
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagDecoder extends AbstractStatefulDecoder
{
    private final Tag tag = new Tag() ;
    
    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#decode(
     * java.lang.Object)
     */
    public void decode( Object encoded ) throws DecoderException
    {
        ByteBuffer buf = ( ByteBuffer ) encoded ;
        
        while ( buf.hasRemaining() )
        {
            byte octet = buf.get() ;
            tag.add( octet ) ;
        
            if ( tag.isFixated() )
            {
                decodeOccurred( tag ) ;
                tag.clear() ;
                return ;
            }
        }
    }
}

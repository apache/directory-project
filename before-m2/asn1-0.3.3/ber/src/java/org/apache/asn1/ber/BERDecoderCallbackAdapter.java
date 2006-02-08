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


import org.apache.asn1.codec.stateful.StatefulDecoder;


/**
 * A do nothing callback adapter.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDecoderCallbackAdapter implements BERDecoderCallback
{
    /* (non-Javadoc)
     * @see org.apache.asn1.ber.BERDecoderCallback#tagDecoded(
     * org.apache.asn1.ber.Tuple)
     */
    public void tagDecoded( Tuple tlv )
    {
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.ber.BERDecoderCallback#lengthDecoded(
     * org.apache.asn1.ber.Tuple)
     */
    public void lengthDecoded( Tuple tlv )
    {
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.ber.BERDecoderCallback#partialValueDecoded(
     * org.apache.asn1.ber.Tuple)
     */
    public void partialValueDecoded(Tuple tlv)
    {
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderCallback#decodeOccurred(
     * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Object)
     */
    public void decodeOccurred( StatefulDecoder decoder, Object decoded )
    {
    }
}

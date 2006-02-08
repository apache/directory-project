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


import org.apache.asn1.codec.stateful.EncoderCallback;


/**
 * A specialized encoder callback that handles specific BER events.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public interface BEREncoderCallback extends EncoderCallback
{
    /**
     * Method used to receive notification that a tag was encoded.  The
     * following tag properties of the TLV tuple are valid at this point:
     * <ul>
     * <li>id</li>
     * <li>isPrimitive</li>
     * <li>typeClass</li>
     * </ul>
     * 
     * @param tlv the TLV tuple
     */
    void tagEncoded( Tuple tlv ) ;
    
    /**
     * Method used to receive notification that a length was encoded.  The
     * following properties of the TLV tuple are valid at this point:
     * <ul>
     * <li>id</li>
     * <li>isPrimitive</li>
     * <li>typeClass</li>
     * <li>length</li>
     * </ul>
     * 
     * @param tlv the TLV tuple
     */
    void lengthEncoded( Tuple tlv ) ;
    
    /**
     * Method used to recieve notification that a part of the value was encoded.
     * 
     * @param tlv the TLV tuple 
     */
    void partialValueEncoded( Tuple tlv ) ;
}

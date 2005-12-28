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
package org.apache.asn1.ber.digester ;


import java.nio.ByteBuffer;

import org.apache.asn1.ber.TypeClass;


/**
 * A BER event processing rule.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public interface Rule
{
    /**
     * Get the <code>BERDigester</code> with which this <code>Rule</code> has
     * been associated.
     * 
     * @return the associated rulesBase
     */
    BERDigester getDigester() ;
    
    /**
     * Set the <code>BERDigester</code> with which this <code>Rule</code> will
     * be associated.
     * 
     * @param digester the rulesBase to associate this rule with
     */
    void setDigester( BERDigester digester ) ;
    
    /**
     * Called when the tag of the matched TLV is encountered.
     * 
     * @param id the tag's id 
     * @param isPrimitive whether tlv is primitive or constructed
     * @param typeClass the tag's type class
     */
    void tag( int id, boolean isPrimitive, TypeClass typeClass ) ;
    
    /**
     * Called when the length of a TLV is encountered.
     * 
     * @param length the length in bytes of the value
     */
    void length( int length ) ;
    
    /**
     * Called when a peice of the value is available.
     * 
     * @param buf a portion of the value
     */
    void value( ByteBuffer buf ) ;
    
    /**
     * Called when the tlv has been completely consumed.
     */
    void finish() ;
}

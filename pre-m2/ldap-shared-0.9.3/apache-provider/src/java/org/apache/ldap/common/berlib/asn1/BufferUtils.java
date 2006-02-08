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
package org.apache.ldap.common.berlib.asn1 ;


import org.apache.commons.lang.ArrayUtils;

import java.nio.ByteBuffer;


/**
 * ByteBuffer manipulation utilities.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BufferUtils
{
    /**
     * One way or another gets an array from a buffer.  If the backing store
     * is an accessible array and the buffer's limit and capacity are the same
     * then the backing store of the buffer is returned.  Otherwise there is
     * no choice but to copy the ByteBuffer.  Hence the returned byte[] may be
     * shared with the ByteBuffer so changes to one will effect the other.
     * Don't use this method if the ByteBuffer will be reused.
     *
     * @param buf the ByteBuffer to get an array for
     * @return the byte[] representing the contents of the ByteBuffer
     */
    public static byte[] getArray( ByteBuffer buf )
    {
        byte[] array = ArrayUtils.EMPTY_BYTE_ARRAY ;

        if ( buf == null || buf.remaining() == 0 )
        {
            return array ;
        }

        if ( buf.limit() == buf.capacity() && buf.hasArray() )
        {
            array = buf.array() ;
        }
        else
        {
            // copy because we don't have accessible array or data < array
            array = new byte[buf.remaining()] ;
            buf.get( array ) ;
        }

        return array ;
    }
}

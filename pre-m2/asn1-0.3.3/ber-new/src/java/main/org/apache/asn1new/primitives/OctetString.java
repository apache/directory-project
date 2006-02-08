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
package org.apache.asn1new.primitives;

import java.io.Serializable;

import org.apache.asn1new.util.StringUtils;


/**
 * Implement the Octet String primitive type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OctetString implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    //~ Static fields/initializers -----------------------------------------------------------------

    /** A null OctetString */
    public static final OctetString EMPTY_STRING = new OctetString(0);

    /** A flag to mark the OctetString as Streamed (for OctetString larger than 1024 chars) */
    // TODO implement the streaming...
    public static final boolean STREAMED = true;

    /** The default length of an octet string */
    private static final int DEFAULT_LENGTH = 1024;

    //~ Instance fields ----------------------------------------------------------------------------

    /** Tells if the OctetString is streamed or not */
    private boolean isStreamed;

    /** The string is stored in a byte array */
    private byte[] bytes;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a OctetString with a specific length.
     * @param length The OctetString length
    */
    public OctetString(  int length )
    {
        if ( length > DEFAULT_LENGTH )
        {
            // TODO : implement the streaming
            isStreamed = true;
            bytes      = new byte[length];
        }
        else
        {
            isStreamed = false;
            bytes      = new byte[length];
        }
    }

    /**
     * Creates a streamed OctetString with a specific length.
     * Actually, it's just a simple OctetString.
     * TODO Implement streaming.
     * @param length The OctetString length 
     * @param isStreamed Tells if the OctetString must be streamed or not 
    */
    public OctetString(  int length, boolean isStreamed )
    {
        this.isStreamed = isStreamed;

        if ( isStreamed )
        {
            // TODO : implement the streaming
            bytes = new byte[length];
        }
        else
        {
            bytes = new byte[length];
        }
    }

    /**
     * Creates a OctetString with a value.  
     * 
     * @param bytes The value to store.
     */
    public OctetString(  byte[] bytes )
    {
        if ( bytes.length > DEFAULT_LENGTH )
        {
            isStreamed = true;

            // It will be a streamed OctetString.
            // TODO : implement the streaming
            this.bytes = new byte[bytes.length];

            // We have to copy the data, because the parameter
            // is not a copy.
            System.arraycopy( bytes, 0, this.bytes, 0, bytes.length );
        }
        else
        {
            isStreamed = false;

            this.bytes = new byte[bytes.length];

            // We have to copy the data, because the parameter
            // is not a copy.
            System.arraycopy( bytes, 0, this.bytes, 0, bytes.length );
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Set a new octetString in the OctetString. It will replace the old OctetString,
     * and reset the current length with the new one.
     * 
     * @param bytes The string to store
     */
    public void setData( byte[] bytes )
    {
        if ( bytes.length > DEFAULT_LENGTH )
        {

            if ( this.bytes.length < bytes.length )
            {
                // The current size is too small.
                // We have to allocate more space
                // It will be a streamed OctetString.
                // TODO : implement the streaming
                this.bytes = new byte[bytes.length];
            }

            System.arraycopy( bytes, 0, this.bytes, 0, bytes.length );
        }
        else
        {
            System.arraycopy( bytes, 0, this.bytes, 0, bytes.length );
        }
    }

    /**
     * Get the data stored into the OctetString
     * @return A byte array
     */
    public byte[] getValue()
    {
        return bytes;
    }

    /**
     * Return a native String representation of the OctetString.
     * @return A string representing the OctetString
    */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < bytes.length; i++ )
        {
            if ( ( bytes[i] < 32 ) || ( bytes[i] > 127 ) ) 
            {
                sb.append( StringUtils.dumpByte( bytes[i] ) );
            }
            else
            {
                sb.append( (char)bytes[i] );
            }
        }

        return sb.toString();
    }

    /**
     * Tells if the OctetString is streamed or not
     * @return <code>true</code> if the OctetString is streamed.
     */
    public boolean isStreamed()
    {
        return isStreamed;
    }
    
    /**
     * @return Returns the length.
     */
    public int getNbBytes() 
    {
        return bytes.length;
    }
}

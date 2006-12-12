/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.otp;


/**
 * Attributes use to calculate a HOTP value.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public final class HotpAttributes
{
    private static final int DEFAULT_SIZE = 6;

    /** the shared secret key */
    private final byte[] secret;
    /** the moving factor */
    private final long factor;
    /** the size of the value to generate */
    private final int size;


    /**
     * Creates a HotpAttributes object with all three attributes.
     *
     * @param factor the moving factor
     * @param secret the shared secret
     */
    public HotpAttributes( long factor, byte[] secret )
    {
        this( DEFAULT_SIZE, factor, secret );
    }


    /**
     * Creates a HotpAttributes object with all three attributes.
     *
     * @param size the size of the value to generate
     * @param factor the moving factor
     * @param secret the shared secret
     */
    public HotpAttributes( int size, long factor, byte[] secret )
    {
        if ( 6 > size || size > 10 )
        {
            StringBuffer buf = new StringBuffer();
            buf.append( "expecting HOTP value size in range [6,10] but got " );
            buf.append( size );
            throw new IllegalArgumentException( buf.toString() );
        }

        this.size = size;
        this.factor = factor;
        this.secret = secret;
    }


    /**
     * Gets the shared secret key.
     *
     * @return the shared secret key
     */
    public byte[] getSecret()
    {
        return secret;
    }


    /**
     * Gets the moving factor.
     *
     * @return the moving factor
     */
    public long getFactor()
    {
        return factor;
    }


    /**
     * Gets the size of the value to generate.
     *
     * @return the size of charactor in the generated OTP
     */
    public int getSize()
    {
        return size;
    }


    /**
     * Checks to see that another HotpAttributes matches this one exactly.
     *
     * @param obj the other object to compare this HotpAttributes to
     * @return true if the objects are the same for all components
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( ! ( obj instanceof HotpAttributes ) )
        {
            return false;
        }

        HotpAttributes other = ( HotpAttributes ) obj;
        if ( other.getSize() != this.size )
        {
            return false;
        }
        if ( other.getFactor() != this.factor )
        {
            return false;
        }
        if ( other.getSecret().length != this.secret.length )
        {
            return false;
        }

        for ( int ii = 0; ii < this.secret.length; ii++ )
        {
            if ( this.secret[ii] != other.getSecret()[ii] )
            {
                return false;
            }
        }

        return true;
    }
}

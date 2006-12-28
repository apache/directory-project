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
package org.safehaus.triplesec.jaas;


import org.safehaus.otp.HotpErrorConstants;

import javax.security.auth.login.LoginException;


/**
 * A Hotp specific SamException.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpLoginException extends LoginException
{
    private static final long serialVersionUID = 1L;
    /** the type of this exception */
    private final int ordinal;


    /**
     * Creates a HotpLoginException using an ordinal.
     *
     * @param ordinal the ordinal for this exception type
     */
    public HotpLoginException( int ordinal )
    {
        super( ( String ) HotpErrorConstants.getErrorMessage( ordinal ) );

        this.ordinal = ordinal;
    }


    /**
     * Creates a HotpLoginException using an ordinal with string.  If the String argument
     * does have a prefix on is inserted based on the ordinal.
     *
     * @param ordinal the ordinal for this exception type
     * @param s an error message string
     */
    public HotpLoginException( int ordinal, String s )
    {
        super( ! s.startsWith( HotpErrorConstants.PREFIX ) ? HotpErrorConstants.PREFIX + ordinal + "]: " + s : s );

        this.ordinal = ordinal;
    }


    /**
     * Creates a HotpLoginException from error mesage which must have a prefix.
     *
     * @param s an error message string
     */
    public HotpLoginException( String s )
    {
        super( s );

        if ( ! s.startsWith( HotpErrorConstants.PREFIX ) )
        {
            throw new IllegalArgumentException( "Message does not contain the prefix: " + HotpErrorConstants.PREFIX );
        }

        ordinal = Integer.parseInt( s.split( HotpErrorConstants.PREFIX )[1].split( "]" )[0] );
    }


    /**
     * Get's the ordinal for this exception type.
     *
     * @return the ordinal for this exception type
     */
    public int getOrdinal()
    {
        return this.ordinal;
    }
}

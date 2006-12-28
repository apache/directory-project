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
package org.safehaus.triplesec.verifier.hotp;


import org.apache.directory.server.kerberos.shared.messages.value.SamType;
import org.apache.directory.server.kerberos.sam.SamException;

import org.safehaus.otp.HotpErrorConstants;


/**
 * A Hotp specific SamException.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpException extends SamException
{
    private static final long serialVersionUID = -798453831733119227L;
    /** the type of this exception */
    private final int ordinal;
 

    /**
     * Creates a HotpException using an ordinal.
     *
     * @param ordinal the ordinal for this exception type
     */
    public HotpException( int ordinal )
    {
        super( SamType.PA_SAM_TYPE_APACHE, ( String ) HotpErrorConstants.getErrorMessage( ordinal ) );
        this.ordinal = ordinal;
    }


    /**
     * Creates a HotpException using an ordinal with underlying exception.
     *
     * @param ordinal the ordinal for this exception type
     * @param throwable the underlying exception
     */
    public HotpException( int ordinal, Throwable throwable )
    {
        super( SamType.PA_SAM_TYPE_APACHE, ( String ) HotpErrorConstants.getErrorMessage( ordinal ), throwable );

        this.ordinal = ordinal;
    }


    /**
     * Creates a HotpException using an ordinal with string.  If the String argument
     * does have a prefix on is inserted based on the ordinal.
     *
     * @param ordinal the ordinal for this exception type
     * @param s an error message string
     */
    public HotpException( int ordinal, String s )
    {
        super( SamType.PA_SAM_TYPE_APACHE,  ! s.startsWith( HotpErrorConstants.PREFIX ) ? HotpErrorConstants.PREFIX + ordinal + "]: " + s : s );

        this.ordinal = ordinal;
    }


    /**
     * Creates a HotpException using an ordinal with underlying exception and error mesage.
     *
     * @param ordinal the ordinal for this exception type
     * @param s an error message string
     * @param throwable the underlying exception
     */
    public HotpException( int ordinal, String s, Throwable throwable )
    {
        super( SamType.PA_SAM_TYPE_APACHE,  ! s.startsWith( HotpErrorConstants.PREFIX ) ? HotpErrorConstants.PREFIX + ordinal + "]: " + s : s, throwable );

        this.ordinal = ordinal;
    }


    /**
     * Creates a HotpException from error mesage which must have a prefix.
     *
     * @param s an error message string
     */
    public HotpException( String s )
    {
        super( SamType.PA_SAM_TYPE_APACHE, s );

        if ( ! s.startsWith( HotpErrorConstants.PREFIX ) )
        {
            throw new IllegalArgumentException( "Message does not contain the prefix: " + HotpErrorConstants.PREFIX );
        }

        ordinal = HotpErrorConstants.getOrdinal( s );
    }


    /**
     * Creates a HotpException from another exception however it's message must have a prefix.
     *
     * @param throwable the underlying exception
     */
    public HotpException( Throwable throwable )
    {
        super( SamType.PA_SAM_TYPE_APACHE, throwable );

        if ( ! throwable.getMessage().startsWith( HotpErrorConstants.PREFIX ) )
        {
            throw new IllegalArgumentException( "Throwable's message does not contain the prefix: " + HotpErrorConstants.PREFIX  );
        }

        ordinal = HotpErrorConstants.getOrdinal( throwable.getMessage() );
    }


    /**
     * Creates a HotpException from error mesage which must have a prefix.
     *
     * @param s an error message string
     * @param throwable the underlying exception
     */
    public HotpException( String s, Throwable throwable )
    {
        super( SamType.PA_SAM_TYPE_APACHE, s, throwable );

        if ( ! s.startsWith( HotpErrorConstants.PREFIX ) )
        {
            throw new IllegalArgumentException( "Message does not contain the prefix: " + HotpErrorConstants.PREFIX  );
        }

        ordinal = HotpErrorConstants.getOrdinal( s );
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

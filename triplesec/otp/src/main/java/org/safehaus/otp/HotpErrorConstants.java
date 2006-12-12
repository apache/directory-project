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


import java.util.Vector;


/**
 * Constants used by hotp.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpErrorConstants
{
    /*
     * Nice to have idea
     * ------------------
     * It would be nice to be able to grab a ResourceBundle around a Preferences
     * node and use that to access these error messages in an internationalized
     * manner.  It would be nice to wrap a ResourceBundle around Preferences in
     * general.
     */

    /**
     * Message prefix for locked out accounts
     */
    public static final String PREFIX = "HOTP-";

    /**
     * Message prefix for locked out accounts
     */
    public static final int LOCKEDOUT_VAL = 0;

    /**
     * Message prefix for locked out accounts
     */
    public static final int DISABLED_VAL = 5;

    /**
     * Message prefix for locked out accounts
     */
    public static final String LOCKEDOUT_PREFIX = PREFIX + LOCKEDOUT_VAL + ": ";

    /**
     * Message prefix for disabled accounts
     */
    public static final String DISABLED_PREFIX = PREFIX + DISABLED_VAL + ": ";

    /**
     * Message prefix for resych initiation
     */
    public static final int RESYNCH_STARTING_VAL = 1;

    /**
     * Message prefix for resych initiation
     */
    public static final String RESYNCH_STARTING_PREFIX = PREFIX + RESYNCH_STARTING_VAL + ": ";

    /**
     * Message prefix for progressing resych process
     */
    public static final int RESYNCH_INPROGRESS_VAL = 2;

    /**
     * Message prefix for progressing resych process
     */
    public static final String RESYNCH_INPROGRESS_PREFIX = PREFIX + RESYNCH_INPROGRESS_VAL + ": ";

    /**
     * Message prefix for preauth failure
     */
    public static final int HOTPAUTH_FAILURE_VAL = 3;

    /**
     * Message prefix for preauth failure
     */
    public static final String HOTPAUTH_FAILURE_PREFIX = PREFIX + HOTPAUTH_FAILURE_VAL + ": ";

    /**
     * Message for locked out accounts
     */
    public static final String LOCKEDOUT_MSG = LOCKEDOUT_PREFIX + "Account locked - contact your administrator!";

    /**
     * Message for disabled accounts
     */
    public static final String DISABLED_MSG = DISABLED_PREFIX + "Account disabled - contact your administrator!";

    /**
     * Message for resych initiation
     */
    public static final String RESYNCH_STARTING_MSG = RESYNCH_STARTING_PREFIX + "Resynch starting - keep entering passwords!";

    /**
     * Message for progressing resych process
     */
    public static final String RESYNCH_INPROGRESS_MSG = RESYNCH_INPROGRESS_PREFIX + "Resynch in progress - keep entering passwords!";

    /**
     * Message for preauth failure
     */
    public static final String HOTPAUTH_FAILURE_MSG = HOTPAUTH_FAILURE_PREFIX + "Preauth failed!";

    /**
     * Message prefix for inactive accounts
     */
    public static final int INACTIVE_VAL = 4;

    /**
     * Message prefix for inactive accounts
     */
    public static final String INACTIVE_PREFIX = PREFIX + INACTIVE_VAL + ": ";

    /**
     * Message for inactive accounts
     */
    public static final String INACTIVE_MSG = INACTIVE_PREFIX + "Account awaiting activation!";

    /**
     * An unmodifiable list of messages to be indexed by ordinal
     */
    private static final String[] MESSAGES;


    static
    {
        // gotta use vector here for J2ME instead of ArrayList

        Vector messages = new Vector( 6 );
        messages.addElement( LOCKEDOUT_MSG );
        messages.addElement( RESYNCH_STARTING_MSG );
        messages.addElement( RESYNCH_INPROGRESS_MSG );
        messages.addElement( HOTPAUTH_FAILURE_MSG );
        messages.addElement( INACTIVE_MSG );
        messages.addElement( DISABLED_MSG );
        MESSAGES = new String[messages.size()];
        messages.copyInto( MESSAGES );
    }


    /**
     * Gets the error message associated with an ordinal value.
     *
     * @param ordinal the error message code
     * @return the error message
     */
    public static String getErrorMessage( int ordinal )
    {
        return MESSAGES[ordinal];
    }


    /**
     * Checks to see if a message has an embedded ordinal value.
     *
     * @param message the message containing an embedded ordinal value
     * @return true if the message has an ordinal value, false otherwise
     */
    public static boolean hasEmbeddedOrdinal( String message )
    {
        return message.indexOf( PREFIX ) != -1;
    }


    /**
     * Gets the embedded ordinal value from a message.
     *
     * @param message the message containing an embedded ordinal value
     * @return the ordinal value within the message
     */
    public static int getEmbeddedOrdinal( String message )
    {
        if ( !hasEmbeddedOrdinal( message ) )
        {
            StringBuffer buf = new StringBuffer();
            buf.append( "Message '" );
            buf.append( message );
            buf.append( "' does not contain embedded ordinal" );
        }

        String pastPrefix = stripPrefix( message, PREFIX );

        return Integer.parseInt( getPrefix( pastPrefix, ':' ) );
    }


    /**
     * Extracts the ordinal from a string that has it embedded.
     *
     * @param s the message with the ordinal embedded
     * @return the ordinal embedded within the message type
     */
    public static int getOrdinal( String s )
    {
        String pastPrefix = stripPrefix( s, PREFIX );
        return Integer.parseInt( getPrefix( pastPrefix, ']' ) );
    }


    private static String getPrefix( String str, char sep )
    {
        int index = -1;
        if ( ( index = str.indexOf( sep ) ) != -1 )
        {
            str = str.substring( 0, index );
        }
        return str;
    }


    private static String stripPrefix( String str, String prefix )
    {
        int index = str.indexOf( prefix );
        if ( index != -1 )
        {
            return str.substring( index + prefix.length() );
        }
        return str;
    }
}

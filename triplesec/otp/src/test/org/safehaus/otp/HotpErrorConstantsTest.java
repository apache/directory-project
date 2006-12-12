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


import junit.framework.TestCase;


/**
 * Tests a couple of the methods in {@link HotpErrorConstants}.
 *
 * @see HotpErrorConstants
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpErrorConstantsTest extends TestCase
{
    /**
     * Tests the {@link HotpErrorConstants#hasEmbeddedOrdinal(String)} method.
     */
    public void testHasEmbeddedOrdinal()
    {
        assertTrue( HotpErrorConstants.hasEmbeddedOrdinal( HotpErrorConstants.HOTPAUTH_FAILURE_MSG ) );

        assertTrue( HotpErrorConstants.hasEmbeddedOrdinal( HotpErrorConstants.LOCKEDOUT_MSG ) );

        assertTrue( HotpErrorConstants.hasEmbeddedOrdinal( HotpErrorConstants.RESYNCH_INPROGRESS_MSG ) );

        assertTrue( HotpErrorConstants.hasEmbeddedOrdinal( HotpErrorConstants.RESYNCH_STARTING_MSG ) );
    }


    /**
     * Tests the {@link HotpErrorConstants#getEmbeddedOrdinal(String)} method.
     */
    public void testGetEmbeddedOrdinal()
    {
        int ordinal = HotpErrorConstants.getEmbeddedOrdinal( HotpErrorConstants.HOTPAUTH_FAILURE_MSG );

        assertEquals( HotpErrorConstants.HOTPAUTH_FAILURE_VAL, ordinal );

        ordinal = HotpErrorConstants.getEmbeddedOrdinal( HotpErrorConstants.LOCKEDOUT_MSG );

        assertEquals( HotpErrorConstants.LOCKEDOUT_VAL, ordinal );

        ordinal = HotpErrorConstants.getEmbeddedOrdinal( HotpErrorConstants.RESYNCH_INPROGRESS_MSG );

        assertEquals( HotpErrorConstants.RESYNCH_INPROGRESS_VAL, ordinal );

        ordinal = HotpErrorConstants.getEmbeddedOrdinal( HotpErrorConstants.RESYNCH_STARTING_MSG );

        assertEquals( HotpErrorConstants.RESYNCH_STARTING_VAL, ordinal );

        String msg = "Generic error (description in e-text) (60) - HOTP-1";
        ordinal = HotpErrorConstants.getEmbeddedOrdinal( msg );
        assertEquals( HotpErrorConstants.RESYNCH_STARTING_VAL, ordinal );
    }


    /**
     * Tests the {@link HotpErrorConstants#getErrorMessage(int)} method.
     */
    public void testGetErrorMessage()
    {
        String message = HotpErrorConstants.getErrorMessage( HotpErrorConstants.HOTPAUTH_FAILURE_VAL );

        assertEquals( HotpErrorConstants.HOTPAUTH_FAILURE_MSG, message );

        message = HotpErrorConstants.getErrorMessage( HotpErrorConstants.LOCKEDOUT_VAL );

        assertEquals( HotpErrorConstants.LOCKEDOUT_MSG, message );

        message = HotpErrorConstants.getErrorMessage( HotpErrorConstants.RESYNCH_INPROGRESS_VAL );

        assertEquals( HotpErrorConstants.RESYNCH_INPROGRESS_MSG, message );

        message = HotpErrorConstants.getErrorMessage( HotpErrorConstants.RESYNCH_STARTING_VAL );

        assertEquals( HotpErrorConstants.RESYNCH_STARTING_MSG, message );
    }
}

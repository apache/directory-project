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
 * Tests the Hotp class' methods and the HOTP algorithm implementation.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev: 585 $
 */
public class HotpTest extends TestCase
{
    public static final byte[] SECRET = { '1', '2', '3', '4', '5', '6', '7', '8', '7', '9',
                                          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

    public void testGenerateDraftData() throws Exception
    {
        for( int ii = 0; ii < 10; ii++ )
        {
            assertEquals( OneTimePasswordAlgorithm.generateOTP( SECRET, ii, 6, 0 ), Hotp.generate( SECRET, ii, 6 ) );
        }
    }

    public void testDump() throws Exception
    {
        for( int ii = 0; ii < 10; ii++ )
        {
            StringBuffer buf = new StringBuffer();
            buf.append( ii );
            buf.append( " = " );
            buf.append( Hotp.generate( SECRET, ii, 6 ) );
            System.out.println( buf.toString() );
        }
    }
}

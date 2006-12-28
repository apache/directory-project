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
package org.safehaus.sms.clickatell;


import junit.framework.TestCase;

import java.io.IOException;

import org.safehaus.sms.SmsSession;
import org.safehaus.sms.SmsMessage;
import org.safehaus.sms.SmsException;


/**
 * Testcases for the ClickatellSmsSessionFactory.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ClickatellSmsSessionFactoryTest extends TestCase
{
    public void testPing() throws IOException, SmsException
    {
        ClickatellSmsSessionFactory factory = new ClickatellSmsSessionFactory();
        SmsSession session = factory.getSmsSession( "akarasulu", "pointer123", "679294" );
        assertNotNull( session );

        int errorCode = -1;
        //errorCode = factory.ping( session );
        //assertTrue( errorCode == 0 );
        errorCode = session.sendMessage( new SmsMessage( "19049826992", "Testing123" ) );
        assertTrue( errorCode == 0 );
    }
}

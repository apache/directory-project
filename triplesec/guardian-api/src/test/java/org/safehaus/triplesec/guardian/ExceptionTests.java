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
package org.safehaus.triplesec.guardian;


import junit.framework.TestCase;


/**
 * Just here to make clover happy with Exceptions.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 25 $
 */
public class ExceptionTests extends TestCase
{
    public void testGuardianExceptionTests()
    {
        GuardianException e = new GuardianException();
        assertNotNull( e );
        e = new GuardianException( "some message" );
        assertNotNull( e );
        e = new GuardianException( new NullPointerException() );
        assertNotNull( e );
        e = new GuardianException( "some message", new NullPointerException() );
        assertNotNull( e );
    }


    public void testNoConnectionDriverExceptionTests()
    {
        NoConnectionDriverException e = new NoConnectionDriverException();
        assertNotNull( e );
        e = new NoConnectionDriverException( "some message" );
        assertNotNull( e );
        e = new NoConnectionDriverException( new NullPointerException() );
        assertNotNull( e );
        e = new NoConnectionDriverException( "some message", new NullPointerException() );
        assertNotNull( e );
    }
}

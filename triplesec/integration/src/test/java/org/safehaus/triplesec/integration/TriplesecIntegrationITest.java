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
package org.safehaus.triplesec.integration;


import org.safehaus.triplesec.integration.TriplesecIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Test case to make sure the TriplesecUnit is working correctly.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecIntegrationITest extends TriplesecIntegration
{
    private static Logger log = LoggerFactory.getLogger( TriplesecIntegrationITest.class );


    public TriplesecIntegrationITest() throws Exception
    {
        super();
    }


    /**
     * Checks to see that init has done it's job
     */
    public void testInit()
    {
        log.info( "entered testInit()" );
        assertTrue( getServerHome().exists() );
        assertTrue( new File( getServerHome(), "logs" ).exists() );
        assertTrue( new File( getServerHome(), "conf" ).exists() );
        assertTrue( new File( getServerHome(), "var" ).exists() );

        File confDir = new File( getServerHome(), "conf" );
        assertTrue( new File( confDir, "server.xml" ).exists() );
    }
}

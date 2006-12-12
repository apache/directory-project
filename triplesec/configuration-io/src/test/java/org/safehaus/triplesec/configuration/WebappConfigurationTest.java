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
package org.safehaus.triplesec.configuration;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;


/**
 * Tests the ServerXmlUtils class.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class WebappConfigurationTest extends TestCase
{
    private static final Logger log = LoggerFactory.getLogger( WebappConfigurationTest.class );
    private File workingDirectory = null;

    
    public void setUp() throws Exception
    {
        String wkdirProp = System.getProperty( "workingDirectory" );
        log.debug( "workingDirectory system property = " + wkdirProp );
        if ( wkdirProp == null || wkdirProp.equals( "" ) )
        {
            wkdirProp = System.getProperty( "java.io.tmpdir" ) + File.separator + "target";
        }
        
        workingDirectory = new File( wkdirProp );
        if ( workingDirectory.exists() )
        {
            log.debug( "deleteing workingDirectory = " + workingDirectory );
            FileUtils.forceDelete( workingDirectory );
        }

        log.debug( "creating workingDirectory = " + workingDirectory );
        workingDirectory.mkdirs();
        super.setUp();
    }
    
    
    public void tearDown() throws Exception
    {
        workingDirectory = null;
        super.tearDown();
    }
    
    
    public void testSetInitParameterValue() throws Exception
    {
        Document initial = XmlUtils.readDocument( WebappConfigurationTest.class.getResource( getName() 
            + "Initial.xml" ) );
        WebappConfiguration config = new WebappConfiguration( initial );
        FilterConfiguration activation = config.getFilterConfiguration( "activation" );
        activation.setInitParameterValue( "smtpSubject", "blah blah bork bork" );
        checkDocument( initial );
    }
    
    
    public void testAddInitParameterValue() throws Exception
    {
        Document initial = XmlUtils.readDocument( WebappConfigurationTest.class.getResource( getName() 
            + "Initial.xml" ) );
        WebappConfiguration config = new WebappConfiguration( initial );
        FilterConfiguration activation = config.getFilterConfiguration( "activation" );
        activation.addInitParameter( "newInitParameter", "testing123" );
        checkDocument( initial );
    }
    
    
    public void testSetServletInitParameterValue() throws Exception
    {
        Document initial = XmlUtils.readDocument( WebappConfigurationTest.class.getResource( getName() 
            + "Initial.xml" ) );
        WebappConfiguration config = new WebappConfiguration( initial );
        ServletConfiguration servlet = config.getServletConfiguration( "TripleSecConfigApplication" );
        servlet.setInitParameterValue( "applicationClassName", "blah blah bork bork" );
        checkDocument( initial );
    }
    
    
    public void testAddServletInitParameterValue() throws Exception
    {
        Document initial = XmlUtils.readDocument( WebappConfigurationTest.class.getResource( getName() 
            + "Initial.xml" ) );
        WebappConfiguration config = new WebappConfiguration( initial );
        ServletConfiguration servlet = config.getServletConfiguration( "TripleSecConfigApplication" );
        servlet.addInitParameter( "newInitParameter", "testing123" );
        checkDocument( initial );
    }
    
    
    // -----------------------------------------------------------------------
    // Private Utility Methods 
    // -----------------------------------------------------------------------

    
    private void checkDocument( Document document ) throws IOException
    {
        File outputFile = new File( workingDirectory, getName() + ".xml" );
        log.debug( getName() + "(): outputing document to file: " + outputFile.getCanonicalPath() );
        XmlUtils.writeDocument( document, outputFile, OutputFormat.createPrettyPrint() );
        checkFile( getClass().getResource( getName() + ".xml" ), outputFile );
    }
    
    
    private void checkFile( URL expected, File generated ) throws IOException
    {
        InputStream generatedIn = new FileInputStream( generated );
        InputStream expectedIn = expected.openStream();
        while ( generatedIn.available() > 0 )
        {
            int generatedByte = generatedIn.read();
            int expectedByte = expectedIn.read();
            assertEquals( generatedByte, expectedByte );
        }
        expectedIn.close();
        generatedIn.close();
        log.debug( "generated output " + generated.getCanonicalPath() + " == expected output " + expected.toString() );
    }
}

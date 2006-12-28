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
package org.safehaus.triplesec.utils.hauskeys;


import java.io.File;

import org.safehaus.otp.HotpAttributes;
import org.safehaus.otp.HotpAttributesCipher;
import org.safehaus.profile.ProfileTestData;
import org.safehaus.profile.ServerProfile;

import junit.framework.TestCase;


/**
 * Tests the midlet builder.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HauskeysMidletBuilderTest extends TestCase
{
    File tmpDirectory = null;
    File hauskeysSrcFile = null;
    File dstDirectory = null;
    

    public HauskeysMidletBuilderTest()
    {
        tmpDirectory = new File( System.getProperty( "tmpDirectory" ) );
        hauskeysSrcFile = new File( System.getProperty( "hauskeysSrcFile" ) );
        dstDirectory = new File( System.getProperty( "dstDirectory" ) );
    }
    
    
    public void testBuild() throws Exception
    {
        HauskeysMidletBuilder builder = new HauskeysMidletBuilder();
        builder.setHauskeysDstFile( new File( dstDirectory, "testBuild.jar" ) );
        builder.setHauskeysSrcFile( hauskeysSrcFile );
        builder.setTmpDirectory( tmpDirectory );
        builder.setHotpInfo( "ssssssssssssssssssssssssss" );
        builder.setMidletName( "testBuild" );
        builder.build();
        assertTrue( new File( dstDirectory, "testBuild.jar" ).exists() );
    }
    
    
    public void testBuildDemoMidlets() throws Exception
    {
        for ( int ii = 0; ii < ProfileTestData.PROFILES.length; ii++ )
        {
            ServerProfile profile = ProfileTestData.PROFILES[ii];
            HotpAttributes attrs = new HotpAttributes( 6, profile.getFactor(), profile.getSecret() );
            String hotpInfo = HotpAttributesCipher.encrypt( profile.getTokenPin(), attrs );
            HotpAttributes decrypted = HotpAttributesCipher.decrypt( "1234", hotpInfo );
            
            assertEquals( attrs.getFactor(), decrypted.getFactor() );
            assertEquals( attrs.getSize(), decrypted.getSize() );

            HauskeysMidletBuilder builder = new HauskeysMidletBuilder();
            builder.setHauskeysDstFile( new File( dstDirectory, profile.getLabel() + ".jar" ) );
            builder.setHauskeysSrcFile( hauskeysSrcFile );
            builder.setTmpDirectory( tmpDirectory );
            builder.setHotpInfo( hotpInfo );
            builder.setMidletName( profile.getLabel() );
            builder.build();
            assertTrue( new File( dstDirectory, profile.getLabel() + ".jar" ).exists() );
        }
    }
}

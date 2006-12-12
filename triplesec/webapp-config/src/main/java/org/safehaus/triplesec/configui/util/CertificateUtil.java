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
package org.safehaus.triplesec.configui.util;


import java.io.File;

import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.apache.tools.ant.taskdefs.Execute;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;


public class CertificateUtil
{
    public static void create( File certFile, TriplesecConfigSettings settings ) throws Exception
    {
        File keytool = new File( new File( new File( System.getProperty( "java.home" ) ), "bin" ), "keytool" );
        File parentDirectory = certFile.getParentFile();
        if ( ! parentDirectory.exists() )
        {
            parentDirectory.mkdirs();
        }

        String baseDn = NamespaceTools.inferLdapName( settings.getPrimaryRealmName().toLowerCase() );
        String[] args = new String[] { 
            keytool.getAbsolutePath(), "-genkey", "-alias", "default",
            "-keyalg", "RSA", "-dname", baseDn, "-keypass", settings.getLdapCertPassword(), 
            "-storepass", settings.getLdapCertPassword(), "-keystore", settings.getLdapCertFilePath()
        };
        Execute exec = new Execute();
        exec.setCommandline( args );
        exec.setWorkingDirectory( parentDirectory );
        exec.execute();
    }
}

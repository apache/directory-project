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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Random;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.Move;


/**
 * Bean which gathers properties and builds a hauskeys midlet using a template jar.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HauskeysMidletBuilder 
{
    private String midletName;
    private String hotpInfo;
    private File tmpDirectory = new File( System.getProperty( "java.io.tmpdir" ) );
    private File hauskeysSrcFile;
    private File hauskeysDstFile;
    
	
    public void build() throws IOException, ManifestException
    {
        File mydir = new File( tmpDirectory, "hauskeys" );
        if ( ! mydir.exists() )
        {
        	mydir.mkdirs();
        }
        
        // compute a random string for destination directory
        StringBuffer buf = new StringBuffer();
        Random rand = new Random();
        while ( buf.length() <= 8 )
        {
            int ch = rand.nextInt() % 123;
            if ( ( ch > 64 && ch < 91 ) || ( ch > 96 && ch < 123 ) || ( ch > 47 && ch < 58 ) )
            {
                buf.append( ( char ) ch );
            }
        }
        String destDirName = buf.append( "." ).append( System.currentTimeMillis() ).toString();

        // create the temp directory destination and unjar
        File destDir = new File( mydir, destDirName );
        Project project = new Project();
        Expand expand = new Expand();
        expand.setProject( project );
        expand.setDest( destDir );
        expand.setSrc( hauskeysSrcFile );
        expand.execute();
        
        // remove the old HOTP-INFO file to create new one later
        File hotpInfoFile = getHotpInfoFile( destDir );
        Delete delete = new Delete();
        delete.setProject( project );
        delete.setFile( hotpInfoFile );
        delete.execute();
        
        // write new file with new content
        OutputStream out = new FileOutputStream( hotpInfoFile );
        out.write( hotpInfo.getBytes( "UTF-8" ) );
        out.flush();
        out.close();
        
        // read manifest file in for modifications
        File manifestFile = new File( new File( destDir, "META-INF" ), "MANIFEST.MF" );
        Manifest manifest = new Manifest( new FileReader( manifestFile ) );
        delete = new Delete();
        delete.setProject( project );
        delete.setFile( manifestFile );
        delete.execute();
        
        // modify manifest for midlet name and dump it back out
        manifest.getMainSection().getAttribute( "MIDlet-Name" ).setValue( midletName );
        PrintWriter pw = new PrintWriter( new FileWriter( manifestFile ) );
        manifest.write( pw );
        pw.flush();
        pw.close();
     
        // jar up contents of the temp directory
        Jar jar = new Jar();
        jar.setProject( project );
        jar.setBasedir( destDir );
        jar.setManifest( manifestFile );
        File jarFile = new File( mydir, destDirName + ".jar" );
        jar.setDestFile( jarFile );
        jar.execute();
    
        // move the generate jar file to the destFile
        Move mv = new Move();
        mv.setProject( project );
        mv.setFile( jarFile );
        mv.setFailOnError( true );
        mv.setTofile( hauskeysDstFile );
        mv.execute();
        
        // delete the temporary jarFile and tempDir
        delete = new Delete();
        delete.setProject( project );
  	    delete.setFile( jarFile );
	    delete.execute();
	    delete = new Delete();
	    delete.setProject( project );
	    delete.setDir( destDir );
	    delete.execute();
    }
    
    
    private static File getHotpInfoFile( File jarBase )
    {
    	File f = new File( jarBase, "org" );
    	f = new File( f, "safehaus" );
    	f = new File( f, "midlets" );
    	f = new File( f, "HOTP-INFO" );
    	return f;
    }

    
    public void setHotpInfo( String hotpInfo )
    {
        this.hotpInfo = hotpInfo;
    }
    
    
    public String getHotpInfo()
    {
        return hotpInfo;
    }
    

    public void setMidletName( String midletName )
    {
        this.midletName = midletName;
    }


    public String getMidletName()
    {
        return midletName;
    }


    public void setTmpDirectory( File tmpDirectory )
    {
        this.tmpDirectory = tmpDirectory;
    }


    public File getTmpDirectory()
    {
        return tmpDirectory;
    }


    public void setHauskeysSrcFile( File hauskeysSrcFile )
    {
        this.hauskeysSrcFile = hauskeysSrcFile;
    }


    public File getHauskeysSrcFile()
    {
        return hauskeysSrcFile;
    }


    public void setHauskeysDstFile( File hauskeysDstFile )
    {
        this.hauskeysDstFile = hauskeysDstFile;
    }


    public File getHauskeysDstFile()
    {
        return hauskeysDstFile;
    }
}

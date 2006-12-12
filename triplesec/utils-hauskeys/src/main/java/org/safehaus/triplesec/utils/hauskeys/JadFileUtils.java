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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Utility class for generating a JAD file.
 * 
 * <h3>Usage</h3>
 * <p>
 *   Use this utility class to generate or modify existing jad files like so:
 * </p>
 * 
 * <pre>
 *    Map jadAttrs = JadFileUtils.getJadAttributes( jadFile );
 *    jadAttrs.put( "Midlet-Name", "YourHauskeys" );
 *    jadAttrs.put( "Midlet-Jar-URL", "HausKeys.jar" );
 *    jadAttrs.put( "Midlet-Jar-Size", String.valueOf( jarFile.length() ) );
 *    String jadContent = JadFileUtils.getJadContentFor( jadAttrs );
 *    FileWriter fw = new FileWriter( newJadFile );
 *    fw.write( jadContent );
 *    fw.flush();
 *    fw.close();
 * </pre>
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class JadFileUtils 
{
    public static final String JAD_CONTENT_TYPE = "text/vnd.sun.j2me.app-descriptor";
    

	public static String getJadContentFor( Map attributes )
	{
		StringBuffer buf = new StringBuffer();
		Iterator list = attributes.keySet().iterator();
		boolean isFirstPass = true;
		while ( list.hasNext() )
		{
			String key = ( String ) list.next();
			String value = String.valueOf( attributes.get( key ) );
			if ( isFirstPass )
			{
				isFirstPass = false;
				buf.append( key );
				buf.append( ": " );
				buf.append( value );
			}
			else
			{
				buf.append( "\n" );
				buf.append( key );
				buf.append( ": " );
				buf.append( value );
			}
 		}
		return buf.toString();
	}
	
	
	public static Map getJadAttributes( File jadFile ) throws IOException
	{
		Map attributes = new HashMap();
		BufferedReader in = new BufferedReader( new FileReader( jadFile ) );
		String line = null;
		while ( ( line = in.readLine() ) != null )
		{
			String[] tuple = line.split( ":" );
			attributes.put( tuple[0].trim(), tuple[1].trim() );
		}
		return attributes;
	}
}

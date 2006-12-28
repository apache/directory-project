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
package org.safehaus.triplesec.activation;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.Mkdir;

import org.safehaus.otp.HotpAttributes;
import org.safehaus.otp.HotpAttributesCipher;
import org.safehaus.triplesec.utils.hauskeys.HauskeysMidletBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility functions for activating an account.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ActivationUtils 
{
	/** logger for this class */
	private static final Logger log = LoggerFactory.getLogger( ActivationUtils.class );
	/** index of the act key when spliting the request URI */
	private static final int ACTKEY_INDEX = 2;
	
	
	/**
	 * Extracts the activation key from a request's URI string.
	 * 
	 * @param requestURI the request's URI string
	 * @return the safehaus activation key
	 * @throws IllegalArgumentException if the request URI is null or does not contain "/"
	 */
	public static String getActivationKey( String requestURI )
	{
		if ( requestURI == null || requestURI.indexOf( "/" ) == -1  || !requestURI.startsWith( "/" ) ) 
		{
			throw new IllegalArgumentException( "Not a valid URI string: " + requestURI );
		}
		
		String[] comps = requestURI.split( "/" );
		return comps[ACTKEY_INDEX];
	}
	
	
	public static File createActivationDirectory( File activationDirectory )
	{
		Project project = new Project();
		
		// if old directory exists blow it away
		if ( activationDirectory.exists() )
		{
			Delete delete = new Delete();
			delete.setProject( project );
			delete.setDir( activationDirectory );
			delete.execute();
		}
		
		Mkdir mkdir = new Mkdir();
		mkdir.setProject( project );
		mkdir.setDir( activationDirectory );
	    mkdir.execute();
	    return activationDirectory;
	}
	
	
	public static void deleteActivationDirectory( File activationDirectory )
	{
		if ( activationDirectory == null )
		{
			throw new IllegalArgumentException( "activationDirectory cannot be null" );
		}
		
		Project project = new Project();
		Delete delete = new Delete();
		delete.setProject( project );
		delete.setDir( activationDirectory );
		delete.execute();
	}
	
	
    public static HotpAccount getAccountHotpAccount( DirContext ctx, String activationKey ) throws NamingException
	{
		NamingEnumeration list = null;
		
        try
        {
            list = ctx.search( "", new BasicAttributes( "safehausActivationKey", activationKey, true ) );
            if ( list.hasMore() )
            {
            	SearchResult result = ( SearchResult ) list.next();
            	Attributes entry = result.getAttributes();
                HotpAccountModifier modifier = new HotpAccountModifier();
                modifier.setCarrier( ( String ) entry.get( "safehausMobileCarrier" ).get() );
                modifier.setCellularNumber( ( String ) entry.get( "mobile" ).get() );
                modifier.setDeliveryMethodSms( ( String ) entry.get( "safehausNotifyBy" ).get() );
                modifier.setEmailAddress( ( String ) entry.get( "mail" ).get() );
                modifier.setFactor( Long.parseLong( ( String ) entry.get( "safehausFactor" ).get() ) );
                modifier.setPin( ( String ) entry.get( "safehausTokenPin" ).get() );
                modifier.setSecret( entry.get( "safehausSecret" ).get() );
                modifier.setMidletName( ( String ) entry.get( "safehausMidletName" ).get() );
                modifier.setName( result.getName() );
                modifier.setRealm( ( String ) entry.get( "safehausRealm" ).get() );
            	return modifier.create();
            }
        }
        finally
        {
            if ( list != null ) { try { list.close(); } catch ( Exception e ){ log.error( "can't close enum" ); } };
        }
        
		return null;
	}
    

    public static String getBogusHotpInfo()
    {
        // max length 16, min length 8 bytes
        int length = 8 + RandomUtils.nextInt() % 8;
        byte[] secret = RandomStringUtils.random( length ).getBytes();
        HotpAttributes hotpAttrs = new HotpAttributes( 6, RandomUtils.nextLong(), secret );
        try 
        {
			return HotpAttributesCipher.encrypt( RandomStringUtils.randomAlphanumeric(4), hotpAttrs );
		} 
        catch ( UnsupportedEncodingException e ) 
        {
        	log.error( "could not make hotp info", e );
        	return null;
		}
    }
    
    
    public static void buildMidlet( String midletName, String hotpInfo, File appSrc, File appDest ) 
        throws IOException, ManifestException
    {
        File tmp = new File( System.getProperty( "java.io.tmpdir" ) );
        HauskeysMidletBuilder builder = new HauskeysMidletBuilder();
        builder.setTmpDirectory( tmp );
        builder.setHauskeysSrcFile( appSrc );
        builder.setHauskeysDstFile( appDest );
        builder.setHotpInfo( hotpInfo );
        builder.setMidletName( midletName );
        builder.build();
    }
}

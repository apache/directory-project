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
package org.safehaus.triplesec.guardian.ldif;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.ConnectionDriver;
import org.safehaus.triplesec.guardian.GuardianException;


/**
 * A simple LDIF file based driver for guardian.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 62 $
 */
public class LdifConnectionDriver implements ConnectionDriver
{
    static
    {
        ApplicationPolicyFactory.registerDriver( new LdifConnectionDriver() );
    }

    
    public LdifConnectionDriver()
    {
    }

    
    public boolean accept( String url )
    {
        if ( ( url.startsWith( "file:" ) || url.startsWith( "jar:" ) ) && url.endsWith( ".ldif" ) )
        {
            return true;
        }

        return false;
    }
    

    public ApplicationPolicy newStore( String url, Properties info ) throws GuardianException
    {
        if ( info == null )
        {
            info = new Properties();
        }

        if ( url == null )
        {
            throw new IllegalArgumentException( "A non-null url must be provided." );
        }

        String application = info.getProperty( "applicationPrincipalDN" );
        if ( application == null )
        {
            throw new IllegalArgumentException( "An applicationPrincipalDN property value must be provided." );
        }
        
        if ( url.startsWith( "file:" ) )
        {
            File ldifFile = null;
            try
            {
                ldifFile = new File( new URL( url ).getPath() );
            }
            catch ( MalformedURLException e )
            {
                throw new GuardianException( "Malformed LDIF file URL: " + url );
            }
            return new LdifApplicationPolicy( ldifFile, info );
        }
        else if ( url.startsWith( "jar:" ) )
        {
            throw new NotImplementedException();
        }
        
        throw new GuardianException( "Unrecognized URL scheme for " );
    }
}

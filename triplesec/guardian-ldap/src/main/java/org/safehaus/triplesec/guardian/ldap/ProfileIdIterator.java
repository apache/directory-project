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
package org.safehaus.triplesec.guardian.ldap;


import java.util.Iterator;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An iterator over a NamingEnumeration containing the results of an LDAP 
 * search over all profiles within an ApplicationPolicy.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ProfileIdIterator implements Iterator
{
    private final static Logger log = LoggerFactory.getLogger( ProfileIdIterator.class );
    private final NamingEnumeration underlying;
    private String prefetchedProfileId;
    
    
    ProfileIdIterator( NamingEnumeration underlying ) throws NamingException
    {
        this.underlying = underlying;
        prefetch();
    }

    
    private void prefetch() throws NamingException
    {
        if ( underlying.hasMore() )
        {
            SearchResult result = ( SearchResult ) underlying.next();
            if ( result.getAttributes().get( "profileId" ) != null )
            {
                prefetchedProfileId = ( String ) result.getAttributes().get( "profileId" ).get();
            }
        }
        else
        {
            prefetchedProfileId = null;
        }
    }
    
    
    public boolean hasNext()
    {
        return prefetchedProfileId != null;
    }

    
    public Object next()
    {
        String retval = prefetchedProfileId;
        try
        {
            prefetch();
        }
        catch ( NamingException e )
        {
            log.error( "Premature truncation of underlying naming enumeration.", e );
            if ( underlying != null ) 
            {
                try
                {
                    underlying.close();
                }
                catch ( NamingException e1 )
                {
                    log.error( "Failed to properly close the underlying naming enumeration.", e1 );
                }
            }
        }
        return retval;
    }

    
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}

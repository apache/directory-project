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
package org.safehaus.triplesec.admin.dao.ldap;


import java.util.Iterator;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An iterator over a NamingEnumeration containing the results of an LDAP 
 * search. 
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class JndiIterator implements Iterator
{
    private final static Logger log = LoggerFactory.getLogger( JndiIterator.class );
    private final NamingEnumeration underlying;
    private final LdapDao dao;
    private final String id;

    private Object prefetched;
    private String prefetchedRdn;
    private String lastRdn;
    private Object extra;
    
    
    JndiIterator( LdapDao dao, String id, NamingEnumeration underlying, Object extra ) throws NamingException
    {
        this.id = id;
        this.dao = dao;
        this.extra = extra;
        this.underlying = underlying;
        prefetch();
    }

    
    JndiIterator( LdapDao dao, NamingEnumeration underlying, Object extra ) throws NamingException
    {
        this.id = null;
        this.dao = dao;
        this.extra = extra;
        this.underlying = underlying;
        prefetch();
    }

    
    private void prefetch() throws NamingException
    {
        if ( underlying.hasMore() )
        {
            SearchResult result = ( SearchResult ) underlying.next();
            if ( id != null )
            {
                if ( result.getAttributes().get( id ) != null )
                {
                    prefetched = result.getAttributes().get( id ).get();
                }
            }
            else 
            {
                if ( log.isDebugEnabled() )
                {
                    log.debug( "Iterator prefetched " + result.getName() + " with attributes:\n" 
                        + result.getAttributes() );
                }
                prefetched = dao.getEntryObject( extra, result.getAttributes() );
            }
    
            prefetchedRdn = result.getName();
        }
        else
        {
            prefetched = null;
        }
    }
    
    
    public boolean hasNext()
    {
        return prefetched != null;
    }

    
    public Object next()
    {
        Object retval = prefetched;
        lastRdn = prefetchedRdn;
        
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
        if ( lastRdn == null )
        {
            throw new IllegalStateException( "next() has not been called." );
        }
        
        if ( dao != null )
        {
            dao.deleteEntry( lastRdn );
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}

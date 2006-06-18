/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.directory.server.core.schema.bootstrap;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.server.core.schema.ComparatorRegistry;
import org.apache.directory.server.core.schema.SerializableComparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple POJO implementation of the ComparatorRegistry service interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BootstrapComparatorRegistry implements ComparatorRegistry
{
    /** static class logger */
    private final static Logger log = LoggerFactory.getLogger( BootstrapComparatorRegistry.class );
    /** the comparators in this registry */
    private final Map comparators;
    /** maps an OID to a schema name*/
    private final Map oidToSchema;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------

    /**
     * Creates a default ComparatorRegistry by initializing the map and the
     * montior.
     */
    public BootstrapComparatorRegistry()
    {
        this.oidToSchema = new HashMap();
        this.comparators = new HashMap();
        SerializableComparator.setRegistry( this );
    }


    // ------------------------------------------------------------------------
    // Service Methods
    // ------------------------------------------------------------------------

    
    public void register( String schema, String oid, Comparator comparator ) throws NamingException
    {
        if ( comparators.containsKey( oid ) )
        {
            NamingException e = new NamingException( "Comparator with OID " + oid + " already registered!" );
            throw e;
        }

        oidToSchema.put( oid, schema );
        comparators.put( oid, comparator );
        if ( log.isDebugEnabled() )
        {
            log.debug( "registed comparator with OID: " + oid );
        }
    }


    public Comparator lookup( String oid ) throws NamingException
    {
        if ( comparators.containsKey( oid ) )
        {
            Comparator c = ( Comparator ) comparators.get( oid );
            if ( log.isDebugEnabled() )
            {
                log.debug( "looked up comparator with OID: " + oid );
            }
            return c;
        }

        NamingException e = new NamingException( "Comparator not found for OID: " + oid );
        throw e;
    }


    public boolean hasComparator( String oid )
    {
        return comparators.containsKey( oid );
    }


    public String getSchemaName( String oid ) throws NamingException
    {
        if ( !Character.isDigit( oid.charAt( 0 ) ) )
        {
            throw new NamingException( "OID " + oid + " is not a numeric OID" );
        }

        if ( oidToSchema.containsKey( oid ) )
        {
            return ( String ) oidToSchema.get( oid );
        }

        throw new NamingException( "OID " + oid + " not found in oid to " + "schema name map!" );
    }
}
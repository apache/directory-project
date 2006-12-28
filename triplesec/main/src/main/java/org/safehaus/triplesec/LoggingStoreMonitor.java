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
package org.safehaus.triplesec;


import javax.naming.directory.Attributes;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.safehaus.profile.ServerProfile;
import org.safehaus.triplesec.store.ServerProfileStore;
import org.safehaus.triplesec.store.StoreMonitorAdapter;


/**
 * A log4j based logging StoreMonitor.
 *
 * @author <a href="mailto:directory-dev@incubator.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class LoggingStoreMonitor extends StoreMonitorAdapter
{
    /** underlying logger used for logging store events */
    private final Log log;


    public LoggingStoreMonitor( Log log )
    {
        if ( log == null )
        {
            this.log = LogFactory.getLog( "StoreMonitor" );

            return;
        }

        this.log = log;
    }


    public LoggingStoreMonitor()
    {
        this.log = LogFactory.getLog( "StoreMonitor" );
    }


    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------


    public void profileAdded( ServerProfileStore store, ServerProfile profile )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "store added profile " + profile.getUserId() + " to realm " + profile.getRealm() );
        }
    }


    public void info( ServerProfileStore store, String s )
    {
        log.info( s );
    }


    public void profileImported( ServerProfileStore store, String dn, Attributes attributes )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "store imported profile - " + dn + " with attributes " + attributes );
        }
    }


    public void profileNotImported( ServerProfileStore store, String dn, Attributes attributes )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "store did not imported profile - " + dn + " because it already existed" );
        }
    }


    /*
    public void profileDeleted( ServerProfileStore store, KerberosPrincipal principal, ServerProfile profile )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "store deleted profile " + profile.getUserId() + " from realm " + profile.getRealm() );
        }
    }


    public void profileAccessed( ServerProfileStore store, KerberosPrincipal principal, ServerProfile profile )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "store accessed profile " + profile.getUserId() + " in realm " + profile.getRealm() );
        }
    }


    public void profileUpdated( ServerProfileStore store, ServerProfile profile, ModificationItem[] mods )
    {
        String report = getModificationReport( mods );

        log.info( "store updated profile " + profile.getUserId() + " in realm " + profile.getRealm() + ": " + report );
    }


    private String getModificationType( ModificationItem item )
    {
        switch( item.getModificationOp() )
        {
            case( DirContext.REMOVE_ATTRIBUTE ):

                return "remove";

            case( DirContext.REPLACE_ATTRIBUTE ):

                return "replace";

            case( DirContext.ADD_ATTRIBUTE ):

                return "add";

            default:
                throw new IllegalArgumentException( "unknown mod type" );
        }
    }


    private String getModificationReport( ModificationItem[] mods )
    {
        StringBuffer buf = new StringBuffer();

        for ( int ii = 0; ii < mods.length; ii++ )
        {
            buf.append( "modop: " ).append( getModificationType( mods[ii] ) );

            buf.append( "attribute: " ).append( mods[ii].getAttribute() );
        }

        return buf.toString();
    }
    */

    public void storeInitialized( ServerProfileStore store )
    {
        log.info( "store initialized" );
    }


    public void storeFailure( ServerProfileStore store )
    {
        log.error( "store failed - no further info available" );
    }


    public void storeFailure( ServerProfileStore store, Throwable t )
    {
        log.error( "store failed - " + t.getMessage(), t );
    }


    public void storeFailure( ServerProfileStore store, KerberosPrincipal principal )
    {
        log.error( "store failed - operation for principal " + principal.getName() );
    }


    public void storeFailure( ServerProfileStore store, KerberosPrincipal principal, Throwable t )
    {
        log.error( "store failed - on principal " + principal.getName() + " - " + t.getMessage(), t );
    }


    public void storeFailure( ServerProfileStore store, KerberosPrincipal principal, String s )
    {
        log.error( "store failed - on principal " + principal.getName() + " - " + s );
    }
}

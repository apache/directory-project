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
package org.safehaus.triplesec.store;


import javax.security.auth.kerberos.KerberosPrincipal;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.Attributes;

import org.safehaus.profile.ServerProfile;
import org.safehaus.triplesec.store.ServerProfileStore;
import org.safehaus.triplesec.store.StoreMonitor;


/**
 * An adapter for the StoreMonitor interface.
 *
 * @author <a href="mailto:directory-dev@incubator.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class StoreMonitorAdapter implements StoreMonitor
{
    public void profileAdded( ServerProfileStore store, ServerProfile profile )
    {
    }


    public void profileDeleted( ServerProfileStore store, ServerProfile profile )
    {
    }


    public void profileAccessed( ServerProfileStore store, ServerProfile profile )
    {
    }


    public void profileUpdated( ServerProfileStore store, ServerProfile profile, ModificationItem[] mods )
    {
    }


    public void storeInitialized( ServerProfileStore store )
    {
    }


    public void storeFailure( ServerProfileStore store )
    {
    }


    public void storeFailure( ServerProfileStore store, Throwable t )
    {
        if ( t == null )
        {
            return;
        }

        t.printStackTrace( System.err );
    }


    public void storeFailure( ServerProfileStore store, KerberosPrincipal principal )
    {
    }


    public void storeFailure( ServerProfileStore store, KerberosPrincipal principal, Throwable t )
    {
        if ( t == null )
        {
            return;
        }

        t.printStackTrace( System.err );
    }


    public void profileImported( ServerProfileStore store, String dn, Attributes attributes )
    {
    }


    public void profileNotImported( ServerProfileStore store, String dn, Attributes attributes )
    {
    }


    public void info( ServerProfileStore store, String s )
    {
    }


    public void storeFailure( ServerProfileStore store, KerberosPrincipal principal, String s )
    {
    }
}

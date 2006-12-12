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


import javax.naming.NamingException;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.safehaus.profile.ServerProfile;


/**
 * A server store's interface.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public interface ServerProfileStore
{
    /** the krb5kdc schema principal name for a krb5KDCEntry */
    String PRINCIPAL_ATTR = "krb5PrincipalName";

    /**
     * The store might need to be initialized before use.  We recommend calling
     * this method before making calls to other methods.
     */
    public void init() throws NamingException;

    /**
     * Gets a server's Hotp account profile.
     *
     * @param principal the KerberosPrincipal associated with the profile
     * @return the Hotp ServerProfile
     * @throws javax.naming.NamingException if the Profile cannot be accessed
     */
    ServerProfile getProfile( KerberosPrincipal principal ) throws NamingException;

    /**
     * Checks to see if a Profile exists within a realm in the Profile store.
     *
     * @param principal the KerberosPrincipal associated with the profile
     * @return true if the Profile exists within the store, false if it does not
     * @throws javax.naming.NamingException if the store is not available
     */
    public boolean hasProfile( KerberosPrincipal principal  ) throws NamingException;

    /**
     * Adds a hotp account Profile to the store.
     *
     * @param profile the account ServerProfile to add
     * @throws javax.naming.NamingException if the Profile cannot be added
     */
    public void add( ServerProfile profile ) throws NamingException;

    /**
     * Deletes an item by name within the list and on the store.
     *
     * @param principal the KerberosPrincipal associated with the profile
     * @throws javax.naming.NamingException if the ServerProfile cannot be deleted
     */
    public void delete( KerberosPrincipal principal ) throws NamingException;

    /**
     * Updates the store by removing the old copy of the profile and creating
     * the new one.  So really this is a simple delete and add operation.
     *
     * @param oldPrincipal the original KerberosPrincipal for the profile
     * @param updated the altered profile
     */
    public void update( KerberosPrincipal oldPrincipal, ServerProfile updated ) throws NamingException;

    /**
     * Sets the monitor whose callbacks are invoked to notify of events in the
     * store.
     *
     * @param monitor the monitor to set for the store
     */
    public void setMonitor( StoreMonitor monitor );
}

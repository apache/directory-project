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
package org.safehaus.triplesec.guardian;


import java.security.Permissions;
import java.util.Iterator;
import java.util.Set;



/**
 * The policy store for an application whose access policy is managed by Triplesec.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @author Trustin Lee
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @version $Rev: 72 $, $Date: 2005-11-07 21:37:46 -0500 (Mon, 07 Nov 2005) $
 */
public interface ApplicationPolicy
{
    /**
     * Removes a change listener so that it does not recieve policy change 
     * notifications.
     * 
     * @param listener the listener to remove.
     */
    boolean removePolicyListener( PolicyChangeListener listener ) throws GuardianException;
    
    /**
     * Adds a change listener so that it recieves policy change notifications.
     * 
     * @param listener the listener to add.
     */
    boolean addPolicyListener( PolicyChangeListener listener ) throws GuardianException;
    
    /** 
     * Gets the name uniquely identifying the applicaiton associated
     * with this store.
     * 
     * @return the name of this store
     */
    String getApplicationName();
    
    /**
     * Gets a set of {@link Role}s defined for this store.
     * 
     * @return a set of {@link Role}s defined for this store.
     */
    Roles getRoles();
    
    /**
     * Gets a set of {@link StringPermission}s defined for this store.
     * 
     * @return a set of {@link StringPermission}s defined for this store.
     */
//    Permissions getPermissions();
    
    /**
     * Gets the names of the profiles dependent on a role. The set contains
     * Strings of the profile name.
     * 
     * @param role the role the dependent profiles are associated with
     * @return the name's of profiles that depend on the supplied role
     * @throws GuardianException if there is an error accessing the backing 
     * store or the role is not associated with this ApplicationPolicy
     */
    Set getDependentProfileNames( Role role ) throws GuardianException;
    
    /**
     * Gets the names of the profiles dependent on a permission.  The set 
     * contains Strings of the profile names.
     * 
     * @param permission the permission the dependent profiles are associated with
     * @return the name's of profiles that depend on the supplied permission
     * @throws GuardianException if there is an error accessing the backing 
     * store or the permission is not associated with this ApplicationPolicy
     */
    Set getDependentProfileNames( StringPermission permission ) throws GuardianException;
    
    /**
     * Gets the set of profiles a user has for this ApplicationPolicy.
     * 
     * @param userName the name of the user to get the profile ids for
     * @return a set of profile ids as Strings or the empty set if the userName is 
     * invalid or does not have profiles defined
     * @throws GuardianException if there are errors accessing the backing store
     */
    Set getUserProfileIds( String userName ) throws GuardianException;
    
    /**
     * Gets an iterator over the set of profiles in this ApplicationPolicy.
     * 
     * @return an iterator over profileId Strings
     * @throws GuardianException if there are errors accessing the backing store
     */
    Iterator getProfileIdIterator() throws GuardianException;

    /**
     * Gets this user's authorization {@link Profile} for the application.
     *
     * @param profileId the name of the user to get the {@link Profile} for
     * @return the {@link Profile} for the application or null if no profile exists for
     *      the specified <tt>profileId</tt>
     */
    Profile getProfile( String profileId ) throws GuardianException;
    
    /**
     * Gets a profile for the admin user which is in all roles and has all permissions
     * granted.
     * 
     * @return the admin user profile with all rights
     */
//    Profile getAdminProfile();

    /**
     * Gets a breif description of this ApplicationPolicy.
     *
     * @return a breif description of this ApplicationPolicy
     */
    String getDescription();

    /**
     * Closes the application store.
     *
     * @throws GuardianException if the store cannot be properly closed.
     */
    void close() throws GuardianException;
}

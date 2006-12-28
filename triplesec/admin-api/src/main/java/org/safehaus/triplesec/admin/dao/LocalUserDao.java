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
package org.safehaus.triplesec.admin.dao;


import java.util.Date;
import java.util.Iterator;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.LocalUser;


public interface LocalUserDao
{
    public LocalUser add( String id, String description, String firstName, 
        String lastName, String password, String address1, String address2, String city, String stateProvRegion, 
        String zipPostalCode, String country, String company, String email, boolean disabled ) 
        throws DataAccessException;

    public LocalUser rename( String newId, LocalUser archetype ) throws DataAccessException;

    public LocalUser modify( String creatorsName, Date createTimestamp, String id, 
        String description, String pasword, String firstName, String lastName, 
        String address1, String address2, String city, String stateProvRegion, 
        String zipPostalCode, String country, String company, String email, 
        boolean disabled, ModificationItem[] mods ) throws DataAccessException;

    public void delete( String id ) throws DataAccessException;

    public LocalUser load( String id ) throws DataAccessException;
    
    public Iterator iterator() throws DataAccessException;
}
 
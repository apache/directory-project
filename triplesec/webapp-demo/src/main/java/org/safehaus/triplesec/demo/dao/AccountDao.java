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
package org.safehaus.triplesec.demo.dao;

import org.safehaus.triplesec.demo.model.Account;

import java.util.Iterator;

/**
 * Interface for retrieving investment accounst from a database.
 */
public interface AccountDao
{
    /**
     * @return total number of accounts available
     */
    int count();

    /**
     * @param qp sorting and paging info
     * @return iterator over accounts
     */
    Iterator find( QueryParam qp );

    /**
     * @param uid user identifier for the account
     * @return account with matching uid
     */
    Account get( String uid );

    /**
     * @param account to update
     */
    void update( Account account );
}

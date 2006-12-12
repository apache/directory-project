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
package org.safehaus.triplesec.demo.dao.mock;

import org.safehaus.triplesec.demo.dao.AccountDao;
import org.safehaus.triplesec.demo.dao.QueryParam;
import org.safehaus.triplesec.demo.model.Account;
import org.safehaus.triplesec.demo.service.Registry;
import org.safehaus.triplesec.guardian.ApplicationPolicy;

import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A Dao implementation with an auto-generated embedded database. In
 * a true implementation, this Dao would interface with a real database,
 * but because we want to keep dependencies to a minimum, we generate our
 * own database here.
 */
public class MockAccountDao implements AccountDao
{
    private Map map = Collections.synchronizedMap( new HashMap() );
    private List uidIdx = Collections.synchronizedList( new ArrayList() );
    private List uidDescIdx = Collections.synchronizedList( new ArrayList() );

    public MockAccountDao()
    {
        ApplicationPolicy policy = Registry.policyManager().getAppPolicy();
        for ( Iterator iter = policy.getProfileIdIterator(); iter.hasNext(); )
        {
            add( new Account( (String) iter.next() ) );
        }
        updateIndecies();
    }

    /**
     * @return total number of accounts available
     */
    public int count()
    {
        return uidIdx.size();
    }

    /**
     * @param qp sorting and paging info
     * @return iterator over accounts
     */
    public Iterator find(QueryParam qp)
    {
        List sublist = getIndex( qp.getSort(), qp.isSortAsc() )
                .subList( qp.getFirst(), qp.getFirst() + qp.getCount() );
        return sublist.iterator();
    }

    protected List getIndex( String prop, boolean asc )
    {
        if ( prop == null )
        {
            return uidIdx;
        }
        return (asc) ? uidIdx : uidDescIdx;
    }

    /**
     * Find account by user ID.
     *
     * @param uid user identifier for the account
     * @return account with matching uid
     */
    public Account get( String uid )
    {
        Account accnt = (Account) map.get( uid );
        if ( accnt == null )
        {
            // hmmmmmm... no account found... create one..
            accnt = new Account( uid );
            add( accnt );
            updateIndecies();
        }
        return accnt;
    }

    /**
     * @param newAccount account to update
     */
    public void update( final Account newAccount )
    {
        Account oldAccount = get( newAccount.getUid() );
        delete( oldAccount );
        save( newAccount );
    }

    protected void add( final Account account )
    {
        map.put( account.getUid(), account );
        uidIdx.add( account );
        uidDescIdx.add( account );
    }

    /**
     * Add account to the database.
     *
     * @param account
     */
    public void save( final Account account )
    {
        // TODO probably should make sure there's not duplicates
        add( account );
        updateIndecies();
    }

    public void delete( final Account account )
    {
        map.remove( account.getUid() );

        uidIdx.remove( account );
        uidDescIdx.remove( account );
    }

    private void updateIndecies()
    {
        Collections.sort( uidIdx, new Comparator()
        {
            public int compare( Object arg0, Object arg1 )
            {
                return ( (Account) arg0 ).getUid().compareTo(
                        ( (Account) arg1 ).getUid() );
            }
        });

        Collections.sort( uidDescIdx, new Comparator()
        {
            public int compare( Object arg0, Object arg1 )
            {
                return ( (Account) arg1 ).getUid().compareTo(
                        ( (Account) arg0 ).getUid() );
            }
        });
    }
}

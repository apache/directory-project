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
package org.safehaus.triplesec.demo.view;

import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;
import wicket.Component;
import org.safehaus.triplesec.demo.model.Account;
import org.safehaus.triplesec.demo.dao.AccountDao;

/**
 * Base class for contact detachable models. This class implements
 * all necessary logic except retrieval of the dao object, this way
 * we can isolate that logic.
 */
public abstract class AccountDetachableModel extends AbstractDetachableModel
{
    private String uid;
    private transient Account account;

    public AccountDetachableModel( Account account )
    {
        this.uid = account.getUid();
        this.account = account;
    }

    public IModel getNestedModel()
    {
        return null;
    }

    protected final void onAttach()
    {
        account = getAccountDao().get( uid );
    }

    protected final void onDetach()
    {
        account = null;
    }

    protected Object onGetObject( Component component )
    {
        return account;
    }

    protected void onSetObject( Component component, Object object )
    {
        throw new UnsupportedOperationException();
    }

    protected abstract AccountDao getAccountDao();
}

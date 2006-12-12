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

import org.safehaus.triplesec.demo.dao.AccountDao;
import org.safehaus.triplesec.demo.model.Account;
import wicket.model.IModel;

public class ProxyDataProvider extends AccountDataProvider
{
    private static final long serialVersionUID = -7305063412324483407L;
    private AccountDao dao;

    public ProxyDataProvider( AccountDao dao )
    {
        this.dao = dao;
    }

    protected AccountDao getAccountDao()
    {
        return dao;
    }

    public IModel model( Object object )
    {
        return new ProxyModel( (Account) object, dao );
    }
}

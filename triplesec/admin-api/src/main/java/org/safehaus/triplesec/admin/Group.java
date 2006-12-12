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
package org.safehaus.triplesec.admin;


import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.safehaus.triplesec.admin.dao.GroupDao;


public class Group extends AdministeredEntity
{
    private final GroupDao dao;
    private final String name;
    private final Set members;
    
    
    public Group( String creatorsName, Date createTimestamp, GroupDao dao, String name, Set members )
    {
        this( creatorsName, createTimestamp, null, null, dao, name, members );
    }
    
    
    public Group( String creatorsName, Date createTimestamp, String modifiersName, 
        Date modifyTimestamp, GroupDao dao, String name, Set members )
    {
        super( creatorsName, createTimestamp, modifiersName, modifyTimestamp );
        this.dao = dao;
        this.name = name;
        this.members = new HashSet( members );
    }


    public String getName()
    {
        return name;
    }


    public Set getMembers()
    {
        return Collections.unmodifiableSet( members );
    }
    
    
    public GroupModifier modifier()
    {
        return new GroupModifier( dao, this );
    }
    
    
    public String toString()
    {
        return name;
    }
}

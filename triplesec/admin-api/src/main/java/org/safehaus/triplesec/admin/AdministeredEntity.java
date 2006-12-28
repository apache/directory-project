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


import java.util.Date;


public abstract class AdministeredEntity
{
    private final String creatorsName;
    private final String modifiersName;
    private final Date createTimestamp;
    private final Date modifyTimestamp;
    
    
    protected AdministeredEntity( String creatorsName, Date creationTimestamp )
    {
        this( creatorsName, creationTimestamp, null, null );
    }
    
    
    protected AdministeredEntity( String creatorsName, Date createTimestamp, String modifiersName, Date modifyTimestamp )
    {
        this.creatorsName = creatorsName;
        this.createTimestamp = createTimestamp;
        this.modifiersName = modifiersName;
        this.modifyTimestamp = modifyTimestamp;
    }
    
    
    public String getCreatorsName()
    {
        return creatorsName;
    }


    public String getModifiersName()
    {
        return modifiersName;
    }


    public Date getCreateTimestamp()
    {
        return createTimestamp;
    }


    public Date getModifyTimestamp()
    {
        return modifyTimestamp;
    }
}

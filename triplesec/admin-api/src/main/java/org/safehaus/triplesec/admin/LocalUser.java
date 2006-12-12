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

import org.safehaus.triplesec.admin.dao.LocalUserDao;


public class LocalUser extends User
{
    private final LocalUserDao dao;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String address1;
    private final String address2;
    private final String city;
    private final String stateProvRegion;
    private final String zipPostalCode;
    private final String country;
    private final String company;
    private final String email;
    
    
    public LocalUser( String creatorsName, Date createTimestamp, String modifiersName, 
        Date modifyTimestamp, LocalUserDao dao, String id, String description, String firstName, 
        String lastName, String password, String address1, String address2, String city, String stateProvRegion, 
        String zipPostalCode, String country, String company, String email, boolean disabled )
    {
        super( creatorsName, createTimestamp, modifiersName, modifyTimestamp, id, description, disabled );
        this.dao = dao;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateProvRegion = stateProvRegion;
        this.zipPostalCode = zipPostalCode;
        this.country = country;
        this.company = company;
        this.email = email;
    }


    public LocalUser( String creatorsName, Date createTimestamp, LocalUserDao dao, String id, 
        String description, String firstName, String lastName, String password, String address1, 
        String address2, String city, String stateProvRegion, String zipPostalCode, String country, 
        String company, String email, boolean disabled )
    {
        super( creatorsName, createTimestamp, id, description, disabled );
        this.dao = dao;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateProvRegion = stateProvRegion;
        this.zipPostalCode = zipPostalCode;
        this.country = country;
        this.company = company;
        this.email = email;
    }


    public LocalUserModifier modifier()
    {
        return new LocalUserModifier( dao, this );
    }


    public String getFirstName()
    {
        return firstName;
    }


    public String getEmail()
    {
        return email;
    }


    public String getLastName()
    {
        return lastName;
    }


    public String getPassword()
    {
        return password;
    }


    public String getAddress1()
    {
        return address1;
    }


    public String getAddress2()
    {
        return address2;
    }


    public String getCity()
    {
        return city;
    }


    public String getStateProvRegion()
    {
        return stateProvRegion;
    }


    public String getZipPostalCode()
    {
        return zipPostalCode;
    }


    public String getCountry()
    {
        return country;
    }


    public String getCompany()
    {
        return company;
    }
}

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

import org.safehaus.triplesec.admin.dao.HauskeysUserDao;


public class HauskeysUser extends User
{
    private final HauskeysUserDao dao;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String mobile;
    private final String email;
    private final String notifyBy;
    private final String mobileCarrier;
    private final String tokenPin;
    private final String midletName;
    private final String failuresInEpoch;
    private final String activationKey;
    private final String realm;
    private final String secret;
    private final String label;
    private final String movingFactor;
    private final String address1;
    private final String address2;
    private final String city;
    private final String stateProvRegion;
    private final String zipPostalCode;
    private final String country;
    private final String company;
    
    
    public HauskeysUser( String creatorsName, Date createTimestamp, String modifiersName, 
        Date modifyTimestamp, HauskeysUserDao dao, String id, String description, String firstName, 
        String lastName, String password, String mobile, String email, String notifyBy, 
        String mobileCarrier, String tokenPin, String midletName, String failuresInEpoch, 
        String activationKey, String realm, String secret, String label, String movingFactor, 
        String address1, String address2, String city, String stateProvRegion, 
        String zipPostalCode, String country, String company, boolean disabled )
    {
        super( creatorsName, createTimestamp, modifiersName, modifyTimestamp, id, description, disabled );
        this.dao = dao;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.mobile = mobile;
        this.email = email;
        this.notifyBy = notifyBy;
        this.mobileCarrier = mobileCarrier;
        this.tokenPin = tokenPin;
        this.midletName = midletName;
        this.failuresInEpoch = failuresInEpoch;
        this.activationKey = activationKey;
        this.realm = realm;
        this.secret = secret;
        this.label = label;
        this.movingFactor = movingFactor;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateProvRegion = stateProvRegion;
        this.zipPostalCode = zipPostalCode;
        this.country = country;
        this.company = company;
    }


    public HauskeysUser( String creatorsName, Date createTimestamp, HauskeysUserDao dao, String id, 
        String description, String firstName, String lastName, String password, String mobile, 
        String email, String notifyBy, String mobileCarrier, String tokenPin, String midletName, 
        String failuresInEpoch, String activationKey, String realm, String secret, String label, 
        String movingFactor, String address1, String address2, String city, String stateProvRegion, 
        String zipPostalCode, String country, String company, boolean disabled )
    {
        this( creatorsName, createTimestamp, null, null, dao, id, description, firstName, lastName, 
            password, mobile, email, notifyBy, mobileCarrier, tokenPin, midletName, failuresInEpoch, 
            activationKey, realm, secret, label, movingFactor, address1, address2, city, stateProvRegion, 
            zipPostalCode, country, company, disabled );
    }


    public String getMobile()
    {
        return mobile;
    }


    public String getEmail()
    {
        return email;
    }


    public String getNotifyBy()
    {
        return notifyBy;
    }


    public String getMobileCarrier()
    {
        return mobileCarrier;
    }


    public String getTokenPin()
    {
        return tokenPin;
    }


    public String getMidletName()
    {
        return midletName;
    }


    public String getFailuresInEpoch()
    {
        return failuresInEpoch;
    }


    public String getActivationKey()
    {
        return activationKey;
    }


    public String getRealm()
    {
        return realm;
    }


    public String getSecret()
    {
        return secret;
    }


    public String getLabel()
    {
        return label;
    }


    public String getMovingFactor()
    {
        return movingFactor;
    }
    
    
    public String getFirstName()
    {
        return firstName;
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

    
    public HauskeysUserModifier modifier()
    {
        return new HauskeysUserModifier( dao, this );
    }
}
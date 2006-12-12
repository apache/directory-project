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
package org.safehaus.triplesec.registration.model;


import java.io.Serializable;

/**
 * Model object for capturing the Triplesec configuration settings.
 */
public class RegistrationInfo implements Serializable
{
    private static final long serialVersionUID = -2672319798571167870L;

    private String username;
    private String password;
    private String passwordConfirm;
    private String firstName;
    private String lastName;
    private String email;
    
    private String address1;
    private String address2;
    private String city;
    private String stateProvRegion;
    private String zipPostalCode;
    private String country;
    
    private String tokenPin;
    private String tokenPinConfirm;
    private String midletName;
    private String mobile;
    private String mobileCarrier;
    private String deploymentMechanism;
    
    
    public void setUsername( String username )
    {
        this.username = username;
    }
    
    
    public String getUsername()
    {
        return username;
    }


    public void setPassword( String password )
    {
        this.password = password;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPasswordConfirm( String passwordConfirm )
    {
        this.passwordConfirm = passwordConfirm;
    }


    public String getPasswordConfirm()
    {
        return passwordConfirm;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public String getFirstName()
    {
        return firstName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setEmail( String email )
    {
        this.email = email;
    }


    public String getEmail()
    {
        return email;
    }


    public void setAddress1( String address1 )
    {
        this.address1 = address1;
    }


    public String getAddress1()
    {
        return address1;
    }


    public void setAddress2( String address2 )
    {
        this.address2 = address2;
    }


    public String getAddress2()
    {
        return address2;
    }


    public void setCity( String city )
    {
        this.city = city;
    }


    public String getCity()
    {
        return city;
    }


    public void setStateProvRegion( String stateProvRegion )
    {
        this.stateProvRegion = stateProvRegion;
    }


    public String getStateProvRegion()
    {
        return stateProvRegion;
    }


    public void setZipPostalCode( String zipPostalCode )
    {
        this.zipPostalCode = zipPostalCode;
    }


    public String getZipPostalCode()
    {
        return zipPostalCode;
    }


    public void setCountry( String country )
    {
        this.country = country;
    }


    public String getCountry()
    {
        return country;
    }


    public void setMidletName( String midletName )
    {
        this.midletName = midletName;
    }


    public String getMidletName()
    {
        return midletName;
    }


    public void setMobile( String mobile )
    {
        this.mobile = mobile;
    }


    public String getMobile()
    {
        return mobile;
    }


    public void setMobileCarrier( String mobileCarrier )
    {
        this.mobileCarrier = mobileCarrier;
    }


    public String getMobileCarrier()
    {
        return mobileCarrier;
    }


    public void setDeploymentMechanism( String deploymentMechanism )
    {
        this.deploymentMechanism = deploymentMechanism;
    }


    public String getDeploymentMechanism()
    {
        return deploymentMechanism;
    }
    

    public void setTokenPin( String tokenPin )
    {
        this.tokenPin = tokenPin;
    }


    public String getTokenPin()
    {
        return tokenPin;
    }


    public void setTokenPinConfirm( String tokenPinConfirm )
    {
        this.tokenPinConfirm = tokenPinConfirm;
    }


    public String getTokenPinConfirm()
    {
        return tokenPinConfirm;
    }


    public String toString() {
        return "TriplesecRegistrationSettings{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirm='" + passwordConfirm + '\'' +
                ", tokenPin='" + tokenPin + '\'' +
                ", tokenPinConfirm='" + tokenPinConfirm + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", stateProvRegion='" + stateProvRegion + '\'' +
                ", zipPostalCode='" + zipPostalCode + '\'' +
                ", country='" + country + '\'' +
                ", midletName='" + midletName + '\'' +
                ", mobile=" + mobile +
                ", mobileCarrier=" + mobileCarrier +
                ", deploymentMechanism=" + deploymentMechanism +
                '}';
    }
}

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
/**
 * Created by IntelliJ IDEA.
 * User: tbennett
 * Date: May 9, 2006
 * Time: 12:02:48 AM
 * To change this template use File | Settings | File Templates.
 */
package org.safehaus.triplesec.demo.service;

/**
 * A common place to get business delegates.
 */
public class Registry
{
    /** Holds the single instance of the registry */
    private static Registry ourInstance = new Registry();

    /** Management object for security */
    private PolicyManager policyManager;

    /** Management object for SMS service */
    private SmsManager smsManager;

    /**
     * Returns the singleton instance of the Registry. The
     * Registry must be initialized using setter methods before
     * it is of any use. This is normally done using Spring's
     * dependency injection.
     */
    public static Registry getInstance()
    {
        return ourInstance;
    }

    /**
     * Private constructor to enforce singleton pattern.
     */
    private Registry()
    {
    }

    public static PolicyManager policyManager()
    {
        return ourInstance.policyManager;
    }

    public static SmsManager smsManager()
    {
        return ourInstance.smsManager;
    }
    
    public PolicyManager getPolicyManager()
    {
        return policyManager;
    }

    public void setPolicyManager( PolicyManager policyManager )
    {
        this.policyManager = policyManager;
    }

    public SmsManager getSmsManager()
    {
        return smsManager;
    }

    public void setSmsManager( SmsManager smsManager )
    {
        this.smsManager = smsManager;
    }
}

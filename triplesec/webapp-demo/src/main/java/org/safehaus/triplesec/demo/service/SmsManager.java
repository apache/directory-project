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
package org.safehaus.triplesec.demo.service;

/**
 * Created by IntelliJ IDEA.
 * User: tbennett
 * Date: May 13, 2006
 * Time: 3:23:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SmsManager {
    void setSmsTransportUrl( String url );
    void setSmsAccountName( String accountName );
    void setSmsUsername( String username );
    void setSmsPassword( String password );
    void sendSmsMessage( String mobile, String carrier, String username )
            throws Exception;
}

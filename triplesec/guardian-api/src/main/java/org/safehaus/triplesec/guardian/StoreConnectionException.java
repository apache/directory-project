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
package org.safehaus.triplesec.guardian;


/**
 * A {@link GuardianException} which is thrown when {@link ConnectionDriver}
 * failed to connect to {@link ApplicationPolicy} due to network,
 * authentication, or parameter problems.
 *
 * @author Trustin Lee
 * @version $Rev: 53 $, $Date: 2005-08-21 20:58:16 -0400 (Sun, 21 Aug 2005) $
 */
public class StoreConnectionException extends GuardianException
{
    /** */
    private static final long serialVersionUID = -3699779444160471445L;


    /**
     *
     */
    public StoreConnectionException()
    {
        super();
    }


    /**
     *
     * @param message
     */
    public StoreConnectionException( String message )
    {
        super(message);
    }


    /**
     *
     * @param nested
     */
    public StoreConnectionException( Throwable nested )
    {
        super(nested);
    }


    /**
     *
     * @param message
     * @param nested
     */
    public StoreConnectionException( String message, Throwable nested )
    {
        super(message, nested);
    }

}

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
 * A {@link GuardianException} which is thrown when no appropriate 
 * {@link ConnectionDriver} for the URL a user specified is found.
 *
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 */
public class NoConnectionDriverException extends GuardianException
{
    private static final long serialVersionUID = -6051735665432589525L;


    /**
     * Creates a new instance.
     */
    public NoConnectionDriverException()
    {
        super();
    }


    /**
     * Creates a new instance.
     *
     * @param message a detailed description
     */
    public NoConnectionDriverException( String message )
    {
        super(message);
    }


    /**
     * Creates a new instance.
     *
     * @param nested the root cause of this exception
     */
    public NoConnectionDriverException( Throwable nested )
    {
        super(nested);
    }


    /**
     * Creates a new instance.
     *
     * @param message a detailed description
     * @param nested the root cause of this exception
     */
    public NoConnectionDriverException( String message, Throwable nested )
    {
        super(message, nested);
    }
}

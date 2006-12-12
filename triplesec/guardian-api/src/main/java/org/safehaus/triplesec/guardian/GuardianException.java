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
 * An exception thrown by {@link ApplicationPolicy} and
 * {@link ApplicationPolicyFactory} when connection to the store is
 * not established or crashed.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @author Trustin Lee
 * 
 * @version $Rev: 53 $, $Date: 2005-08-21 20:58:16 -0400 (Sun, 21 Aug 2005) $
 */
public class GuardianException extends RuntimeException
{
    private static final long serialVersionUID = 4460210351439248871L;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------
    
    
    /**
     * Creates a simple exception with no message.
     */
    public GuardianException()
    {
        super();
    }
    

    /**
     * Creates a an exception with a message.
     * 
     * @param message a message String indicating the problem
     */
    public GuardianException( String message )
    {
        super( message );
    }

    
    /**
     * Creates a nested exception wrapping another throwable.
     *  
     * @param nested the throwable wrapped by this ExceptionTests.
     */
    public GuardianException( Throwable nested )
    {
        super( nested );
    }

    
    /**
     * Creates a nested exception wrapping another throwable with a message.
     * 
     * @param message a message String indicating the problem
     * @param nested the throwable wrapped by this ExceptionTests.
     */
    public GuardianException( String message, Throwable nested )
    {
        super( message, nested );
    }
}

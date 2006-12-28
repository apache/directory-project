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
package org.safehaus.triplesec.verifier.hotp;


import org.safehaus.otp.HotpErrorConstants;


/**
 * Exception thrown when a HOTP account is locked.
 *
 * @version $Rev$
 */
public class AccountLockedOutException extends HotpException
{
    private static final long serialVersionUID = -5526582031617488434L;


    public AccountLockedOutException()
    {
        super( HotpErrorConstants.LOCKEDOUT_VAL, HotpErrorConstants.LOCKEDOUT_MSG );
    }


    public AccountLockedOutException( Throwable throwable )
    {
        super( HotpErrorConstants.LOCKEDOUT_VAL, HotpErrorConstants.LOCKEDOUT_MSG, throwable );
    }


    public AccountLockedOutException( String s, Throwable throwable )
    {
        super( HotpErrorConstants.LOCKEDOUT_VAL, s, throwable );
    }
}

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
package org.safehaus.triplesec.jaas;


import org.safehaus.otp.HotpErrorConstants;


/**
 * Exception thrown when a HOTP account is locked.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev$
 */
public class ResynchInProgressException extends HotpLoginException
{
    private static final long serialVersionUID = 1L;


    public ResynchInProgressException()
    {
        super( HotpErrorConstants.RESYNCH_INPROGRESS_VAL, HotpErrorConstants.RESYNCH_INPROGRESS_MSG );
    }


    public ResynchInProgressException( String s )
    {
        super( HotpErrorConstants.RESYNCH_INPROGRESS_VAL, s );
    }
}

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
package org.safehaus.sms;


/**
 * An exception denoting a failure with the SMS system.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class SmsException extends Exception
{
    private static final long serialVersionUID = 1L;
    /** if an error code is unavailable we set it to -1 */
    private static final int UNAVAILABLE = -1;
    /** the error code associated with this exception or -1 if none */
    private final int errorCode;


    /**
     * Creates an SmsException with an errorCode.
     *
     * @param errorCode the provider specific error code associated with this exception
     */
    public SmsException( int errorCode )
    {
        this.errorCode = errorCode;
    }


    /**
     * Creates an SmsException without an error code.
     */
    public SmsException()
    {
        this.errorCode = UNAVAILABLE;
    }


    /**
     * Creates an SmsException without an error code but with a message.
     */
    public SmsException( String msg )
    {
        super( msg );
        this.errorCode = UNAVAILABLE;
    }


    /**
     * Gets the error code associated with this exception or -1 if none is available.
     *
     * @return a non-negative error code or -1 if none is available
     */
    public int getErrorCode()
    {
        return errorCode;
    }
}

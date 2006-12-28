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
package org.safehaus.otp;


/**
 * HOTP moving factor resynchronization protocol parameters.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public interface ResynchParameters
{
    /** an implementation that always returns the default values */
    ResynchParameters DEFAULTS = new ResynchParameters()
    {
        public int getLookaheadSize()
        {
            return 10;
        }


        public int getNumResyncValidations()
        {
            return 2;
        }


        public int getLockoutCount()
        {
            return 3;
        }


        public String toString()
        {
            return "(defaults) resync params[lookahead=10, resyncValidations=2, lockoutCount=3]";
        }
    };


    /**
     * Gets the HOTP value lookahead window size used to resynchronize the
     * moving factor for both client and server.
     *
     * @return the size of the HOTP lookahead window (s)
     */
    int getLookaheadSize();


    /**
     * Gets the number of consecutive HOTP values an out of sync client will
     * be asked for.  2-3 times is an acceptable and secure value for this
     * parameter.
     *
     * @return the number of consecutive validations for resynch
     */
    int getNumResyncValidations();


    /**
     * Gets the 'throttling' (T) parameter used to lock out an account after a
     * certain number of authentication attempts.
     *
     * @return the number of failured authentication attempts before locking
     * out an account
     */
    int getLockoutCount();
}

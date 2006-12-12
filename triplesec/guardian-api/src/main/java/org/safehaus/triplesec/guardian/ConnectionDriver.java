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


import java.util.Properties;


/**
 * A driver interface that provides an extension mechanism for TripleSec
 * Guardian API to support various connection types such as JNDI, in-memory
 * store, and JDBC.
 *
 * @author Trustin Lee
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 53 $
 */
public interface ConnectionDriver
{
    /**
     * Implement this method to return <tt>true</tt> if and only if
     * this driver can connect to the specified <tt>url</tt>
     *
     * @param url the URL to connect to
     * @return <tt>true</tt> if and only if this driver can connect to
     *         the specified <tt>url</tt>
     */
    boolean accept( String url );

    /**
     * Implement this method to connect to the {@link ApplicationPolicy}
     * with the specified <tt>url</tt> and return the connected {@link ApplicationPolicy}.
     *
     * @param url the URL to connect to
     * @param info the extra information a user specified
     * @return the connected {@link ApplicationPolicy}
     * @throws GuardianException if failed to connect
     */
    ApplicationPolicy newStore( String url, Properties info ) throws GuardianException;
}

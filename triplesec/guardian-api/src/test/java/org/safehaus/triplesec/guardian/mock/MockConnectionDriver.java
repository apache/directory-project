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
package org.safehaus.triplesec.guardian.mock;


import java.util.Properties;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.ConnectionDriver;
import org.safehaus.triplesec.guardian.GuardianException;


/**
 * A mock connection specification.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 53 $
 */
public class MockConnectionDriver implements ConnectionDriver
{
    static
    {
        ApplicationPolicyFactory.registerDriver( new MockConnectionDriver() );
    }

    public boolean accept( String url )
    {
        if ( url.equals( "mockApplication" ) )
        {
            return true;
        }

        return false;
    }

    public ApplicationPolicy newStore( String url, Properties info ) throws GuardianException
    {
        return new MockApplicationPolicy();
    }
}

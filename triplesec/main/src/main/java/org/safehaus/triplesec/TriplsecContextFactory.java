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
package org.safehaus.triplesec;


import org.apache.directory.server.jndi.ServerContextFactory;
import org.apache.directory.server.core.DirectoryService;

import javax.naming.NamingException;


/**
 * Nothing for now.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplsecContextFactory extends ServerContextFactory
{

    public void afterStartup( DirectoryService service ) throws NamingException
    {
        super.afterStartup( service );
    }


    public void afterShutdown( DirectoryService service )
    {
        super.afterShutdown( service );
    }
}

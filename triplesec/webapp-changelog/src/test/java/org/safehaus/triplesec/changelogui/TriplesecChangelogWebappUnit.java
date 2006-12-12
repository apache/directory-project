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
package org.safehaus.triplesec.changelogui;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.NamingException;

import org.safehaus.triplesec.integration.TriplesecIntegration;

public class TriplesecChangelogWebappUnit extends TriplesecIntegration
{
    
    private static String dbDriver = "org.apache.derby.jdbc.EmbeddedDriver";

    private static String dbProtocol = "jdbc:derby:";

    private static String dbName = "changelogDb";

    private static String dbTable = "changelogTable";

    private static String dbTableFields = "(Id int generated always as identity, EventType int, AffectedEntry varchar(255), EventPrincipal varchar(127), EventTime timestamp, EventMessage varchar(1024))";

    private static String dbUserName = "user1";

    private static String dbPassword = "user1";
    
    private static Properties props = new Properties();
    

    public TriplesecChangelogWebappUnit() throws Exception
    {
        super();
        
        props.put( "user", dbUserName );
        props.put( "password", dbPassword );
        
    }
    
    
    protected void setUp() throws Exception
    {
        File serverHome = getServerHome();
        File dbDir = new File( serverHome, "db" );
        dbDir.mkdir();
        System.setProperty( "derby.system.home", dbDir.getCanonicalPath() );
        initDb( dbDir );
        super.setUp();
        
    }
    
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
        shutdownDb();
    }
    
    
    private void initDb( File dbDir ) throws Exception
    {
        
        // Initialize the DB driver
        // (This starts the embedded server.)
        Class.forName( dbDriver ).newInstance();

        Connection conn;
        
        // Get the DB connection
        try
        {
            conn = DriverManager.getConnection( dbProtocol + dbName + ";create=true", props );
        }
        catch ( SQLException e )
        {
            NamingException ne = new NamingException();
            ne.setRootCause( e );
            throw ne;
        }

        // Create the table
        Statement statement = conn.createStatement();
        statement.execute( "create table " + dbTable + dbTableFields );

    }
    
    
    private void shutdownDb() throws Exception
    {
        DriverManager.getConnection( dbProtocol + dbName + ";shutdown=true", props );
    }

}

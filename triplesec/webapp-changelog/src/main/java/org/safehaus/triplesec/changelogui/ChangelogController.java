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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;

import org.safehaus.triplesec.changelog.beta.model.BaseChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.ChangeEvent;

public class ChangelogController
{

    private static String dbProtocol = "jdbc:derby:";

    private static String dbName = "changelogDb";

    private static String dbTable = "changelogTable";

    private static String dbUserName = "user1";

    private static String dbPassword = "user1";
    
    private Properties props = new Properties();
    
    private Connection conn;

    private BaseChangeEvent changeEvent;
    
    public void setChangeEvent( BaseChangeEvent changeEvent )
    {
        this.changeEvent = changeEvent;
    }
    
    public ChangeEvent getChangeEvent()
    {
        return this.changeEvent;
    }
    
    public ChangelogController() throws SQLException
    {
        props.put( "user", dbUserName );
        props.put( "password", dbPassword );
        conn = DriverManager.getConnection( dbProtocol + dbName, props );
    }
        
    public List getLogs() throws SQLException
    {
        List logs = new ArrayList();

        PreparedStatement statement = null;
        statement = conn.prepareStatement( "select * from " + dbTable );

        ResultSet rs = statement.executeQuery();
        while ( rs.next() )
        {

            BaseChangeEvent changeEvent = new BaseChangeEvent(
                rs.getInt( "Id" ),
                rs.getInt( "EventType" ), 
                rs.getString( "AffectedEntry" ), 
                rs.getString( "EventPrincipal" ),
                new Date( rs.getTimestamp( "EventTime" ).getTime() ) );

            logs.add( changeEvent );
        }

        return logs;
    }
    
    public String rollbackPointSelected() throws SQLException {
        FacesContext context = FacesContext.getCurrentInstance();
        String eventId = (String) context.getExternalContext().getRequestParameterMap().get("eventId");
        
        PreparedStatement statement = conn.prepareStatement( "select * from " + dbTable + " where Id = ?" );
        try
        {
            statement.setInt( 1, Integer.parseInt( eventId ) );
        }
        catch ( NumberFormatException e )
        {
            e.printStackTrace();
        }

        ResultSet rs = statement.executeQuery();
        if ( rs.next() )
        {
            BaseChangeEvent changeEvent = new BaseChangeEvent(
                rs.getInt( "Id" ),
                rs.getInt( "EventType" ), 
                rs.getString( "AffectedEntry" ), 
                rs.getString( "EventPrincipal" ),
                new Date( rs.getTimestamp( "EventTime" ).getTime() ) );
            
            this.changeEvent =  changeEvent;

        }

        return "success";
    }
    
    

}

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
package org.safehaus.triplesec.demo.dao.jdbc;

import org.safehaus.triplesec.demo.dao.InvestmentsDao;
import org.safehaus.triplesec.demo.model.Investments;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class JdbcInvestmentsDao implements InvestmentsDao
{
    private final Connection connection;


    public JdbcInvestmentsDao ( Connection c )
    {
        connection = c;
    }


    public void clean() throws SQLException
    {
        Statement st = null;

        try
        {
            st = connection.createStatement();
            st.execute( "DROP TABLE INVESTMENTS" );
        }
        finally
        {
            if ( st != null ) { st.close(); }
        }
    }


    public void build() throws SQLException
    {
        Statement st = null;

        try
        {
            st = connection.createStatement();
            st.execute( "CREATE TABLE INVESTMENTS ( UID CHAR(128), BONDS INT, TBILLS INT, EMERGING_MARKETS INT, TECH_STOCKS INT, VOLATILE_HIGH_YIELD INT )" );
            st.execute( "CREATE UNIQUE INDEX uid_index ON INVESTMENTS ( UID )" );
        }
        finally
        {
            if ( st != null ) { st.close(); }
        }
    }


    public Investments get( String uid ) throws SQLException
    {
        Investments investments;
        Statement st = null;
        ResultSet rs = null;

        try
        {
            st = connection.createStatement();
            rs = st.executeQuery( "SELECT * FROM INVESTMENTS WHERE UID = '" + uid + "'" );

            while( rs.next() )
            {
                investments = new Investments( uid );
                investments.setBonds( rs.getInt( "BONDS" ) );
                investments.setTbills( rs.getInt( "TBILLS" ) );
                investments.setEmergingMarkets( rs.getInt( "EMERGING_MARKETS" ) );
                investments.setTechStocks( rs.getInt( "TECH_STOCKS" ) );
                investments.setVolatileHighYield( rs.getInt( "VOLATILE_HIGH_YIELD" ) );
                return investments;
            }
        }
        finally
        {
            if ( st != null ) { st.close(); }
            if ( rs != null ) { rs.close(); }
        }

        return null;
    }


    public void insert( Investments investments ) throws SQLException
    {
        Statement st = null;

        try
        {
            st = connection.createStatement();
            st.execute( "INSERT INTO INVESTMENTS VALUES ( '" + investments.getUid() + "', "
                    + investments.getBonds() + ", " + investments.getTbills() + ", "
                    + investments.getEmergingMarkets() + ", " + investments.getTechStocks() + ", "
                    + investments.getVolatileHighYield() + " ) " );
        }
        finally
        {
            if ( st != null ) { st.close(); }
        }
    }


    public void update( Investments investments ) throws SQLException
    {
        delete( investments.getUid() );
        insert( investments );
    }


    public void delete( String uid ) throws SQLException
    {
        Statement st = null;

        try
        {
            st = connection.createStatement();
            st.execute( "DELETE FROM INVESTMENTS WHERE UID = '" + uid + "'" );
        }
        finally
        {
            if ( st != null ) { st.close(); }
        }
    }
}

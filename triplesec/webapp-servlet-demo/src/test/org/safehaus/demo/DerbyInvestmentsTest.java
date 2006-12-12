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
package org.safehaus.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;

import junit.framework.TestCase;


public class DerbyInvestmentsTest extends TestCase
{
	Connection c;
	
	public void setUp() throws Exception
	{
		DriverManager.registerDriver( new EmbeddedDriver() );
		c = DriverManager.getConnection( "jdbc:derby:testdb;create=true" );
		InvestmentsAccessObject dao = new InvestmentsAccessObject( c );

		try
		{
			dao.dropTable();
		}
		catch ( SQLException  e )
		{
		}

		dao.createTable();
		Investments investments = new Investments( "akarasulu" );
		investments.set( 0, 1000 );
		investments.set( 1, 1000 );
		investments.set( 2, 1000 );
		investments.set( 3, 1000 );
		investments.set( 4, 1000 );
		dao.insert( investments );
	}
	

	public void tearDown() throws Exception
	{
		InvestmentsAccessObject dao = new InvestmentsAccessObject( c );
		try
		{
			dao.dropTable();
		}
		catch ( SQLException  e )
		{
		}
		c.close();
		c = null;
	}
	
	
	public void testInsertInvestment() throws Exception
	{
		Investments investments = new Investments( "testuser" );
		investments.setBonds( 100 );
		InvestmentsAccessObject dao = new InvestmentsAccessObject( c );
		dao.insert( investments );
		Investments inserted = dao.getInvestments( "testuser" );
		assertNotNull( inserted );
		assertEquals( inserted.getUid(), "testuser" );
		assertEquals( inserted.getBonds(), 100 );
		assertEquals( inserted.getEmergingMarkets(), 10000 );
		assertEquals( inserted.getTbills(), 10000 );
		assertEquals( inserted.getTechStocks(), 10000 );
		assertEquals( inserted.getVolatileHighYield(), 10000 );
	}
	

	public void testDeleteInvestment() throws Exception
	{
		InvestmentsAccessObject dao = new InvestmentsAccessObject( c );
		dao.delete( "akarasulu" );
		Investments inserted = dao.getInvestments( "akarasulu" );
		assertNull( inserted );
	}
	
	
	public void testUpdateInvestment() throws Exception
	{
		InvestmentsAccessObject dao = new InvestmentsAccessObject( c );
		Investments investments = dao.getInvestments( "akarasulu" );
		assertNotNull( investments );
		investments.setBonds( 12345 );
		investments.setVolatileHighYield( 54321 );
		dao.update( investments );
		investments = null;
		investments = dao.getInvestments( "akarasulu" );
		assertNotNull( investments );
		assertEquals( 12345, investments.getBonds() );
		assertEquals( 54321, investments.getVolatileHighYield() );
		assertEquals( 1000, investments.getTechStocks() );
	}
	
	
	public void testGetInvestment() throws Exception
	{
		InvestmentsAccessObject dao = new InvestmentsAccessObject( c );
		Investments investments = dao.getInvestments( "akarasulu" );
		assertNotNull( investments );
		assertEquals( investments.getUid(), "akarasulu" );
		assertEquals( investments.getBonds(), 1000 );
		assertEquals( investments.getEmergingMarkets(), 1000 );
		assertEquals( investments.getTbills(), 1000 );
		assertEquals( investments.getTechStocks(), 1000 );
		assertEquals( investments.getVolatileHighYield(), 1000 );
	}
}

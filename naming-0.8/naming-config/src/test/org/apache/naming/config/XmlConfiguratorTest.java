/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.naming.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import junit.framework.TestCase;

/**
 * Test case for the XML configuration methods, testing environment entries
 * and database connection resource factories.
 * 
 * @author <a href="brett@apache.org">Brett Porter</a>
 * @version $Id: XmlConfiguratorTest.java,v 1.2 2003/12/01 02:02:45 brett Exp $
 */
public class XmlConfiguratorTest extends TestCase
{
    public XmlConfiguratorTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        XmlConfigurator.destroyInitialContext();
    }
    
    /** 
     * Default root context name -- what XmlConfigurator assumes if root context
     * element in xml config file does not have a name attribute, as in
     * test-jndi.xml
     */
    protected static String DEFAULT_ROOT="java:comp/env";
    
    /**
     * Alternate root context name specified in test-jndi2.xml.  Must match
     * name attribute of top-level context element in test-jndi2.xml
     */
    protected static String ALT_ROOT="alt/root/context";

    /**
     * Test for correctly configured environment entries.
     * @throws Exception as tests do
     */
    public void testEnvironment() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi.xml"));
        checkEnvironment(DEFAULT_ROOT);
        XmlConfigurator.destroyInitialContext();
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi2.xml"));
        checkEnvironment(ALT_ROOT);
    }
    
    protected void checkEnvironment(String root) throws Exception {
        Context ctx = new InitialContext();
        Context env = (Context) ctx.lookup(root);
        String host = (String) env.lookup("config/host");
        Integer port = (Integer) env.lookup("config/port");

        assertEquals("Check host", "www.apache.org", host);
        assertEquals("Check port", new Integer(80), port);

        Boolean trueBool = (Boolean) env.lookup("config/mytruebool");
        Boolean falseBool = (Boolean) env.lookup("config/myfalsebool");

        assertTrue("Check true boolean value", trueBool.booleanValue());
        assertTrue("Check false boolean value", !falseBool.booleanValue());
        
        trueBool = (Boolean) ctx.lookup(root + "/config/mytruebool");
        assertTrue("Check true boolean value -- root lookup", trueBool.booleanValue());
        
    }
  
    /**
     *  Test config as a subcontext of a different root.
     *  @throws Exception if it fails
     */
    public void testDuplicateSubcontextName() throws Exception{
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi.xml"));
        checkDuplicateSubcontextName(DEFAULT_ROOT);     
    }
    
    protected void checkDuplicateSubcontextName(String root) throws Exception {
        Context ctx = new InitialContext();
        Context env = (Context) ctx.lookup(root);
        String user = (String) env.lookup("jdbc/config/pool/user");
        assertEquals("Check user", "dbuser", user);
        user = (String) ctx.lookup(root + "/jdbc/config/pool/user");
        assertEquals("Check user -- root lookup", "dbuser", user);   
    }

    /**
     * Test for correctly configured and operational database connection
     * resource factories.
     * @throws Exception as tests do
     */
    public void testJdbc() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi.xml"));
        checkJdbc(DEFAULT_ROOT);
        XmlConfigurator.destroyInitialContext();
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi2.xml"));
        checkJdbc(ALT_ROOT);
    }
    
    protected void checkJdbc(String root) throws Exception {
        Context ctx = new InitialContext();
        Context env = (Context) ctx.lookup(root);
        DataSource ds = (DataSource) env.lookup("jdbc/pool");
        Connection con = null;
        Statement stat = null; 
        ResultSet rs = null;
        try {
            con = ds.getConnection();
            stat = con.createStatement();
            stat.executeUpdate("DROP TABLE DUAL IF EXISTS");
            stat.executeUpdate("CREATE TABLE DUAL(value char(50))");
            stat.executeUpdate("INSERT INTO DUAL VALUES(1)");
            rs = stat.executeQuery("SELECT * FROM DUAL");
            while (rs.next()) {
               assertEquals("Check you get back what you put into the DB", 1, rs.getInt(1));
            }
        }
        finally {
            if (rs != null) { 
                rs.close(); 
            }
            if (stat != null) { 
                stat.close(); 
            }
            if (con != null) { 
                con.close(); 
            }
        }
    }
}


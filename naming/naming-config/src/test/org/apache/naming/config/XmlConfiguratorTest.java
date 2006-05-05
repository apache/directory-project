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

import java.util.Hashtable;



import javax.naming.Context;

import javax.naming.InitialContext;

import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePartDataSource;


import javax.sql.DataSource;



import junit.framework.TestCase;

import org.apache.naming.ContextBindings;
import org.apache.naming.NamingContextFactory;



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
     * 
     * @throws Exception as tests do
     */
    public void testEnvironment() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi.xml"));
        checkEnvironment(DEFAULT_ROOT, new InitialContext());
        XmlConfigurator.destroyInitialContext();
        
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi2.xml"));
        checkEnvironment(ALT_ROOT, new InitialContext());
        
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi1.xml"));
        // "named" global context has same environment entries -- verify
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.naming.NamingContextFactory");
        env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
        env.put(NamingContextFactory.NAME, "global"); //name attribute 
        InitialContext ctx = new InitialContext(env);
        checkEnvironment(DEFAULT_ROOT, new InitialContext(env));
        
        // "anonymous" context entries should be overwritten now
        ctx = new InitialContext();
        Context envCtx = (Context) ctx.lookup(ALT_ROOT);
        String host = (String) envCtx.lookup("config/host");
        Integer port = (Integer) envCtx.lookup("config/port");
        assertEquals("Check host", "www.jakarta.org", host);
        assertEquals("Check port", new Integer(85), port);
        
        ContextBindings.destroyBoundContexts();
    }
    
    protected void checkEnvironment(String root, Context ctx) throws Exception {
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
        XmlConfigurator.destroyInitialContext();
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
        checkJdbc(DEFAULT_ROOT, new InitialContext());
        XmlConfigurator.destroyInitialContext();
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi2.xml"));
        checkJdbc(ALT_ROOT, new InitialContext());
        XmlConfigurator.destroyInitialContext();
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi1.xml"));
        checkJdbc(ALT_ROOT, new InitialContext());
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.naming.NamingContextFactory");
        env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
        env.put(NamingContextFactory.NAME, "global"); //name attribute 
        checkJdbc(DEFAULT_ROOT + "/global", new InitialContext(env));
        ContextBindings.destroyBoundContexts();  
    }
    
    protected void checkJdbc(String root, Context ctx) throws Exception {
        Context env = (Context) ctx.lookup(root);
        DataSource ds = (DataSource) env.lookup("jdbc/pool");
        checkDs(ds);
    }
    
    public void testLocalLinks() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi3.xml"));
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.naming.NamingContextFactory");
        env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
        env.put(NamingContextFactory.NAME, "global"); //name attribute 
        checkJdbc(DEFAULT_ROOT, new InitialContext(env));
        
        env.put(NamingContextFactory.NAME, "app1");
        Context ctx = new InitialContext(env);
        Context envCtx = (Context) ctx.lookup("java:comp/env");
        Integer port = (Integer) envCtx.lookup("port");
        // follow link
        DataSource ds = (DataSource) envCtx.lookup("datasource"); 
        checkDs(ds);
        // resolve across link to other context
        checkDs((DataSource) ctx.lookup("java:comp/env/datasource")); 
        // use jndi url explicitly
        checkDs((DataSource) ctx.lookup("jndi:global/java:comp/env/jdbc/pool")); 
    }
    
    /* Verify that jndi: urls work with default config, using default context name and root */
    public void testJNDIDefault() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi.xml"));
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.naming.NamingContextFactory");
        env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
        env.put(NamingContextFactory.NAME, "global"); //name attribute 
        Context ctx = new InitialContext(env);
        String defaultName = NamingContextFactory.DEFAULT_NAME;
        checkDs((DataSource) ctx.lookup("jndi:" + defaultName + "/java:comp/env/jdbc/pool"));       
    }
    
    /**
     * Tests ldap links.  To activate, change the name to "testLdapLinks" and
     * 
     * 1. Change the rname in the following line from /test-jndi3.xml to a valid
     *     ldap url pointing to a live server which will accept the connection
     * 
     *     <link name="fooCo"  rname="ldap://ldap.fooco.com" />
     * 
     * 2. Change the value of lookupString so that rname + lookupString names
     *     an entry in the directory
     * 
     * 3. Change the value of mail to match the value of the mail attribute of
     *     the entry named by rname + lookupString.
     */
    public void tstLdapLinks() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/test-jndi3.xml"));
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.naming.NamingContextFactory"); 
        env.put(NamingContextFactory.NAME, "app1");
        Context ctx = new InitialContext(env);
        Context envCtx = (Context) ctx.lookup("java:comp/env");
        
        // local (link) name of the ldap context.  This is the name attribute
        // of the link in the config 
        String name = "fooCo"; 
        
        // name of the ldap entry to look up, relative to the ldap url provided
        // in the rname of the config
        String lookupString = "uid=jbgoode,ou=People,dc=fooco,dc=com";
        
        // Value of mail attribute of target entry. 
        String mail = "jbgoode@fooco.com";
       
        // follow link to ldap URL
        Context ldapContext = (Context) envCtx.lookup(name); 
        LdapContext entryCtx = (LdapContext)
            ldapContext.lookup(lookupString);
        Attributes attributes = (Attributes) entryCtx.getAttributes("");
       
        /* Remove comment to dump entry
        NamingEnumeration enum = attributes.getAll();
        while (enum.hasMore()) {
            System.out.println(enum.next());
        } */
        
        assertTrue(attributes.get("mail").contains(mail));
        ldapContext.close();
        
        // Now try through a jndi url
        ldapContext = (Context) new InitialContext().lookup
            ("jndi:app1/java:comp/env/" + name);
        entryCtx = (LdapContext)
        ldapContext.lookup(lookupString);
        attributes = (Attributes) entryCtx.getAttributes("");
        assertTrue(attributes.get("mail").contains(mail));
        ldapContext.close();
        
    }
    
    protected void checkDs(DataSource ds) throws Exception {
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
    
    /**
     * Mail factory configuration tests.  Currently disabled, as the tests
     * and associated configs require Javamail libraries and access to outbound
     * smpt servers and send email messages. 
     * 
     * For these test cases to work, both javamail.jar and activation.jar must be
     * available and "touser@todomain.com" needs to be a real email address.
     * NB: These test cases will send actual email messages to the to address.
     * 
     * To activate these tests, change the names from "tstXxx" to "testXxx".
     */

    /**
     * Test for correctly configured and operational mail 
     * session factory.  Does not use authorization config.
     * 
     * @throws Exception as tests do
     */
    public void tstMailSession() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/mail.xml"));
        Context ctx = new InitialContext();
        Context env = (Context) ctx.lookup("java:comp/env");
        Session session = (Session) env.lookup("mail/smtp");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("fromuser@fromdomain.com"));
        InternetAddress to[] = new InternetAddress[1];
        to[0] = new InternetAddress("touser@todomain.com");
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setSubject("mail session test");
        msg.setContent("this is a test", "text/plain");
        Transport.send(msg);
    }        
    
    /**
     * Test for correctly configured and operational send mail 
     * resource factory. 
     * 
     * @throws Exception as tests do
     */
    public void tstSendMail() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/mail.xml"));
        Context ctx = new InitialContext();
        Context env = (Context) ctx.lookup("java:comp/env");
        MimePartDataSource ds = (MimePartDataSource) env.lookup("mail/send");
        Message msg = ds.getMessageContext().getMessage();
        msg.setFrom(new InternetAddress("fromuser@fromdomain.com"));
        InternetAddress to[] = new InternetAddress[1];
        to[0] = new InternetAddress("touser@todomain.com");
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setSubject("sendmail test");
        msg.setContent("this is a test", "text/plain");
        Transport.send(msg);
    }
    
    /**
     * Test for correctly configured and operational send mail 
     * resource factory.  Uses auth config, passing mail.smpt.user
     * and password from config to mail session factory.
     * 
     * @throws Exception as tests do
     */
    public void tstSendMailAuth() throws Exception {
        XmlConfigurator.loadConfiguration(getClass().getResourceAsStream("/mail.xml"));
        Context ctx = new InitialContext();
        Context env = (Context) ctx.lookup("java:comp/env");
        Session session = (Session) env.lookup("mail/smtp-auth");
        Message msg = new MimeMessage(session);
        /* fromuser@todomain.com must be allowed to send authenticated
            mail from the configured smpt host.  Generally, that means that
            mail.smpt.user = fromuser. */
        msg.setFrom(new InternetAddress("fromuser@todomain.com"));
        InternetAddress to[] = new InternetAddress[1];
        to[0] = new InternetAddress("touser@todomain.com");
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setSubject("authenticated mail session test");
        msg.setContent("this is a test", "text/plain");
        Transport.send(msg);
    }

}




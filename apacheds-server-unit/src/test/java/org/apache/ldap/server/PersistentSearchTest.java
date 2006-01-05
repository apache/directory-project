/*
 * Copyright (c) 2004 Solarsis Group LLC.
 *
 * Licensed under the Open Software License, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://opensource.org/licenses/osl-2.1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ldap.server;


import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.ldap.common.message.PersistentSearchControl;


/**
 * Testcase which tests the correct operation of the persistent search control.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PersistentSearchTest extends AbstractServerTest
{
    private LdapContext ctx = null;

    public static final String RDN = "cn=Tori Amos";

    public static final String PERSON_DESCRIPTION = "an American singer-songwriter";


    /**
     * Creation of required attributes of a person entry.
     */
    protected Attributes getPersonAttributes( String sn, String cn )
    {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute( "objectClass" );
        attribute.add( "top" );
        attribute.add( "person" );
        attributes.put( attribute );
        attributes.put( "cn", cn );
        attributes.put( "sn", sn );

        return attributes;
    }


    /**
     * Create context and a person entry.
     */
    public void setUp() throws Exception
    {
        super.setUp();

        Hashtable env = new Hashtable();
        env.put( "java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( "java.naming.provider.url", "ldap://localhost:" + port + "/ou=system" );
        env.put( "java.naming.security.principal", "uid=admin,ou=system" );
        env.put( "java.naming.security.credentials", "secret" );
        env.put( "java.naming.security.authentication", "simple" );

        ctx = new InitialLdapContext( env, null );
        assertNotNull( ctx );

        // Create a person with description
        Attributes attributes = this.getPersonAttributes( "Amos", "Tori Amos" );
        attributes.put( "description", PERSON_DESCRIPTION );
        ctx.createSubcontext( RDN, attributes );
    }


    /**
     * Remove person entry and close context.
     */
    public void tearDown() throws Exception
    {
        ctx.unbind( RDN );
        ctx.close();

        ctx.close();
        ctx = null;

        super.tearDown();
    }


    public void testPsearchSend() throws Exception
    {
        PSearchListener listener = new PSearchListener();
        Thread t = new Thread( listener );
        t.start();
        
        Thread.sleep( 3000 );

        System.out.println( "--------------------------------------------------------------" );
        ctx.modifyAttributes( RDN, DirContext.REMOVE_ATTRIBUTE, 
            new BasicAttributes( "description", PERSON_DESCRIPTION, true ) );
        Thread.sleep( 3000 );
        
        // Create a person with description
        System.out.println( "--------------------------------------------------------------" );
        Attributes attributes = this.getPersonAttributes( "Black", "Jack Black" );
        attributes.put( "description", PERSON_DESCRIPTION );
        ctx.createSubcontext( "cn=Jack Black", attributes );
        Thread.sleep( 3000 );
    }
    
    
    class PSearchListener implements Runnable
    {
        public void run()
        {
            PersistentSearchControl control = new PersistentSearchControl();
            control.setCritical( true );
            Control[] ctxCtls = new Control[] { control };
            
            try
            {
                ctx.setRequestControls( ctxCtls );
                NamingEnumeration list = ctx.search( "", "objectClass=*", null );
                while( list.hasMore() )
                {
                    SearchResult result = ( SearchResult ) list.next();
                    System.out.print( "got entry: " + result );
                }
            }
            catch( Exception e ) 
            {
                e.printStackTrace();
            }
        }
    }
}

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


import com.sun.security.auth.module.Krb5LoginModule;
import junit.framework.TestCase;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.*;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Simple test to make sure Jaas authentication is working.  This test gets
 * bypassed without setting the demo.test.interactive property.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class JaasTest extends TestCase
{
    /** controls whether or not authentication is actually conducted */
    public static final String INTERACTIVE_KEY = "demo.test.interactive";

    LoginModule module = null;


    protected void setUp() throws Exception
    {
        module = new Krb5LoginModule();
    }


    protected void tearDown() throws Exception
    {
        module = null;
    }


    public void testLogin() throws LoginException
    {
        if ( ! System.getProperties().containsKey( INTERACTIVE_KEY ) )
        {
            System.err.println( "WARN: Interactive test bypassed - set demo.test.interactive=true to enable." );

            return;
        }

        String val = ( ( String ) System.getProperties().get( INTERACTIVE_KEY ) ).trim().toLowerCase();

        if ( ! val.equals( "true    " ) || ! val.equals( "yes" ) || ! val.equals( "on" ) || ! val.equals( "1") )
        {
            System.err.println( "WARN: Interactive test bypassed - set demo.test.interactive=true to enable." );

            return;
        }

        Map state = new HashMap();

        Map options = new HashMap();

        Subject subject = new Subject();

        CallbackHandler handler = new Handler();

        module.initialize( subject, handler, state, options );

        boolean result = module.login();

        if ( result )
        {
            System.out.println( "Authentication SUCCESS!" );
        }
        else
        {
            System.out.println( "Authentication FAILURE!" );
        }

        module.commit();
    }


    class Handler implements CallbackHandler
    {
        public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException
        {
            for ( int ii = 0; ii < callbacks.length; ii++ )
            {
                if ( callbacks[ii] instanceof TextOutputCallback )
                {
                    TextOutputCallback tocb = ( TextOutputCallback ) callbacks[ii];

                    switch( tocb.getMessageType() )
                    {
                        case( TextOutputCallback.INFORMATION ):

                            System.out.println( tocb.getMessage() );

                            break;

                        case( TextOutputCallback.WARNING ):

                            System.out.println( "WARNING: " + tocb.getMessage() );

                            break;

                        case( TextOutputCallback.ERROR ):

                            System.err.println( "ERROR: " + tocb.getMessage() );

                            break;

                        default:

                            throw new IllegalStateException( "unsupported message type" );
                    }
                }

                else if ( callbacks[ii] instanceof NameCallback )
                {
                    NameCallback ncb = ( NameCallback ) callbacks[ii];

                    System.err.print( ncb.getPrompt() );

                    System.err.flush();

                    ncb.setName( ( new BufferedReader( new InputStreamReader( System.in ) ) ).readLine() );
                }

                else if ( callbacks[ii] instanceof PasswordCallback )
                {
                    PasswordCallback pcb = ( PasswordCallback ) callbacks[ii];

                    System.err.print( pcb.getPrompt() );

                    System.err.flush();

                    pcb.setPassword( ( new BufferedReader( new InputStreamReader( System.in ) ) ).readLine().toCharArray() );
                }
            }
        }
    }
}

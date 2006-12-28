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
package org.safehaus.crypto;


import junit.framework.TestCase;


/**
 * Simple test for the BlockCipherWrapper.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class BlockCipherWrapperTest extends TestCase
{
    public void testWrapperOnDES() throws Exception
    {
        String message = "hello world this is a test";
        BlockCipherWrapper wrapper = new BlockCipherWrapper( DESEngine.class );
        byte[] encryped = wrapper.encrypt( "secret", message.getBytes( "UTF-8" ) );
        String decrypted = new String( wrapper.decrypt( "secret", encryped ), "UTF-8" );
        assertTrue( message.equals( decrypted.substring( 0, message.length()) ) );
    }
}

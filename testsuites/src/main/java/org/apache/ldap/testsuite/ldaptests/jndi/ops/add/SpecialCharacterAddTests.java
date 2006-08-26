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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.add;

import java.io.UnsupportedEncodingException;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * Check whether adding entries with special characters (e.g. German umlauts) works.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SpecialCharacterAddTests extends BaseProtocolTest
{

    DirContext ctx;

    public void setUp() throws NamingException
    {
        super.setUp();

        ctx = this.createContext();
        ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());
    }

    public void tearDown() throws NamingException
    {
        ctx.close();
        ctx = null;

        super.tearDown();
    }

    /**
     * Checks that an entry with non-ASCII chars can be added
     * 
     * @throws NamingException
     */
    public void testAddEntryTurkish() throws NamingException
    {
        // The bytes used are the UTF-8 encoding for turkish characters
        String cn = new String(
                new byte[] { (byte)0xC4, (byte)0xB0, (byte)0xC4, (byte)0xB1, 
                             (byte)0xC5, (byte)0x9E, (byte)0xC5, (byte)0x9F, 
                             (byte)0xC3, (byte)0x96, (byte)0xC3, (byte)0xB6, 
                             (byte)0xC3, (byte)0x9C, (byte)0xC3, (byte)0xBC, 
                             (byte)0xC4, (byte)0x9E, (byte)0xC4, (byte)0x9F });
        String sn = "baumgarten";

        // Add a person
        Attributes attributes = AttributesFactory.createPersonAttributes(cn, sn);

        String rdn = "cn=" + cn;

        DirContext person = ctx.createSubcontext(rdn, attributes);

        // Check whether person looks fine
        person = (DirContext) ctx.lookup(rdn);
        assertNotNull(person);
        attributes = person.getAttributes("");

        // objectclasses
        Attribute ocls = attributes.get("objectClass");
        String[] expectedOcls = { "top", "person" };

        for (int i = 0; i < expectedOcls.length; i++) {
            String name = expectedOcls[i];
            assertTrue("object class " + name + " is not present", ocls.contains(name));
        }

        // Other attributes
        Attribute cnRes = attributes.get("cn");
        assertTrue(cnRes.contains(cn));
        Attribute snRes = attributes.get("sn");
        assertTrue(snRes.contains(sn));
    }

    public void testAddEntryWithGermanUmlauts() throws NamingException, UnsupportedEncodingException
    {

        // Create a person with german "umlaut". The UTF-8 bytes code for
        // a german o umlaut is C3 B6. Its equivalence in ISO-8859-1 is F6.
        String cnValue = new String(new byte[]{'S', 't', 'e', 'f', 'a', 'n', ' ', 'Z', (byte)0xC3, (byte)0xB6, 'r', 'n', 'e', 'r'}, "UTF-8");
        String snValue = new String(new byte[]{'Z', (byte)0xC3, (byte)0xB6, 'r', 'n', 'e', 'r'}, "UTF-8");
        String rdn = "cn=" + cnValue;
        String allUmlauts = new String(new byte[]{(byte)0xC3, (byte)0x84, (byte)0xC3, (byte)0x96, (byte)0xC3, (byte)0x9C, (byte)0xC3, (byte)0x9F, (byte)0xC3, (byte)0xA4, (byte)0xC3, (byte)0xB6, (byte)0xC3, (byte)0xBC}, "UTF-8");
        Attributes attributes = AttributesFactory.createPersonAttributes(cnValue, snValue);
        attributes.put("description", allUmlauts);

        // add entry
        ctx.createSubcontext(rdn, attributes);

        // lookup and verify entry
        DirContext stefan = (DirContext) ctx.lookup(rdn);
        attributes = stefan.getAttributes("");
        // cn
        Attribute cn = attributes.get("cn");
        assertNotNull(cn);
        assertEquals("number of cn values", 1, cn.size());
        assertTrue(cn.contains(cnValue));
        // sn
        Attribute sn = attributes.get("sn");
        assertNotNull(sn);
        assertEquals("number of sn values", 1, sn.size());
        assertTrue(sn.contains(snValue));
        // description
        Attribute description = attributes.get("description");
        assertNotNull(description);
        assertEquals("number of description values", 1, description.size());
        assertTrue(description.contains(allUmlauts));
    }

    /**
     * Checks that an entry with non-ASCII chars can be added
     * 
     * @throws NamingException
     */
    public void testAddEntryNonASCII() throws NamingException
    {
        // The bytes used are the UTF-8 encoding for Jérôme :
        // 0x4A = J
        // 0xC3 0xA9 = é, encoded
        // 0x72 = r
        // 0xC3 0xB4 = ô, encoded
        // 0x6D = m
        // 0x65 = e
        String cn = new String(
                new byte[] { 0x4A, (byte) 0xC3, (byte) 0xA9, 0x72, (byte) 0xC3, (byte) 0xB4, 0x6D, 0x65 });
        String sn = "baumgarten";

        // Add a person
        Attributes attributes = AttributesFactory.createPersonAttributes(cn, sn);

        String rdn = "cn=" + cn;

        DirContext person = ctx.createSubcontext(rdn, attributes);

        // Check whether person looks fine
        person = (DirContext) ctx.lookup(rdn);
        assertNotNull(person);
        attributes = person.getAttributes("");

        // objectclasses
        Attribute ocls = attributes.get("objectClass");
        String[] expectedOcls = { "top", "person" };

        for (int i = 0; i < expectedOcls.length; i++) {
            String name = expectedOcls[i];
            assertTrue("object class " + name + " is not present", ocls.contains(name));
        }

        // Other attributes
        Attribute cnRes = attributes.get("cn");
        assertTrue(cnRes.contains(cn));
        Attribute snRes = attributes.get("sn");
        assertTrue(snRes.contains(sn));
    }

}

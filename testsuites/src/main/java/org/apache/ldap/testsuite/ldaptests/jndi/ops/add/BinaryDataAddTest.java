/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.testsuite.ldaptests.jndi.ops.add;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * Check whether adding entries with binary data works.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BinaryDataAddTest extends BaseProtocolTest
{

    private static String PERSON_SN_VALUE = "Amos";

    private static String PERSON_CN_VALUE = "Tori Amos";

    private static String PERSON_RDN = "cn=" + PERSON_CN_VALUE;

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
     * Add an inetOrgPerson entry with photo and check whether it can be
     * retrieved back correctly.
     */
    public void testInetOrgPersonWithJpegPhoto() throws NamingException
    {
        Attributes attrs = AttributesFactory.createInetOrgPersonAttributes(PERSON_CN_VALUE, PERSON_SN_VALUE);

        // Random data for jpegPhoto, 64k
        byte[] sourceData = new byte[64 * 1024];
        Random r = new Random(0);
        for (int i = 0; i < sourceData.length; i++) {
            sourceData[i] = (byte) r.nextInt();
        }
        attrs.put("jpegPhoto", sourceData);

        // Create entry
        ctx.bind(PERSON_RDN, null, attrs);

        // Read same entry and compare data bytewise
        Attributes readAttrs = ctx.getAttributes(PERSON_RDN);
        Attribute image = readAttrs.get("jpegPhoto");
        byte[] imageBytes = (byte[]) image.get();
        for (int i = 0; i < imageBytes.length; i++) {
            assertEquals("Byte number " + i, sourceData[i], imageBytes[i]);
        }
    }

    /**
     * Add an person entry with userPassword and check whether the value can be
     * retrieved back correctly.
     */
    public void testPersonWithPassword() throws NamingException, UnsupportedEncodingException
    {
        Attributes attrs = AttributesFactory.createPersonAttributes(PERSON_CN_VALUE, PERSON_SN_VALUE);

        String password = "MySecret1!";
        attrs.put("userPassword", password.getBytes("UTF-8"));

        // Create entry
        ctx.bind(PERSON_RDN, null, attrs);

        // Read same entry and compare password value
        Attributes readAttrs = ctx.getAttributes(PERSON_RDN);
        Attribute pwd = readAttrs.get("userPassword");
        byte[] bytes = (byte[]) pwd.get();
        String toTest = new String(bytes, "UTF-8");
        assertEquals(password, toTest);
    }

}

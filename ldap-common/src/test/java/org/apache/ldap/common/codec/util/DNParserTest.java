/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.ldap.common.codec.util;

import javax.naming.InvalidNameException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.ldap.common.codec.util.DNParser;
import org.apache.ldap.common.codec.util.LdapDN;


/**
 * Test the class LdapDN
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DNParserTest extends TestCase
{
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setup the test
     */
    protected void setUp()
    {
    }

    // CONSTRUCTOR functions --------------------------------------------------

    /**
     * test an empty DN
     */
    public void testLdapDNEmpty() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();

        Assert.assertEquals( "", (( LdapDN )dnParser.parse( "" )).getName() );
    }

    /**
     * test a simple DN : a = b
     */
    public void testLdapDNSimple() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();

        Assert.assertEquals( "a = b", (( LdapDN )dnParser.parse( "a = b" )).getName() );
        Assert.assertEquals( "a=b", (( LdapDN )dnParser.parse( "a = b" )).getString() );
    }

    /**
     * test a composite DN : a = b, d = e
     */
    public void testLdapDNComposite() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a = b, c = d" );
        Assert.assertEquals( "a=b,c=d", dn.toString() );
        Assert.assertEquals( "a = b, c = d", dn.getName() );
    }

    /**
     * test a composite DN with or without spaces: a=b, a =b, a= b, a = b, a  =  b
     */
    public void testLdapDNCompositeWithSpace() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a=b, a =b, a= b, a = b, a  =  b" );
        Assert.assertEquals( "a=b,a=b,a=b,a=b,a=b", dn.toString() );
        Assert.assertEquals( "a=b, a =b, a= b, a = b, a  =  b", dn.getName() );
    }

    /**
     * test a composite DN with differents separators : a=b;c=d,e=f
     * It should return a=b,c=d,e=f (the ';' is replaced by a ',')
     */
    public void testLdapDNCompositeSepators() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a=b;c=d,e=f" );
        Assert.assertEquals( "a=b,c=d,e=f", dn.toString() );
        Assert.assertEquals( "a=b;c=d,e=f", dn.getName() );
    }

    /**
     * test a simple DN with multiple NameComponents : a = b + c = d
     */
    public void testLdapDNSimpleMultivaluedAttribute() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a = b + c = d" );
        Assert.assertEquals( "a=b+c=d", dn.toString() );
        Assert.assertEquals( "a = b + c = d", dn.getName() );
    }

    /**
     * test a composite DN with multiple NC and separators : a=b+c=d, e=f + g=h + i=j
     */
    public void testLdapDNCompositeMultivaluedAttribute() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a=b+c=d, e=f + g=h + i=j" );
        Assert.assertEquals( "a=b+c=d,e=f+g=h+i=j", dn.toString() );
        Assert.assertEquals( "a=b+c=d, e=f + g=h + i=j", dn.getName() );
    }

    /**
     * test a simple DN with an oid prefix (uppercase) : OID.12.34.56 = azerty
     */
    public void testLdapDNOidUpper() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "OID.12.34.56 = azerty" );
        Assert.assertEquals( "oid.12.34.56=azerty", dn.toString() );
        Assert.assertEquals( "OID.12.34.56 = azerty", dn.getName() );
    }

    /**
     * test a simple DN with an oid prefix (lowercase) : oid.12.34.56 = azerty
     */
    public void testLdapDNOidLower() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "oid.12.34.56 = azerty" );
        Assert.assertEquals( "oid.12.34.56=azerty", dn.toString() );
        Assert.assertEquals( "oid.12.34.56 = azerty", dn.getName() );
    }

    /**
     * test a simple DN with an oid attribut without oid prefix : 12.34.56 = azerty
     */
    public void testLdapDNOidWithoutPrefix() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "12.34.56 = azerty" );
        Assert.assertEquals( "12.34.56=azerty", dn.toString() );
        Assert.assertEquals( "12.34.56 = azerty", dn.getName() );
    }

    /**
     * test a composite DN with an oid attribut wiithout oid prefix : 12.34.56 = azerty; 7.8 = test
     */
    public void testLdapDNCompositeOidWithoutPrefix() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "12.34.56 = azerty; 7.8 = test" );
        Assert.assertEquals( "12.34.56=azerty,7.8=test", dn.toString() );
        Assert.assertEquals( "12.34.56 = azerty; 7.8 = test", dn.getName() );
    }

    /**
     * test a simple DN with pair char attribute value : a = \,\=\+\<\>\#\;\\\"\A0\00"
     */
    public void testLdapDNPairCharAttributeValue() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00" );
        Assert.assertEquals( "a=\\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00", dn.toString() );
        Assert.assertEquals( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00", dn.getName() );
    }

    /**
     * test a simple DN with hexString attribute value : a = #0010A0AAFF
     */
    public void testLdapDNHexStringAttributeValue() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a = #0010A0AAFF" );
        Assert.assertEquals( "a=#0010A0AAFF", dn.toString() );
        Assert.assertEquals( "a = #0010A0AAFF", dn.getName() );
    }

    /**
     * test a simple DN with quoted attribute value : a = "quoted \"value"
     */
    public void testLdapDNQuotedAttributeValue() throws InvalidNameException
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "a = quoted \\\"value" );
        Assert.assertEquals( "a=quoted \\\"value", dn.toString() );
        Assert.assertEquals( "a = quoted \\\"value", dn.getName() );
    }

    /**
     * Test the encoding of a LdanDN
     *
     */
    public void testNameToBytes() throws Exception
    {
        DNParser dnParser = new DNParser();
        LdapDN dn = ( LdapDN )dnParser.parse( "cn = John, ou = People, OU = Marketing" );

        byte[] bytes = dn.getBytes();

        Assert.assertEquals( 30, dn.getNbBytes() );
        Assert.assertEquals("cn=John,ou=People,ou=Marketing", new String( bytes, "UTF-8" ) );
    }

    public void testStringParser() throws Exception
    {
        DNParser dnParser = new DNParser();
        LdapDN name = ( LdapDN )dnParser.parse( "CN = Emmanuel  Lécharny" );

        Assert.assertEquals( "CN = Emmanuel  Lécharny", name.getName() );
        Assert.assertEquals( "cn=Emmanuel  Lécharny", name.toString() );
    }

    public void testVsldapExtras() throws Exception
    {
        DNParser dnParser = new DNParser();
        LdapDN name = ( LdapDN )dnParser.parse( "cn=Billy Bakers, OID.2.5.4.11=Corporate Tax, ou=Fin-Accounting, ou=Americas, ou=Search, o=IMC, c=US" );

        Assert.assertEquals( "cn=Billy Bakers, OID.2.5.4.11=Corporate Tax, ou=Fin-Accounting, ou=Americas, ou=Search, o=IMC, c=US", name.getName() );
        Assert.assertEquals( "cn=Billy Bakers,oid.2.5.4.11=Corporate Tax,ou=Fin-Accounting,ou=Americas,ou=Search,o=IMC,c=US", name.toString() );
    }
}

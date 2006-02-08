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
package org.apache.asn1new.ldap.codec.primitives;

import javax.naming.InvalidNameException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.asn1new.ldap.codec.primitives.LdapRDN;

/**
 * Test the class LdapRDN
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapRDNTest extends TestCase
{
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setup the test
     */
    protected void setUp()
    {
    }

    /**
     * Test a null RDN
     */
    public void testLdapRDNNull() throws InvalidNameException
    {
        Assert.assertEquals( "", new LdapRDN().toString() );
    }

    /**
     * test an empty RDN
     */
    public void testLdapRDNEmpty() throws InvalidNameException
    {
        Assert.assertEquals( "", new LdapRDN( "" ).toString() );
    }

    /**
     * test a simple RDN : a = b
     */
    public void testLdapRDNSimple() throws InvalidNameException
    {
        Assert.assertEquals( "a=b", new LdapRDN( "a = b" ).toString() );
    }

    /**
     * test a composite RDN : a = b, d = e
     */
    public void testLdapRDNComposite() throws InvalidNameException
    {
        Assert.assertEquals( "a=b+c=d", new LdapRDN( "a = b + c = d" ).toString() );
    }

    /**
     * test a composite RDN with or without spaces: a=b, a =b, a= b, a = b, a  =  b
     */
    public void testLdapRDNCompositeWithSpace() throws InvalidNameException
    {
        Assert.assertEquals( "a=b", new LdapRDN( "a=b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a=b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a =b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a= b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a=b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a =b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a= b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a=b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a = b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a =b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a= b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a = b" ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a =b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a= b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( "a = b " ).toString() );
        Assert.assertEquals( "a=b", new LdapRDN( " a = b " ).toString() );
    }

    /**
     * test a simple RDN with differents separators : a = b + c = d
     */
    public void testLdapRDNSimpleMultivaluedAttribute() throws InvalidNameException
    {
        String result = new LdapRDN( "a = b + c = d" ).toString(); 
        Assert.assertEquals( "a=b+c=d", result );
    }

    /**
     * test a composite RDN with differents separators : a=b+c=d, e=f + g=h + i=j
     */
    public void testLdapRDNCompositeMultivaluedAttribute() throws InvalidNameException
    {
        LdapRDN rdn = new LdapRDN( "a =b+c=d + e=f + g  =h + i =j " );
        
        // NameComponent are not ordered
        Assert.assertEquals( "b", rdn.getValue("a") );
        Assert.assertEquals( "d", rdn.getValue("c") );
        Assert.assertEquals( "f", rdn.getValue("  E  ") );
        Assert.assertEquals( "h", rdn.getValue("g") );
        Assert.assertEquals( "j", rdn.getValue("i") );
    }

    /**
     * test a simple RDN with an oid prefix (uppercase) : OID.12.34.56 = azerty
     */
    public void testLdapRDNOidUpper() throws InvalidNameException
    {
        Assert.assertEquals( "oid.12.34.56=azerty",
                new LdapRDN( "OID.12.34.56 =  azerty" ).toString() );
    }

    /**
     * test a simple RDN with an oid prefix (lowercase) : oid.12.34.56 = azerty
     */
    public void testLdapRDNOidLower() throws InvalidNameException
    {
        Assert.assertEquals( "oid.12.34.56=azerty",
                new LdapRDN( "oid.12.34.56 = azerty" ).toString() );
    }

    /**
     * test a simple RDN with an oid attribut wiithout oid prefix : 12.34.56 = azerty
     */
    public void testLdapRDNOidWithoutPrefix() throws InvalidNameException
    {
        Assert.assertEquals( "12.34.56=azerty",
                new LdapRDN( "12.34.56 = azerty" ).toString() );
    }

    /**
     * test a composite RDN with an oid attribut wiithout oid prefix : 12.34.56 = azerty; 7.8 = test
     */
    public void testLdapRDNCompositeOidWithoutPrefix() throws InvalidNameException
    {
        String result = new LdapRDN( "12.34.56 = azerty + 7.8 = test" ).toString(); 
        Assert.assertEquals( "12.34.56=azerty+7.8=test", result );
    }

    /**
     * test a simple RDN with pair char attribute value : a = \,\=\+\<\>\#\;\\\"\A0\00"
     */
    public void testLdapRDNPairCharAttributeValue() throws InvalidNameException
    {
        Assert.assertEquals( "a=\\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00",
                new LdapRDN( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00" ).toString() );
    }

    /**
     * test a simple RDN with hexString attribute value : a = #0010A0AAFF
     */
    public void testLdapRDNHexStringAttributeValue() throws InvalidNameException
    {
        Assert.assertEquals( "a=#0010A0AAFF",
                new LdapRDN( "a = #0010A0AAFF" ).toString() );
    }

    /**
     * test a simple RDN with quoted attribute value : a = "quoted \"value"
     */
    public void testLdapRDNQuotedAttributeValue() throws InvalidNameException
    {
        Assert.assertEquals( "a=quoted \\\"value",
                new LdapRDN( "a = quoted \\\"value" ).toString() );
    }
    
    /**
     * Test the clone method for a RDN.
     */
    public void testRDNCloningOneNameComponent()  throws InvalidNameException
    {
        LdapRDN rdn = new LdapRDN( "a", "b" );
        
        LdapRDN rdnClone = (LdapRDN)rdn.clone();
        
        rdn.removeAttributeTypeAndValue( "a" );
        
        RDNParser.parse( "c=d", rdn );
        
        Assert.assertEquals( "b" , rdnClone.getValue( "a" ) );
    }

    /**
     * Test the clone method for a RDN.
     */
    public void testRDNCloningTwoNameComponent()  throws InvalidNameException
    {
        LdapRDN rdn = new LdapRDN( "a", "b" );
        rdn.addAttributeTypeAndValue( "aa", "bb" );
        
        LdapRDN rdnClone = (LdapRDN)rdn.clone();
        
        rdn.removeAttributeTypeAndValue( "aa" );
        rdn.clear();
        RDNParser.parse( "c=d", rdn );
        
        Assert.assertEquals( "b" , rdnClone.getValue( "a" ) );
        Assert.assertEquals( "bb" , rdnClone.getValue( "aa" ) );
        Assert.assertEquals( "" , rdnClone.getValue( "c" ) );
    }
    
    /**
     * Test the clone method for a RDN.
     */
    public void testRDNRemoveNameComponent() throws InvalidNameException
    {
        LdapRDN rdn = new LdapRDN( " a = b + c = d + a = f + g = h " );
        rdn.removeAttributeTypeAndValue( "a" );
        Assert.assertEquals( "c=d+g=h", rdn.toString() );
    }

    /**
     * Test the compareTo method for a RDN.
     */
    public void testRDNCompareToNull() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b + c = d + a = f + g = h " );
        LdapRDN rdn2 = null;
        Assert.assertEquals( LdapRDN.NOT_EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Test the compareTo method for a RDN.
     */
    public void testRDNCompareToNC2Null() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b + c = d + a = f + g = h " );
        LdapRDN rdn2 = new LdapRDN( " a = b " );
        Assert.assertEquals( LdapRDN.NOT_EQUALS, rdn1.compareTo( rdn2 ) );
    }
    
    /**
     * Compares a composite NC to a single NC.
     */
    public void testRDNCompareToNCS2NC() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b + c = d + a = f + g = h " );
        LdapRDN rdn2 = new LdapRDN( " a = b " );
        Assert.assertEquals( LdapRDN.NOT_EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a single NC to a composite NC.
     */
    public void testRDNCompareToNC2NCS() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b " );
        LdapRDN rdn2 = new LdapRDN( " a = b + c = d + a = f + g = h " );

        Assert.assertEquals( LdapRDN.NOT_EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a composite NCS to a composite NCS in the same order.
     */
    public void testRDNCompareToNCS2NCSOrdered() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b + c = d + a = f + g = h " );
        LdapRDN rdn2 = new LdapRDN( " a = b + c = d + a = f + g = h " );

        Assert.assertEquals( LdapRDN.EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a composite NCS to a composite NCS in a different order.
     */
    public void testRDNCompareToNCS2NCSUnordered() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b + a = f + g = h + c = d " );
        LdapRDN rdn2 = new LdapRDN( " a = b + c = d + a = f + g = h " );

        Assert.assertEquals( LdapRDN.EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a composite NCS to a different composite NCS.
     */
    public void testRDNCompareToNCS2NCSNotEquals() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = f + g = h + c = d " );
        LdapRDN rdn2 = new LdapRDN( " c = d + a = h + g = h " );

        Assert.assertEquals( LdapRDN.NOT_EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a simple NC to a simple NC.
     */
    public void testRDNCompareToNC2NC() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b " );
        LdapRDN rdn2 = new LdapRDN( " a = b " );

        Assert.assertEquals( LdapRDN.EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a simple NC to a simple NC in UperCase.
     */
    public void testRDNCompareToNC2NCUperCase() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b " );
        LdapRDN rdn2 = new LdapRDN( " A = B " );

        Assert.assertEquals( LdapRDN.EQUALS, rdn1.compareTo( rdn2 ) );
    }

    /**
     * Compares a simple NC to a different simple NC.
     */
    public void testRDNCompareToNC2NCNotEquals() throws InvalidNameException
    {
        LdapRDN rdn1 = new LdapRDN( " a = b " );
        LdapRDN rdn2 = new LdapRDN( " A = d " );

        Assert.assertEquals( LdapRDN.NOT_EQUALS, rdn1.compareTo( rdn2 ) );
    }
}


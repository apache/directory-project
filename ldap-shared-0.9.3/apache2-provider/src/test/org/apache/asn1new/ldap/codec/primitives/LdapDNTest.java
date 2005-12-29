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

import java.util.Enumeration;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.ldap.codec.primitives.LdapDN;

/**
 * Test the class LdapDN
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapDNTest extends TestCase
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
     * Test a null DN
     */
    public void testLdapDNNull() throws DecoderException
    {
        Assert.assertEquals( "", new LdapDN().getName() );
    }

    /**
     * test an empty DN
     */
    public void testLdapDNEmpty() throws InvalidNameException
    {
        Assert.assertEquals( "", new LdapDN( "" ).getName() );
    }

    /**
     * test a simple DN : a = b
     */
    public void testLdapDNSimple() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        Assert.assertEquals( "a = b", dn.getName() );
        Assert.assertEquals( "a=b", dn.toString() );
    }

    /**
     * test a composite DN : a = b, d = e
     */
    public void testLdapDNComposite() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b, c = d" );
        Assert.assertEquals( "a=b,c=d", dn.toString() );
        Assert.assertEquals( "a = b, c = d", dn.getName() );
    }

    /**
     * test a composite DN with or without spaces: a=b, a =b, a= b, a = b, a  =  b
     */
    public void testLdapDNCompositeWithSpace() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, a =b, a= b, a = b, a  =  b" );
        Assert.assertEquals( "a=b,a=b,a=b,a=b,a=b", dn.toString() );
        Assert.assertEquals( "a=b, a =b, a= b, a = b, a  =  b", dn.getName() );
    }

    /**
     * test a composite DN with differents separators : a=b;c=d,e=f
     * It should return a=b,c=d,e=f (the ';' is replaced by a ',')
     */
    public void testLdapDNCompositeSepators() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b;c=d,e=f" );
        Assert.assertEquals( "a=b,c=d,e=f", dn.toString() );
        Assert.assertEquals( "a=b;c=d,e=f", dn.getName() );
    }

    /**
     * test a simple DN with multiple NameComponents : a = b + c = d
     */
    public void testLdapDNSimpleMultivaluedAttribute() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b + c = d" );
        Assert.assertEquals( "a=b+c=d", dn.toString() );
        Assert.assertEquals( "a = b + c = d", dn.getName() );
    }

    /**
     * test a composite DN with multiple NC and separators : a=b+c=d, e=f + g=h + i=j
     */
    public void testLdapDNCompositeMultivaluedAttribute() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b+c=d, e=f + g=h + i=j" );
        Assert.assertEquals( "a=b+c=d,e=f+g=h+i=j", dn.toString() );
        Assert.assertEquals( "a=b+c=d, e=f + g=h + i=j", dn.getName() );
    }

    /**
     * test a simple DN with an oid prefix (uppercase) : OID.12.34.56 = azerty
     */
    public void testLdapDNOidUpper() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "OID.12.34.56 = azerty" );
        Assert.assertEquals( "oid.12.34.56=azerty", dn.toString() );
        Assert.assertEquals( "OID.12.34.56 = azerty", dn.getName() );
    }

    /**
     * test a simple DN with an oid prefix (lowercase) : oid.12.34.56 = azerty
     */
    public void testLdapDNOidLower() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "oid.12.34.56 = azerty" );
        Assert.assertEquals( "oid.12.34.56=azerty", dn.toString() );
        Assert.assertEquals( "oid.12.34.56 = azerty", dn.getName() );
    }

    /**
     * test a simple DN with an oid attribut without oid prefix : 12.34.56 = azerty
     */
    public void testLdapDNOidWithoutPrefix() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "12.34.56 = azerty" );
        Assert.assertEquals( "12.34.56=azerty", dn.toString() );
        Assert.assertEquals( "12.34.56 = azerty", dn.getName() );
    }

    /**
     * test a composite DN with an oid attribut wiithout oid prefix : 12.34.56 = azerty; 7.8 = test
     */
    public void testLdapDNCompositeOidWithoutPrefix() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "12.34.56 = azerty; 7.8 = test" );
        Assert.assertEquals( "12.34.56=azerty,7.8=test", dn.toString() );
        Assert.assertEquals( "12.34.56 = azerty; 7.8 = test", dn.getName() );
    }

    /**
     * test a simple DN with pair char attribute value : a = \,\=\+\<\>\#\;\\\"\A0\00"
     */
    public void testLdapDNPairCharAttributeValue() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00" );
        Assert.assertEquals( "a=\\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00", dn.toString() );
        Assert.assertEquals( "a = \\,\\=\\+\\<\\>\\#\\;\\\\\\\"\\A0\\00", dn.getName() );
    }

    /**
     * test a simple DN with hexString attribute value : a = #0010A0AAFF
     */
    public void testLdapDNHexStringAttributeValue() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = #0010A0AAFF" );
        Assert.assertEquals( "a=#0010A0AAFF", dn.toString() );
        Assert.assertEquals( "a = #0010A0AAFF", dn.getName() );
    }

    /**
     * test a simple DN with quoted attribute value : a = "quoted \"value"
     */
    public void testLdapDNQuotedAttributeValue() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = quoted \\\"value" );
        Assert.assertEquals( "a=quoted \\\"value", dn.toString() );
        Assert.assertEquals( "a = quoted \\\"value", dn.getName() );
    }

    // REMOVE operation -------------------------------------------------------
    
    /**
     * test a remove from position 0
     */
    public void testLdapDNRemove0() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d, e=f" );
        Assert.assertEquals( "e=f", dn.remove( 0 ).toString() );
        Assert.assertEquals( "a=b,c=d", dn.toString() );
        Assert.assertEquals( "a=b,c=d", dn.getName() );
    }

    /**
     * test a remove from position 1
     */
    public void testLdapDNRemove1() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d, e=f" );
        Assert.assertEquals( "c=d", dn.remove( 1 ).toString() );
        Assert.assertEquals( "a=b,e=f", dn.getName() );
    }
    
    /**
     * test a remove from position 2
     */
    public void testLdapDNRemove2() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d, e=f" );
        Assert.assertEquals( "a=b", dn.remove( 2 ).toString() );
        Assert.assertEquals( "c=d,e=f", dn.getName() );
    }

    /**
     * test a remove from position 1 whith semi colon
     */
    public void testLdapDNRemove1WithSemiColon() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d; e=f" );
        Assert.assertEquals( "c=d", dn.remove( 1 ).toString() );
        Assert.assertEquals( "a=b,e=f", dn.getName() );
    }

    /**
     * test a remove out of bound
     */
    public void testLdapDNRemoveOutOfBound() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d; e=f" );
        
        try
        {
            dn.remove( 4 );
            // We whould never reach this point
            Assert.fail();
        }
        catch ( ArrayIndexOutOfBoundsException aoobe )
        {
            Assert.assertTrue( true );
        }
    }
    
    // SIZE operations
    /**
     * test a 0 size 
     */
    public void testLdapDNSize0() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        Assert.assertEquals( 0, dn.size() );
    }

    /**
     * test a 1 size 
     */
    public void testLdapDNSize1() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b" );
        Assert.assertEquals( 1, dn.size() );
    }

    /**
     * test a 3 size 
     */
    public void testLdapDNSize3() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d, e=f" );
        Assert.assertEquals( 3, dn.size() );
    }

    /**
     * test a 3 size with NameComponents
     */
    public void testLdapDNSize3NC() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b+c=d, c=d, e=f" );
        Assert.assertEquals( 3, dn.size() );
    }

    /**
     * test size after operations
     */
    public void testLdapResizing() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        Assert.assertEquals( 0, dn.size() );
        
        dn.add( "e = f" );
        Assert.assertEquals( 1, dn.size() );
        
        dn.add( "c = d" );
        Assert.assertEquals( 2, dn.size() );

        dn.remove( 0 );
        Assert.assertEquals( 1, dn.size() );
        
        dn.remove( 0 );
        Assert.assertEquals( 0, dn.size() );
    }
    
    // ADD Operations
    /**
     * test Add on a new LdapDN
     */
    public void testLdapEmptyAdd() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        
        dn.add( "e = f" );
        Assert.assertEquals( "e=f" , dn.toString() );
        Assert.assertEquals( "e=f" , dn.getName() );
        Assert.assertEquals( 1, dn.size() );
    }
    
    /**
     * test Add to an existing LdapDN
     */
    public void testLdapDNAdd() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d");
        
        dn.add( "e = f" );
        Assert.assertEquals( "e=f,a=b,c=d" , dn.toString() );
        Assert.assertEquals( "e=f,a=b,c=d" , dn.getName() );
        Assert.assertEquals( 3, dn.size() );
    }

    /**
     * test Add a composite RDN to an existing LdapDN
     */
    public void testLdapDNAddComposite() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d");
        
        dn.add( "e = f + g = h" );
        
        // Warning ! The order of AVAs has changed during the parsing
        // This has no impact on the correctness of the DN, but the
        // String used to do the comparizon should be inverted.
        Assert.assertEquals( "g=h+e=f,a=b,c=d" , dn.toString() );
        Assert.assertEquals( 3, dn.size() );
    }
    
    /**
     * test Add at the end of an existing LdapDN
     */
    public void testLdapDNAddEnd() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d");
        
        dn.add( dn.size(), "e = f" );
        Assert.assertEquals( "e=f,a=b,c=d" , dn.getName() );
        Assert.assertEquals( 3, dn.size() );
    }

    /**
     * test Add at the start of an existing LdapDN
     */
    public void testLdapDNAddStart() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d");
        
        dn.add( 0, "e = f" );
        Assert.assertEquals( "a=b,c=d,e=f" , dn.getName() );
        Assert.assertEquals( 3, dn.size() );
    }

    /**
     * test Add at the middle of an existing LdapDN
     */
    public void testLdapDNAddMiddle() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d");
        
        dn.add( 1, "e = f" );
        Assert.assertEquals( "a=b,e=f,c=d" , dn.getName() );
        Assert.assertEquals( 3, dn.size() );
    }

    // ADD ALL Operations
    /**
     * Test AddAll 
     * @throws InvalidNameException
     */
    public void testLdapDNAddAll() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        LdapDN dn2 = new LdapDN( "c = d" );
        dn.addAll( dn2 );
        Assert.assertEquals( "c=d,a=b", dn.getName() );
    }
    
    /**
     * Test AddAll with an empty added name
     * @throws InvalidNameException
     */
    public void testLdapDNAddAllAddedNameEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        LdapDN dn2 = new LdapDN();
        dn.addAll( dn2 );
        Assert.assertEquals( "a=b", dn.toString() );
        Assert.assertEquals( "a = b", dn.getName() );
    }

    /**
     * Test AddAll to an empty name
     * @throws InvalidNameException
     */
    public void testLdapDNAddAllNameEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        LdapDN dn2 = new LdapDN( "a = b" );
        dn.addAll( dn2 );
        Assert.assertEquals( "a=b", dn.getName() );
    }
    
    /**
     * Test AddAll at position 0
     * @throws InvalidNameException
     */
    public void testLdapDNAt0AddAll() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        LdapDN dn2 = new LdapDN( "c = d" );
        dn.addAll( 0, dn2 );
        Assert.assertEquals( "a=b,c=d", dn.getName() );
    }
    
    /**
     * Test AddAll at position 1
     * @throws InvalidNameException
     */
    public void testLdapDNAt1AddAll() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        LdapDN dn2 = new LdapDN( "c = d" );
        dn.addAll( 1, dn2 );
        Assert.assertEquals( "c=d,a=b", dn.getName() );
    }
    
    /**
     * Test AddAll at the middle
     * @throws InvalidNameException
     */
    public void testLdapDNAtTheMiddleAddAll() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b, c = d" );
        LdapDN dn2 = new LdapDN( "e = f" );
        dn.addAll( 1, dn2 );
        Assert.assertEquals( "a=b,e=f,c=d", dn.getName() );
    }
    
    /**
     * Test AddAll with an empty added name at position 0
     * @throws InvalidNameException
     */
    public void testLdapDNAddAllAt0AddedNameEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        LdapDN dn2 = new LdapDN();
        dn.addAll( 0, dn2 );
        Assert.assertEquals( "a=b", dn.toString() );
        Assert.assertEquals( "a = b", dn.getName() );
    }

    /**
     * Test AddAll to an empty name at position 0
     * @throws InvalidNameException
     */
    public void testLdapDNAddAllAt0NameEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        LdapDN dn2 = new LdapDN( "a = b" );
        dn.addAll( 0, dn2 );
        Assert.assertEquals( "a=b", dn.getName() );
    }
    
    // GET PREFIX actions
    /**
     * Get the prefix at pos 0 
     */
    public void testLdapDNGetPrefixPos0() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getPrefix( 0 ));
         Assert.assertEquals( "", newDn.getName() );
    }

    /**
     * Get the prefix at pos 1 
     */
    public void testLdapDNGetPrefixPos1() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getPrefix( 1 ));
        Assert.assertEquals( "e=f", newDn.getName() );
    }
    
    /**
     * Get the prefix at pos 2 
     */
    public void testLdapDNGetPrefixPos2() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getPrefix( 2 ));
        Assert.assertEquals( "c=d,e=f", newDn.getName() );
    }
    
    /**
     * Get the prefix at pos 3 
     */
    public void testLdapDNGetPrefixPos3() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getPrefix( 3 ));
        Assert.assertEquals( "a=b,c=d,e=f", newDn.getName() );
    }

    /**
     * Get the prefix out of bound 
     */
    public void testLdapDNGetPrefixPos4() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        
        try
        {
            dn.getPrefix( 4 );
            // We should not reach this point.
            Assert.fail();
        }
        catch ( ArrayIndexOutOfBoundsException aoobe )
        {
            Assert.assertTrue( true );
        }
    }

    /**
     * Get the prefix of an empty LdapName
     */
    public void testLdapDNGetPrefixEmptyDN() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        LdapDN newDn = ((LdapDN)dn.getPrefix( 0 ));
        Assert.assertEquals( "", newDn.getName() );
    }
    
    // GET SUFFIX operations 
    /**
     * Get the suffix at pos 0 
     */
    public void testLdapDNGetSuffixPos0() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getSuffix( 0 ));
         Assert.assertEquals( "a=b,c=d,e=f", newDn.getName() );
    }

    /**
     * Get the suffix at pos 1 
     */
    public void testLdapDNGetSuffixPos1() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getSuffix( 1 ));
        Assert.assertEquals( "a=b,c=d", newDn.getName() );
    }
    
    /**
     * Get the suffix at pos 2 
     */
    public void testLdapDNGetSuffixPos2() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getSuffix( 2 ));
        Assert.assertEquals( "a=b", newDn.getName() );
    }
    
    /**
     * Get the suffix at pos 3 
     */
    public void testLdapDNGetSuffixPos3() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        LdapDN newDn = ((LdapDN)dn.getSuffix( 3 ));
        Assert.assertEquals( "", newDn.getName() );
    }

    /**
     * Get the suffix out of bound 
     */
    public void testLdapDNGetSuffixPos4() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        
        try
        {
            dn.getSuffix( 4 );
            // We should not reach this point.
            Assert.fail();
        }
        catch ( ArrayIndexOutOfBoundsException aoobe )
        {
            Assert.assertTrue( true );
        }
    }

    /**
     * Get the suffix of an empty LdapName
     */
    public void testLdapDNGetSuffixEmptyDN() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        LdapDN newDn = ((LdapDN)dn.getSuffix( 0 ));
        Assert.assertEquals( "", newDn.getName() );
    }
    
    // IS EMPTY operations
    /**
     * Test that a LdapDN is empty
     */
    public void testLdapDNIsEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        Assert.assertEquals( true, dn.isEmpty() );
    }
    
    /**
     * Test that a LdapDN is empty
     */
    public void testLdapDNNotEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b" );
        Assert.assertEquals( false, dn.isEmpty() );
    }

    /**
     * Test that a LdapDN is empty
     */
    public void testLdapDNRemoveIsEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d" );
        dn.remove(0);
        dn.remove(0);
        
        Assert.assertEquals( true, dn.isEmpty() );
    }
    
    // STARTS WITH operations
    /**
     * Test a startsWith a null LdapDN
     */
    public void testLdapDNStartsWithNull() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.startsWith( null ) );
    }
    
    /**
     * Test a startsWith an empty LdapDN
     */
    public void testLdapDNStartsWithEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.startsWith( new LdapDN() ) );
    }
    
    /**
     * Test a startsWith an simple LdapDN
     */
    public void testLdapDNStartsWithSimple() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.startsWith( new LdapDN( "e=f" ) ) );
    }
    
    /**
     * Test a startsWith a complex LdapDN
     */
    public void testLdapDNStartsWithComplex() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.startsWith( new LdapDN( "c =  d, e =  f" ) ) );
    }

    /**
     * Test a startsWith a complex LdapDN
     */
    public void testLdapDNStartsWithComplexMixedCase() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.startsWith( new LdapDN( "c =  D, E =  f" ) ) );
    }

    /**
     * Test a startsWith a full LdapDN
     */
    public void testLdapDNStartsWithFull() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.startsWith( new LdapDN( "a=  b; c =  d, e =  f" ) ) );
    }
    
    /**
     * Test a startsWith which returns false
     */
    public void testLdapDNStartsWithWrong() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( false, dn.startsWith( new LdapDN( "c =  t, e =  f" ) ) );
    }
    
    // ENDS WITH operations
    /**
     * Test a endsWith a null LdapDN
     */
    public void testLdapDNEndsWithNull() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.endsWith( null ) );
    }
    
    /**
     * Test a endsWith an empty LdapDN
     */
    public void testLdapDNEndsWithEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.endsWith( new LdapDN() ) );
    }
    
    /**
     * Test a endsWith an simple LdapDN
     */
    public void testLdapDNEndsWithSimple() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.endsWith( new LdapDN( "a=b" ) ) );
    }
    
    /**
     * Test a endsWith a complex LdapDN
     */
    public void testLdapDNEndsWithComplex() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.endsWith( new LdapDN( "a =  b, c =  d" ) ) );
    }

    /**
     * Test a endsWith a complex LdapDN
     */
    public void testLdapDNEndsWithComplexMixedCase() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.endsWith( new LdapDN( "a =  B, C =  d" ) ) );
    }

    /**
     * Test a endsWith a full LdapDN
     */
    public void testLdapDNEndsWithFull() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( true, dn.endsWith( new LdapDN( "a=  b; c =  d, e =  f" ) ) );
    }
    
    /**
     * Test a endsWith which returns false
     */
    public void testLdapDNEndsWithWrong() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b, c=d,e = f" );
        Assert.assertEquals( false, dn.endsWith( new LdapDN( "a =  b, e =  f" ) ) );
    }
    
    // GET ALL operations
    /**
     * test a getAll operation on a null DN
     */
    public void testLdapDNGetAllNull() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        Enumeration nc = dn.getAll();
        
        Assert.assertEquals( false, nc.hasMoreElements() );
    }

    /**
     * test a getAll operation on an empty DN
     */
    public void testLdapDNGetAllEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN("");
        Enumeration nc = dn.getAll();
        
        Assert.assertEquals( false, nc.hasMoreElements() );
    }
    
    /**
     * test a getAll operation on a simple DN
     */
    public void testLdapDNGetAllSimple() throws InvalidNameException
    {
        LdapDN dn = new LdapDN("a=b");
        Enumeration nc = dn.getAll();
        
        Assert.assertEquals( true, nc.hasMoreElements() );
        Assert.assertEquals( "a=b", nc.nextElement() );
        Assert.assertEquals( false, nc.hasMoreElements() );
    }
    
    /**
     * test a getAll operation on a complex DN
     */
    public void testLdapDNGetAllComplex() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "e=f+g=h,a=b,c=d" );
        Enumeration nc = dn.getAll();
        
        Assert.assertEquals( true, nc.hasMoreElements() );
        Assert.assertEquals( "c=d", nc.nextElement() );
        Assert.assertEquals( true, nc.hasMoreElements() );
        Assert.assertEquals( "a=b", nc.nextElement() );
        Assert.assertEquals( true, nc.hasMoreElements() );
        Assert.assertEquals( "e=f+g=h", nc.nextElement() );
        Assert.assertEquals( false, nc.hasMoreElements() );
    }
    
    // CLONE Operation
    /**
     * test a clone operation on a empty DN
     */
    public void testLdapDNCloneEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        LdapDN clone = (LdapDN)dn.clone();
        
        Assert.assertEquals( "", clone.getName() );
    }
    
    /**
     * test a clone operation on a simple DN
     */
    public void testLdapDNCloneSimple() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a=b" );
        LdapDN clone = (LdapDN)dn.clone();
        
        Assert.assertEquals( "a=b", clone.getName() );
        dn.remove( 0 );
        Assert.assertEquals( "a=b", clone.getName() );
    }
    
    /**
     * test a clone operation on a complex DN
     */
    public void testLdapDNCloneComplex() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "e=f+g=h,a=b,c=d" );
        LdapDN clone = (LdapDN)dn.clone();
        
        Assert.assertEquals( "e=f+g=h,a=b,c=d", clone.getName() );
        dn.remove( 2 );
        Assert.assertEquals( "e=f+g=h,a=b,c=d", clone.getName() );
    }
    
    // GET operations
    /**
     * test a get in a null DN
     */
    public void testLdapDNGetNull() throws InvalidNameException
    {
        LdapDN dn = new LdapDN();
        Assert.assertEquals( "", dn.get( 0 ) );
    }

    /**
     * test a get in an empty DN
     */
    public void testLdapDNGetEmpty() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "" );
        Assert.assertEquals( "", dn.get( 0 ) );
    }
    
    /**
     * test a get in a simple DN
     */
    public void testLdapDNGetSimple() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b" );
        Assert.assertEquals( "a=b", dn.get( 0 ) );
    }
    
    /**
     * test a get in a complex DN
     */
    public void testLdapDNGetComplex() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b + c= d, e= f; g =h" );
        Assert.assertEquals( "g=h", dn.get( 0 ) );
        Assert.assertEquals( "e=f", dn.get( 1 ) );
        Assert.assertEquals( "a=b+c=d", dn.get( 2 ) );
    }
    
    /**
     * test a get out of bound
     */
    public void testLdapDNGetOutOfBound() throws InvalidNameException
    {
        LdapDN dn = new LdapDN( "a = b + c= d, e= f; g =h" );
        
        try
        {
            dn.get( 4 );
            Assert.fail();
        }
        catch ( ArrayIndexOutOfBoundsException aioob )
        {
            Assert.assertTrue( true );
        }
    }
    
    /**
     * Tests the examples from the JNDI tutorials to make sure LdapName behaves
     * appropriately.  The example can be found online 
     * <a href="">here</a>.
     * 
     * @throws Exception if anything goes wrong
     */
    public void testJNDITutorialExample()
        throws Exception
    {
        // Parse the name
        Name name = new LdapDN( "cn=John,ou=People,ou=Marketing" );
        
        // Remove the second component from the head: ou=People
        String out = name.remove( 1 ).toString() ;
        
        // System.out.println( l_out ) ;
        assertEquals( "ou=People", out ) ;

        // Add to the head (first): cn=John,ou=Marketing,ou=East
        out = name.add( 0, "ou=East" ).toString() ;
        
        assertEquals( "cn=John,ou=Marketing,ou=East", out ) ;
        
        // Add to the tail (last): cn=HomeDir,cn=John,ou=Marketing,ou=East
        out = name.add( "cn=HomeDir" ).toString() ;
        
        assertEquals( "cn=HomeDir,cn=John,ou=Marketing,ou=East", out ) ;
    }
    
    public void testAttributeEqualsIsCaseInSensitive() throws Exception
    {
        Name name1 = new LdapDN( "cn=HomeDir" );
        Name name2 = new LdapDN( "CN=HomeDir" );

        assertTrue( name1.equals( name2 ) );
    }


    public void testAttributeTypeEqualsIsCaseInsensitive() throws Exception
    {
        Name name1 = new LdapDN( "cn=HomeDir+cn=WorkDir" );
        Name name2 = new LdapDN( "cn=HomeDir+CN=WorkDir" );

        assertTrue( name1.equals( name2 ) );
    }


    public void testNameEqualsIsInsensitiveToAttributesOrder() throws Exception
    {

        Name name1 = new LdapDN( "cn=HomeDir+cn=WorkDir" );
        Name name2 = new LdapDN( "cn=WorkDir+cn=HomeDir" );

        assertTrue( name1.equals( name2 ) );
    }


    public void testAttributeComparisonIsCaseInSensitive() throws Exception
    {
        Name name1 = new LdapDN( "cn=HomeDir" );
        Name name2 = new LdapDN( "CN=HomeDir" );

        assertEquals( 0, name1.compareTo( name2 ) );
    }


    public void testAttributeTypeComparisonIsCaseInsensitive() throws Exception
    {
        Name name1 = new LdapDN( "cn=HomeDir+cn=WorkDir" );
        Name name2 = new LdapDN( "cn=HomeDir+CN=WorkDir" );

        assertEquals( 0, name1.compareTo( name2 ) );
    }


    public void testNameComparisonIsInsensitiveToAttributesOrder() throws Exception
    {

        Name name1 = new LdapDN( "cn=HomeDir+cn=WorkDir" );
        Name name2 = new LdapDN( "cn=WorkDir+cn=HomeDir" );

        assertEquals( 0, name1.compareTo( name2 ) );
    }
    
    public void testNameComparisonIsInsensitiveToAttributesOrderFailure() throws Exception
    {

        Name name1 = new LdapDN( "cn=HomeDir+cn=Workdir" );
        Name name2 = new LdapDN( "cn=Work+cn=HomeDir" );

        assertEquals( -1, name1.compareTo( name2 ) );
    }
    
    /**
     * Test the encoding of a LdanDN
     *
     */
    public void testNameToBytes() throws Exception
    {
        LdapDN dn = new LdapDN( "cn = John, ou = People, OU = Marketing" );
        
        byte[] bytes = dn.getBytes();
        
        Assert.assertEquals( 30, dn.getNbBytes() );
        Assert.assertEquals("cn=John,ou=People,ou=Marketing", new String( bytes, "UTF-8" ) );
    }
    
    public void testStringParser() throws Exception
    {
        LdapDN name = DNParser.parse( "CN = Emmanuel  Lécharny" );
        
        Assert.assertEquals( "CN = Emmanuel  Lécharny", name.getName() );
        Assert.assertEquals( "cn=Emmanuel  Lécharny", name.toString() );
    }
}

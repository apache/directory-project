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
package org.apache.asn1new.ldap.codec;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.naming.directory.Attributes;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.Asn1Decoder;
import org.apache.asn1new.ber.containers.IAsn1Container;
import org.apache.asn1new.ldap.codec.primitives.LdapString;
import org.apache.asn1new.ldap.pojo.AttributeValueAssertion;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.ldap.pojo.SearchRequest;
import org.apache.asn1new.ldap.pojo.filters.AndFilter;
import org.apache.asn1new.ldap.pojo.filters.AttributeValueAssertionFilter;
import org.apache.asn1new.ldap.pojo.filters.NotFilter;
import org.apache.asn1new.ldap.pojo.filters.OrFilter;
import org.apache.asn1new.ldap.pojo.filters.PresentFilter;
import org.apache.asn1new.ldap.pojo.filters.SubstringFilter;
import org.apache.asn1new.util.StringUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * A test case for SearchRequest messages
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchRequestTest extends TestCase {
    /**
     * Test the decoding of a SearchRequest with no controls.
     * The search filter is : 
     * (&(|(objectclass=top)(ou=contacts))(!(objectclass=ttt)))
     */
    public void testDecodeSearchRequestGlobalNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x90 );
        stream.put(
            new byte[]
            {
                0x30, (byte)0x81, (byte)0x8D, // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, (byte)0x81, (byte)0x87,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,   	  //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA0, 0x3C,        	  // Filter ::= CHOICE {
				                         	  //    and             [0] SET OF Filter,
				(byte)0xA1, 0x24,        	  //    or              [1] SET of Filter,
				(byte)0xA3, 0x12,        	  //    equalityMatch   [3] AttributeValueAssertion,
									     	  // AttributeValueAssertion ::= SEQUENCE {
								 	          //    attributeDesc   AttributeDescription (LDAPString),
				0x04, 0x0B, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
									          //    assertionValue  AssertionValue (OCTET STRING) }
				0x04, 0x03, 't', 'o', 'p',
				(byte)0xA3, 0x0E,             //    equalityMatch   [3] AttributeValueAssertion,
				                              // AttributeValueAssertion ::= SEQUENCE {
				0x04, 0x02, 'o', 'u',         //    attributeDesc   AttributeDescription (LDAPString),
			                                  //    assertionValue  AssertionValue (OCTET STRING) }
				0x04, 0x08, 'c', 'o', 'n', 't', 'a', 'c', 't', 's',
				(byte)0xA2, 0x14,             //    not             [2] Filter,
				(byte)0xA3, 0x12,             //    equalityMatch   [3] AttributeValueAssertion,
			                                  // AttributeValueAssertion ::= SEQUENCE {
		 	                                  //    attributeDesc   AttributeDescription (LDAPString),
                0x04, 0x0B, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
			                                  //    assertionValue  AssertionValue (OCTET STRING) }
                0x04, 0x03, 't', 't', 't',
              						          //    attributes      AttributeDescriptionList }
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (& (...
        AndFilter andFilter = (AndFilter)sr.getFilter();
        Assert.assertNotNull(andFilter);
        
        ArrayList andFilters = andFilter.getAndFilter();
        
        // (& (| (...
        Assert.assertEquals(2, andFilters.size());
        OrFilter orFilter = (OrFilter)andFilters.get(0);
        Assert.assertNotNull(orFilter);
        
        // (& (| (obectclass=top) (...
        ArrayList orFilters = orFilter.getOrFilter();
        Assert.assertEquals(2, orFilters.size());
        AttributeValueAssertionFilter equalityMatch = (AttributeValueAssertionFilter)orFilters.get(0);  
        Assert.assertNotNull(equalityMatch);
        
        AttributeValueAssertion assertion = equalityMatch.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("objectclass", assertion.getAttributeDesc().toString());
        Assert.assertEquals("top", assertion.getAssertionValue().toString());
        
        // (& (| (objectclass=top) (ou=contacts) ) (...
        equalityMatch = (AttributeValueAssertionFilter)orFilters.get(1);  
        Assert.assertNotNull(equalityMatch);
        
        assertion = equalityMatch.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("ou", assertion.getAttributeDesc().toString());
        Assert.assertEquals("contacts", assertion.getAssertionValue().toString());
        
        // (& (| (objectclass=top) (ou=contacts) ) (! ...
        NotFilter notFilter = (NotFilter)andFilters.get(1);
        Assert.assertNotNull(notFilter);
        
        // (& (| (objectclass=top) (ou=contacts) ) (! (objectclass=ttt) ) )
        equalityMatch = (AttributeValueAssertionFilter)notFilter.getNotFilter();  
        Assert.assertNotNull(equalityMatch);
        
        assertion = equalityMatch.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("objectclass", assertion.getAttributeDesc().toString());
        Assert.assertEquals("ttt", assertion.getAssertionValue().toString());
        
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x81 ), decodedPdu.substring( 0, 0x81 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with no controls.
     * Test the various types of filter : >=, <=, ~=
     * The search filter is : 
     * (&(|(objectclass~=top)(ou<=contacts))(!(objectclass>=ttt)))
     */
    public void testDecodeSearchRequestCompareFiltersNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x90 );
        stream.put(
            new byte[]
            {
                0x30, (byte)0x81, (byte)0x8D, // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, (byte)0x81, (byte)0x87,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA0, 0x3C,        	  // Filter ::= CHOICE {
				                         	  //    and             [0] SET OF Filter,
				(byte)0xA1, 0x24,        	  //    or              [1] SET of Filter,
				(byte)0xA8, 0x12,        	  //    approxMatch     [8] AttributeValueAssertion,
									     	  // AttributeValueAssertion ::= SEQUENCE {
								 	          //    attributeDesc   AttributeDescription (LDAPString),
				0x04, 0x0B, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
									          //    assertionValue  AssertionValue (OCTET STRING) }
				0x04, 0x03, 't', 'o', 'p',
				(byte)0xA6, 0x0E,             //    lessOrEqual     [3] AttributeValueAssertion,
				                              // AttributeValueAssertion ::= SEQUENCE {
				0x04, 0x02, 'o', 'u',         //    attributeDesc   AttributeDescription (LDAPString),
			                                  //    assertionValue  AssertionValue (OCTET STRING) }
				0x04, 0x08, 'c', 'o', 'n', 't', 'a', 'c', 't', 's',
				(byte)0xA2, 0x14,             //    not             [2] Filter,
				(byte)0xA5, 0x12,             //    greaterOrEqual  [5] AttributeValueAssertion,
			                                  // AttributeValueAssertion ::= SEQUENCE {
		 	                                  //    attributeDesc   AttributeDescription (LDAPString),
                0x04, 0x0B, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
			                                  //    assertionValue  AssertionValue (OCTET STRING) }
                0x04, 0x03, 't', 't', 't',
              						          //    attributes      AttributeDescriptionList }
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (& (...
        AndFilter andFilter = (AndFilter)sr.getFilter();
        Assert.assertNotNull(andFilter);
        
        ArrayList andFilters = andFilter.getAndFilter();
        
        // (& (| (...
        Assert.assertEquals(2, andFilters.size());
        OrFilter orFilter = (OrFilter)andFilters.get(0);
        Assert.assertNotNull(orFilter);
        
        // (& (| (objectclass~=top) (...
        ArrayList orFilters = orFilter.getOrFilter();
        Assert.assertEquals(2, orFilters.size());
        AttributeValueAssertionFilter approxMatch = (AttributeValueAssertionFilter)orFilters.get(0);  
        Assert.assertNotNull(approxMatch);
        
        AttributeValueAssertion assertion = approxMatch.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("objectclass", assertion.getAttributeDesc().toString());
        Assert.assertEquals("top", assertion.getAssertionValue().toString());
        
        // (& (| (objectclass~=top) (ou<=contacts) ) (...
        AttributeValueAssertionFilter lessOrEqual = (AttributeValueAssertionFilter)orFilters.get(1);  
        Assert.assertNotNull(lessOrEqual);
        
        assertion = lessOrEqual.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("ou", assertion.getAttributeDesc().toString());
        Assert.assertEquals("contacts", assertion.getAssertionValue().toString());
        
        // (& (| (objectclass~=top) (ou<=contacts) ) (! ...
        NotFilter notFilter = (NotFilter)andFilters.get(1);
        Assert.assertNotNull(notFilter);
        
        // (& (| (objectclass~=top) (ou<=contacts) ) (! (objectclass>=ttt) ) )
        AttributeValueAssertionFilter greaterOrEqual = (AttributeValueAssertionFilter)notFilter.getNotFilter();  
        Assert.assertNotNull(greaterOrEqual);
        
        assertion = greaterOrEqual.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("objectclass", assertion.getAttributeDesc().toString());
        Assert.assertEquals("ttt", assertion.getAssertionValue().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x90, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x81 ), decodedPdu.substring( 0, 0x81 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with no controls.
     * Test the present filter : =*
     * The search filter is : 
     * (&(|(objectclass=*)(ou=*))(!(objectclass>=ttt)))
     */
    public void testDecodeSearchRequestPresentNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x7B );
        stream.put(
            new byte[]
            {
                0x30, 0x79,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x74,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA0, 0x29,        	  // Filter ::= CHOICE {
				                         	  //    and             [0] SET OF Filter,
				(byte)0xA1, 0x11,        	  //    or              [1] SET of Filter,
				(byte)0x87, 0x0B,        	  //    present         [7] AttributeDescription,
									     	  // AttributeDescription ::= LDAPString
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
									          //    assertionValue  AssertionValue (OCTET STRING) }
				(byte)0x87, 0x02, 'o', 'u',   //    present         [7] AttributeDescription,
				                              // AttributeDescription ::= LDAPString
				(byte)0xA2, 0x14,             //    not             [2] Filter,
				(byte)0xA5, 0x12,             //    greaterOrEqual  [5] AttributeValueAssertion,
			                                  // AttributeValueAssertion ::= SEQUENCE {
		 	                                  //    attributeDesc   AttributeDescription (LDAPString),
                0x04, 0x0B, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
			                                  //    assertionValue  AssertionValue (OCTET STRING) }
                0x04, 0x03, 't', 't', 't',
              						          //    attributes      AttributeDescriptionList }
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (& (...
        AndFilter andFilter = (AndFilter)sr.getFilter();
        Assert.assertNotNull(andFilter);
        
        ArrayList andFilters = andFilter.getAndFilter();
        
        // (& (| (...
        Assert.assertEquals(2, andFilters.size());
        OrFilter orFilter = (OrFilter)andFilters.get(0);
        Assert.assertNotNull(orFilter);
        
        // (& (| (objectclass=*) (...
        ArrayList orFilters = orFilter.getOrFilter();
        Assert.assertEquals(2, orFilters.size());

        PresentFilter presentFilter = (PresentFilter)orFilters.get(0);  
        Assert.assertNotNull(presentFilter);
        
        Assert.assertEquals("objectclass", presentFilter.getAttributeDescription().toString());
        
        // (& (| (objectclass=*) (ou=*) ) (...
        presentFilter = (PresentFilter)orFilters.get(1);  
        Assert.assertNotNull(presentFilter);
        
        Assert.assertEquals("ou", presentFilter.getAttributeDescription().toString());
        
        // (& (| (objectclass=*) (ou=*) ) (! ...
        NotFilter notFilter = (NotFilter)andFilters.get(1);
        Assert.assertNotNull(notFilter);
        
        // (& (| (objectclass=*) (ou=*) ) (! (objectclass>=ttt) ) )
        AttributeValueAssertionFilter greaterOrEqual = (AttributeValueAssertionFilter)notFilter.getNotFilter();  
        Assert.assertNotNull(greaterOrEqual);
        
        AttributeValueAssertion assertion = greaterOrEqual.getAssertion();
        Assert.assertNotNull(assertion);
        
        Assert.assertEquals("objectclass", assertion.getAttributeDesc().toString());
        Assert.assertEquals("ttt", assertion.getAssertionValue().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x7B, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x6C ), decodedPdu.substring( 0, 0x6C ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with no attributes.
     * The search filter is : 
     * (objectclass=*)
     */
    public void testDecodeSearchRequestNoAttributes()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x40 );
        stream.put(
            new byte[]
            {
                0x30, 0x37,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x03, 	          //        messageID MessageID
				0x63, 0x32,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x12, 		     	  //    baseObject LDAPDN,
				'o', 'u', '=', 'u', 's', 'e', 'r', 's', ',',  
				'o', 'u', '=', 's', 'y', 's', 't', 'e', 'm', 
				0x0A, 0x01, 0x00,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (infinite)
				0x02, 0x01, 0x00, 
                					     	  //    timeLimit INTEGER (0 .. maxInt), (infinite)
				0x02, 0x01, 0x00,
				0x01, 0x01, (byte)0x00,       //    typesOnly BOOLEAN, (FALSE)
				                         	  //    filter    Filter,
											  // Filter ::= CHOICE {
				(byte)0x87, 0x0B,             //    present         [7] AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'C', 'l', 'a', 's', 's',
              						          //    attributes      AttributeDescriptionList }
                0x30, 0x00,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x00, 0x00,					  // Some trailing 00, useless.
                0x00, 0x00,
                0x00, 0x00
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 3, message.getMessageId() );
        Assert.assertEquals( "ou=users,ou=system", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_BASE_OBJECT, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 0, sr.getSizeLimit() );
        Assert.assertEquals( 0, sr.getTimeLimit() );
        Assert.assertEquals( false, sr.isTypesOnly() );
        
        // (objectClass = *)
        PresentFilter presentFilter = (PresentFilter)sr.getFilter();
        Assert.assertNotNull(presentFilter);
        Assert.assertEquals("objectClass", presentFilter.getAttributeDescription().toString());
        
        // The attributes
        Attributes attributes = sr.getAttributes();
        
       	Assert.assertNull( attributes );

        // Check the length
        Assert.assertEquals(0x39, message.computeLength());

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu, decodedPdu.substring( 0, decodedPdu.length() - 35) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*)
     */
    public void testDecodeSearchRequestSubstringInitialAny()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x64 );
        stream.put(
            new byte[]
            {
                0x30, 0x62,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x5D,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x12,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x03,
				(byte)0x80, 0x01, 't',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals("t", substringFilter.getInitialSubstrings().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }

        // Check the length
        Assert.assertEquals(0x64, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x53 ), decodedPdu.substring( 0, 0x53 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*)
     */
    public void testDecodeSearchRequestSubstringAny()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x64 );
        stream.put(
            new byte[]
            {
                0x30, 0x62,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x5D,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x12,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x03,
				(byte)0x81, 0x01, 't',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals(null, substringFilter.getInitialSubstrings());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(0)).toString());
        Assert.assertEquals(null, substringFilter.getFinalSubstrings());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }

        // Check the length
        Assert.assertEquals(0x64, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x53 ), decodedPdu.substring( 0, 0x53 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*)
     */
    public void testDecodeSearchRequestSubstringAnyFinal()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x67 );
        stream.put(
            new byte[]
            {
                0x30, 0x65,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x60,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x15,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x06,
				(byte)0x81, 0x01, 't',        //
				(byte)0x82, 0x01, 't',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals(null, substringFilter.getInitialSubstrings());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(0)).toString());
        Assert.assertEquals("t", substringFilter.getFinalSubstrings().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x67, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x58 ), decodedPdu.substring( 0, 0x58 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*)
     */
    public void testDecodeSearchRequestSubstringInitialAnyFinal()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x6A );
        stream.put(
            new byte[]
            {
                0x30, 0x68,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x63,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x18,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x09,
				(byte)0x80, 0x01, 't',        //
				(byte)0x81, 0x01, 't',        //
				(byte)0x82, 0x01, 't',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals("t", substringFilter.getInitialSubstrings().toString());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(0)).toString());
        Assert.assertEquals("t", substringFilter.getFinalSubstrings().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x6A, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x5B ), decodedPdu.substring( 0, 0x5B ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*t*)
     */
    public void testDecodeSearchRequestSubstringInitialAnyAnyFinal()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x67 );
        stream.put(
            new byte[]
            {
                0x30, 0x65,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x60,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x15,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x06,
				(byte)0x80, 0x01, 't',        //
				(byte)0x81, 0x01, 't',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals("t", substringFilter.getInitialSubstrings().toString());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(0)).toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x67, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x58 ), decodedPdu.substring( 0, 0x58 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*)
     */
    public void testDecodeSearchRequestSubstringAnyAnyFinal()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x6A );
        stream.put(
            new byte[]
            {
                0x30, 0x68,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x63,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x18,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x09,
				(byte)0x81, 0x01, 't',        //
				(byte)0x81, 0x01, 't',        //
				(byte)0x82, 0x01, 't',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals(null, substringFilter.getInitialSubstrings());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(0)).toString());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(1)).toString());
        Assert.assertEquals("t", substringFilter.getFinalSubstrings().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x6A, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x5B ), decodedPdu.substring( 0, 0x5B ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=t*)
     */
    public void testDecodeSearchRequestSubstringInitialAnyAny()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x67 );
        stream.put(
            new byte[]
            {
                0x30, 0x65,                   // LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	          //        messageID MessageID
				0x63, 0x60,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				0x0A, 0x01, 0x01,        	  //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				0x0A, 0x01, 0x03,        	  //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				0x02, 0x02, 0x03, (byte)0xE8,
				0x01, 0x01, (byte)0xFF,       //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				(byte)0xA4, 0x15,        	  // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				0x04, 0x0B,                   //     type            AttributeDescription,
				'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				0x30, 0x06,
				(byte)0x80, 0x01, 't',        //
				(byte)0x81, 0x01, '*',        //
                0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().toString());
        Assert.assertEquals("t", substringFilter.getInitialSubstrings().toString());
        Assert.assertEquals("*", ((LdapString)substringFilter.getAnySubstrings().get(0)).toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x67, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x58 ), decodedPdu.substring( 0, 0x58 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a SearchRequest with a substring filter.
     * Test the initial filter : 
     * (objectclass=*t*t*t*)
     */
    public void testDecodeSearchRequestSubstringAnyAnyAny()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x6A );
        stream.put(
            new byte[]
            {
                0x30, 0x68,                   // LDAPMessage ::=SEQUENCE {
				  0x02, 0x01, 0x01, 	          //        messageID MessageID
				  0x63, 0x63,                   //	      CHOICE { ..., searchRequest SearchRequest, ...
                        			     	  // SearchRequest ::= APPLICATION[3] SEQUENCE {
				    0x04, 0x1F, 		     	  //    baseObject LDAPDN,
				    'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                    'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
				    0x0A, 0x01, 0x01,         //    scope           ENUMERATED {
                					     	  //        baseObject              (0),
				                         	  //        singleLevel             (1),
				                         	  //        wholeSubtree            (2) },
				    0x0A, 0x01, 0x03,         //    derefAliases    ENUMERATED {
									     	  //        neverDerefAliases       (0),
									     	  //        derefInSearching        (1),
									     	  //        derefFindingBaseObj     (2),
									     	  //        derefAlways             (3) },
				                         	  //    sizeLimit INTEGER (0 .. maxInt), (1000)
				    0x02, 0x02, 0x03, (byte)0xE8,
                					     	  //    timeLimit INTEGER (0 .. maxInt), (1000)
				    0x02, 0x02, 0x03, (byte)0xE8,
				    0x01, 0x01, (byte)0xFF,   //    typesOnly BOOLEAN, (TRUE)
				                         	  //    filter    Filter,
				    (byte)0xA4, 0x18,         // Filter ::= CHOICE {
				                         	  //    substrings      [4] SubstringFilter
											  // }
									     	  // SubstringFilter ::= SEQUENCE {
				      0x04, 0x0B,             //     type            AttributeDescription,
				      'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
				      0x30, 0x09,
				        (byte)0x81, 0x01, 't',    //
				        (byte)0x81, 0x01, 't',    //
				        (byte)0x81, 0x01, 't',    //
                    0x30, 0x15,				      // AttributeDescriptionList ::= SEQUENCE OF AttributeDescription
                      0x04, 0x05, 'a', 't', 't', 'r', '0', // AttributeDescription ::= LDAPString
                      0x04, 0x05, 'a', 't', 't', 'r', '1', // AttributeDescription ::= LDAPString
                      0x04, 0x05, 'a', 't', 't', 'r', '2'  // AttributeDescription ::= LDAPString
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_SINGLE_LEVEL, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_ALWAYS, sr.getDerefAliases() );
        Assert.assertEquals( 1000, sr.getSizeLimit() );
        Assert.assertEquals( 1000, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        SubstringFilter substringFilter = (SubstringFilter)sr.getFilter();
        Assert.assertNotNull(substringFilter);
        
        Assert.assertEquals("objectclass", substringFilter.getType().getString());
        Assert.assertEquals(null, substringFilter.getInitialSubstrings());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(0)).getString());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(1)).getString());
        Assert.assertEquals("t", ((LdapString)substringFilter.getAnySubstrings().get(2)).getString());
        Assert.assertEquals(null, substringFilter.getFinalSubstrings());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x6A, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x5B ), decodedPdu.substring( 0, 0x5B ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Tests an search request decode with a simple equality match filter.
     */
    public void testDecodeSearchRequestOrFilters()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x96 );
        stream.put(
            new byte[]
            {
                    0x30, 0xFFFFFF81, 0xFFFFFF93, 
                	0x02, 0x01, 0x21, 
                	0x63, 0xFFFFFF81, 0xFFFFFF8D, // "dc=example,dc=com"
                		0x04, 0x11, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 
                		0x0A, 0x01, 0x00, 
                		0x0A, 0x01, 0x02, 
                		0x02, 0x01, 0x02, 
                		0x02, 0x01, 0x03, 
                		0x01, 0x01, 0xFFFFFFFF, 
                		0xFFFFFFA1, 0x52, 								// ( | 
                			0xFFFFFFA3, 0x10, 							// ( uid=akarasulu )
                				0x04, 0x03, 0x75, 0x69, 0x64, 			
                				0x04, 0x09, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 
                			0xFFFFFFA3, 0x09, 							// ( cn=aok )
                				0x04, 0x02, 0x63, 0x6E, 
                				0x04, 0x03, 0x61, 0x6F, 0x6B, 
                			0xFFFFFFA3, 0x15, 							// ( ou = Human Resources )
                				0x04, 0x02, 0x6F, 0x75, 
                				0x04, 0x0F, 0x48, 0x75, 0x6D, 0x61, 0x6E, 0x20, 0x52, 0x65, 0x73, 0x6F, 0x75, 0x72, 0x63, 0x65, 0x73, 
                			0xFFFFFFA3, 0x10, 
                				0x04, 0x01, 0x6C, 						// ( l=Santa Clara )
                				0x04, 0x0B, 0x53, 0x61, 0x6E, 0x74, 0x61, 0x20, 0x43, 0x6C, 0x61, 0x72, 0x61, 
                			0xFFFFFFA3, 0x0A, 							// ( cn=abok )
                				0x04, 0x02, 0x63, 0x6E, 
                				0x04, 0x04, 0x61, 0x62, 0x6F, 0x6B, 
                		0x30, 0x15,										// Attributes 
                			0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x30, 	// attr0
                			0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x31, 	// attr1
                			0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x32	// attr2
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
           ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 33, message.getMessageId() );
        Assert.assertEquals( "dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_BASE_OBJECT, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_FINDING_BASE_OBJ, sr.getDerefAliases() );
        Assert.assertEquals( 2, sr.getSizeLimit() );
        Assert.assertEquals( 3, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // (objectclass=t*)
        OrFilter orFilter = (OrFilter)sr.getFilter();
        Assert.assertNotNull(orFilter);

        // uid=akarasulu
        AttributeValueAssertion assertion = ((AttributeValueAssertionFilter)orFilter.getOrFilter().get(0)).getAssertion();
        
        Assert.assertEquals("uid", assertion.getAttributeDesc().toString());
        Assert.assertEquals("akarasulu", assertion.getAssertionValue().toString());

        // cn=aok
        assertion = ((AttributeValueAssertionFilter)orFilter.getOrFilter().get(1)).getAssertion();
        
        Assert.assertEquals("cn", assertion.getAttributeDesc().toString());
        Assert.assertEquals("aok", assertion.getAssertionValue().toString());

        // ou = Human Resources
        assertion = ((AttributeValueAssertionFilter)orFilter.getOrFilter().get(2)).getAssertion();
        
        Assert.assertEquals("ou", assertion.getAttributeDesc().toString());
        Assert.assertEquals("Human Resources", assertion.getAssertionValue().toString());

        // l=Santa Clara
        assertion = ((AttributeValueAssertionFilter)orFilter.getOrFilter().get(3)).getAssertion();
        
        Assert.assertEquals("l", assertion.getAttributeDesc().toString());
        Assert.assertEquals("Santa Clara", assertion.getAssertionValue().toString());

        // cn=abok
        assertion = ((AttributeValueAssertionFilter)orFilter.getOrFilter().get(4)).getAssertion();
        
        Assert.assertEquals("cn", assertion.getAttributeDesc().toString());
        Assert.assertEquals("abok", assertion.getAssertionValue().toString());

        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x96, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x87 ), decodedPdu.substring( 0, 0x87 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }

    /**
     * Tests an search request decode with a simple equality match filter.
     */
    public void testDecodeSearchRequestExtensibleMatch()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x65 );
        stream.put(
            new byte[]
            {
                    0x30, 0x63, 
                	0x02, 0x01, 0x01, 
                	0x63, 0x5E, // "dc=example,dc=com"
                		0x04, 0x11, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 
                		0x0A, 0x01, 0x00, 
                		0x0A, 0x01, 0x02, 
                		0x02, 0x01, 0x02, 
                		0x02, 0x01, 0x03, 
                		0x01, 0x01, (byte)0xFF,
                		(byte)0xA9, 0x23,
                			0x30, 0x21,
                				(byte)0x81, 0x02, 'c', 'n',
                				(byte)0x82, 0x13, '1', '.', '2', '.', '8', '4', '0',   
                								  '.', '4', '8', '0', '1', '8', '.',  
                								  '1', '.', '2', '.', '2',
                	            (byte)0x83, 0x03, 'a', 'o', 'k',
                	            (byte)0x84, 0x01, (byte)0xFF,
                		0x30, 0x15,										// Attributes 
                			0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x30, 	// attr0
                			0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x31, 	// attr1
                			0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x32	// attr2
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
           ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        SearchRequest sr      = message.getSearchRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( "dc=example,dc=com", sr.getBaseObject().toString() );
        Assert.assertEquals( LdapConstants.SCOPE_BASE_OBJECT, sr.getScope() );
        Assert.assertEquals( LdapConstants.DEREF_FINDING_BASE_OBJ, sr.getDerefAliases() );
        Assert.assertEquals( 2, sr.getSizeLimit() );
        Assert.assertEquals( 3, sr.getTimeLimit() );
        Assert.assertEquals( true, sr.isTypesOnly() );
        
        // The attributes
        Attributes attributes = sr.getAttributes();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
        	Assert.assertNotNull( attributes.get( "attr" + i ) );
        }
        
        // Check the length
        Assert.assertEquals(0x65, message.computeLength());

        // Check the encoding
        // We won't check the whole PDU, as it may differs because
        // attributes may have been reordered
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu.substring( 0, 0x56 ), decodedPdu.substring( 0, 0x56 ) );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }
}
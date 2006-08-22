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
package org.apache.directory.shared.ldap.codec.bind;


import java.nio.ByteBuffer;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.LdapDecoder;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.bind.BindRequest;
import org.apache.directory.shared.ldap.codec.bind.SaslCredentials;
import org.apache.directory.shared.ldap.codec.bind.SimpleAuthentication;
import org.apache.directory.shared.ldap.util.StringTools;

import junit.framework.TestCase;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BindRequestTest extends TestCase
{
    /**
     * Test the decoding of a BindRequest with Simple authentication and no
     * controls
     */
    public void testDecodeBindRequestSimpleNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x52 );
        stream.put( new byte[]
            { 0x30, 0x50, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x2E, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0x80, 0x08, // authentication
                                                                                            // AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd', ( byte ) 0xA0, 0x1B, // A
                                                                                // control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        assertEquals( "password", StringTools.utf8ToString( ( ( SimpleAuthentication ) br.getAuthentication() )
            .getSimple() ) );

        // Check the Control
        List controls = message.getControls();

        assertEquals( 1, controls.size() );

        Control control = message.getControls( 0 );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getControlType() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getControlValue() ) );

        // Check the length
        assertEquals( 0x52, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Simple authentication and
     * controls
     */
    public void testDecodeBindRequestSimpleWithControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x35 );
        stream.put( new byte[]
            { 0x30, 0x33, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x2E, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0x80, 0x08, // authentication
                                                                                            // AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        assertEquals( "password", StringTools.utf8ToString( ( ( SimpleAuthentication ) br.getAuthentication() )
            .getSimple() ) );

        // Check the length
        assertEquals( 0x35, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Simple authentication, no name
     * and no controls
     */
    public void testDecodeBindRequestSimpleNoName()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x15 );
        stream.put( new byte[]
            { 0x30, 0x13, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0D, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                ( byte ) 0x80, 0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertEquals( "Cannot pop the grammar BIND_REQUEST_GRAMMAR for state BIND_REQUEST_NAME_TAG", de
                .getMessage() );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "Should never reach this point." );
    }


    /**
     * Test the decoding of a BindRequest with Simple authentication, empty name
     * (an anonymous bind) and no controls
     */
    public void testDecodeBindRequestSimpleEmptyName()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x16 );
        stream.put( new byte[]
            { 0x30, 0x14, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0F, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, // name LDAPDN,
                ( byte ) 0x80, 0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        assertEquals( "password", StringTools.utf8ToString( ( ( SimpleAuthentication ) br.getAuthentication() )
            .getSimple() ) );

        // Check the length
        assertEquals( 0x16, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Sasl authentication, no
     * credentials and no controls
     */
    public void testDecodeBindRequestSaslNoCredsNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x3A );
        stream.put( new byte[]
            { 0x30, 0x38, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x33, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0xA3, 0x0D, // authentication
                                                                                            // AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { ... sasl [3]
                // SaslCredentials }
                // SaslCredentials ::= SEQUENCE {
                // mechanism LDAPSTRING,
                // ...
                0x04, 0x0B, 'K', 'E', 'R', 'B', 'E', 'R', 'O', 'S', '_', 'V', '4' } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SaslCredentials ) );
        assertEquals( "KERBEROS_V4", ( ( SaslCredentials ) br.getAuthentication() ).getMechanism() );

        // Check the length
        assertEquals( 0x3A, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Sasl authentication, a
     * credentials and no controls
     */
    public void testDecodeBindRequestSaslCredsNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x42 );
        stream.put( new byte[]
            { 0x30, 0x40, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x3B, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0xA3, 0x15, // authentication
                                                                                            // AuthenticationChoice
                                                                                            // }
                // AuthenticationChoice ::= CHOICE { ... sasl [3]
                // SaslCredentials }
                // SaslCredentials ::= SEQUENCE {
                // mechanism LDAPSTRING,
                // ...
                0x04, 0x0B, 'K', 'E', 'R', 'B', 'E', 'R', 'O', 'S', '_', 'V', '4', ( byte ) 0x04, 0x06, // SaslCredentials
                                                                                                        // ::=
                                                                                                        // SEQUENCE
                                                                                                        // {
                // ...
                // credentials OCTET STRING OPTIONAL }
                // 
                'a', 'b', 'c', 'd', 'e', 'f' } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SaslCredentials ) );
        assertEquals( "KERBEROS_V4", ( ( SaslCredentials ) br.getAuthentication() ).getMechanism() );
        assertEquals( "abcdef", StringTools.utf8ToString( ( ( SaslCredentials ) br.getAuthentication() )
            .getCredentials() ) );

        // Check the length
        assertEquals( 0x42, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty body
     */
    public void testDecodeBindRequestEmptyBody()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x07 );
        stream.put( new byte[]
            { 0x30, 0x05, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x00 // CHOICE { ..., bindRequest BindRequest, ...
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty version
     */
    public void testDecodeBindRequestEmptyVersion()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x09 );
        stream.put( new byte[]
            { 0x30, 0x07, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x02, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x00 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with a bad version (0)
     */
    public void testDecodeBindRequestBadVersion0()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x03, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x00 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with a bad version (4)
     */
    public void testDecodeBindRequestBadVersion4()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x03, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x04 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with a bad version (128)
     */
    public void testDecodeBindRequestBadVersion128()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0C );
        stream.put( new byte[]
            { 0x30, 0x0A, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x04, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x02, 0x00, ( byte ) 0x80 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with no name
     */
    public void testDecodeBindRequestNoName()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x03, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty name
     */
    public void testDecodeBindRequestEmptyName()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0C );
        stream.put( new byte[]
            { 0x30, 0x0A, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x05, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00 } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty simple
     */
    public void testDecodeBindRequestEmptysimple()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );
        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x07, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0x80, 0x00 } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        assertEquals( "", StringTools.utf8ToString( ( ( SimpleAuthentication ) br.getAuthentication() ).getSimple() ) );

        // Check the length
        assertEquals( 0x0E, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty sasl
     */
    public void testDecodeBindRequestEmptySasl()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );
        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x07, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x00 } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty mechanism
     */
    public void testDecodeBindRequestEmptyMechanism()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x10 );
        stream.put( new byte[]
            { 0x30, 0x0E, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x09, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x02, 0x04, 0x00 } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SaslCredentials ) );
        assertEquals( "", ( ( SaslCredentials ) br.getAuthentication() ).getMechanism() );

        // Check the length
        assertEquals( 0x10, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty credentials
     */
    public void testDecodeBindRequestEmptyCredentials()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x12 );
        stream.put( new byte[]
            { 0x30, 0x10, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0B, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x04, 0x04, 0x00, 0x04, 0x00, } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SaslCredentials ) );
        assertEquals( "", ( ( SaslCredentials ) br.getAuthentication() ).getMechanism() );
        assertEquals( "", StringTools.utf8ToString( ( ( SaslCredentials ) br.getAuthentication() ).getCredentials() ) );

        // Check the length
        assertEquals( 0x12, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty credentials with
     * controls
     */
    public void testDecodeBindRequestEmptyCredentialsWithControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2F );
        stream.put( new byte[]
            { 0x30, 0x2D, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0B, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x04, 0x04, 0x00, 0x04, 0x00, ( byte ) 0xA0, 0x1B, // A
                                                                                                // control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SaslCredentials ) );
        assertEquals( "", ( ( SaslCredentials ) br.getAuthentication() ).getMechanism() );
        assertEquals( "", StringTools.utf8ToString( ( ( SaslCredentials ) br.getAuthentication() ).getCredentials() ) );

        // Check the length
        assertEquals( 0x2F, message.computeLength() );

        // Check the Control
        List controls = message.getControls();

        assertEquals( 1, controls.size() );

        Control control = message.getControls( 0 );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getControlType() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getControlValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty mechanisms with controls
     */
    public void testDecodeBindRequestEmptyMechanismWithControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2D );
        stream.put( new byte[]
            { 0x30, 0x2B, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x09, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x02, 0x04, 0x00, ( byte ) 0xA0, 0x1B, // A
                                                                                    // control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
        catch ( NamingException ne )
        {
            ne.printStackTrace();
            fail( ne.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br = message.getBindRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SaslCredentials ) );
        assertEquals( "", ( ( SaslCredentials ) br.getAuthentication() ).getMechanism() );
        assertEquals( "", StringTools.utf8ToString( ( ( SaslCredentials ) br.getAuthentication() ).getCredentials() ) );

        // Check the length
        assertEquals( 0x2D, message.computeLength() );

        // Check the Control
        List controls = message.getControls();

        assertEquals( 1, controls.size() );

        Control control = message.getControls( 0 );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getControlType() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getControlValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }
}
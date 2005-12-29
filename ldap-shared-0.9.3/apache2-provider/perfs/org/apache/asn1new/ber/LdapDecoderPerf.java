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
package org.apache.asn1new.ber;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.ber.Asn1Decoder;
import org.apache.asn1new.ldap.codec.LdapDecoder;
import org.apache.asn1new.ldap.codec.LdapMessageContainer;
import org.apache.asn1new.ldap.pojo.BindRequest;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.ldap.pojo.SearchResultEntry;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import java.nio.ByteBuffer;


/**
 * A performance test.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapDecoderPerf extends Thread
{
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * A performance test...
     */
    public void run()
    {
    	PropertyConfigurator.configure( System.getProperty( "log4j.configuration" ) );
    	
    	Asn1Decoder ldapDecoder = new LdapDecoder();
        
        ByteBuffer  stream      = ByteBuffer.allocate( 0x7b );

        
        stream.put(
            new byte[]
            {
                0x30, 0x79, 		// LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	//     messageID MessageID
				0x64, 0x74, 		//     CHOICE { ..., searchResEntry  SearchResultEntry, ...
                        			// SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
									//     objectName      LDAPDN,
				0x04, 0x1b, 'o', 'u', '=', 'c', 'o', 'n', 't', 'a', 'c', 't', 's', ',', 'd', 'c', '=', 'i', 'k', 't', 'e', 'k', ',', 'd', 'c', '=', 'c', 'o', 'm',
									//     attributes      PartialAttributeList }
									// PartialAttributeList ::= SEQUENCE OF SEQUENCE {
                0x30, 0x55, 
                0x30, 0x28, 
                					//     type    AttributeDescription,
                0x04, 0x0b, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's',
                					//     vals    SET OF AttributeValue }
                0x31, 0x19, 
                					// AttributeValue ::= OCTET STRING
                0x04, 0x03, 't', 'o', 'p', 
									// AttributeValue ::= OCTET STRING
                0x04, 0x12, 'o', 'r', 'g', 'a', 'n', 'i', 'z', 'a', 't', 'i', 'o', 'n', 'a', 'l', 'U', 'n', 'i', 't',
                0x30, 0x29, 
				//     type    AttributeDescription,
				0x04, 0x0c, 'o', 'b', 'j', 'e', 'c', 't', 'c', 'l', 'a', 's', 's', '2',
								//     vals    SET OF AttributeValue }
				0x31, 0x19, 
								// AttributeValue ::= OCTET STRING
				0x04, 0x03, 't', 'o', 'p', 
								// AttributeValue ::= OCTET STRING
				0x04, 0x12, 'o', 'r', 'g', 'a', 'n', 'i', 'z', 'a', 't', 'i', 'o', 'n', 'a', 'l', 'U', 'n', 'i', 't'
            } );

/*        
        stream.put(
                new byte[]
                {
                    0x30, 0x33, 		// LDAPMessage ::=SEQUENCE {
    				0x02, 0x01, 0x01, 	//         messageID MessageID
    				0x60, 0x2E, 		//        CHOICE { ..., bindRequest BindRequest, ...
                            			// BindRequest ::= APPLICATION[0] SEQUENCE {
    				0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
    				0x04, 0x1F, 		//        name LDAPDN,
    				'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                    'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
    				( byte ) 0x80, 0x08, //        authentication AuthenticationChoice
                                         // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING, ...
    				'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
                } );
*/        
        stream.flip();

        try
        {

            long t0 = System.currentTimeMillis();
            LdapMessageContainer ldapMessageContainer = new LdapMessageContainer();

            for ( int i = 0; i < 100000; i++ )
            {
                // Allocate a BindRequest Container

                ldapDecoder.decode( stream, ldapMessageContainer );

                // Restore the buffer, and reset the container
                stream.flip();
                ldapMessageContainer.clean();
            }

            long t1 = System.currentTimeMillis();

            System.out.println( "Delta = " + ( t1 - t0 ) );
        }
        catch ( DecoderException de )
        {
            System.out.println( de.getMessage() );
            de.printStackTrace();
        }
    }

    /**
     * Main
     *
     * @param args DOCUMENT ME!
     */
    public static void main( String[] args )
    {

        LdapDecoderPerf ldpt1 = new LdapDecoderPerf();
        LdapDecoderPerf ldpt2 = new LdapDecoderPerf();
        LdapDecoderPerf ldpt3 = new LdapDecoderPerf();
        LdapDecoderPerf ldpt4 = new LdapDecoderPerf();

        ldpt1.start();
        ldpt2.start();
        ldpt3.start();
        ldpt4.start();
    }
}

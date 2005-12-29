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
package org.apache.ldap.common.berlib.snacc.ldap_v3;

// Import PrintStream class for print methods
import java.io.PrintStream;

import com.ibm.asn1.ASN1Decoder;
import com.ibm.asn1.ASN1Encoder;
import com.ibm.asn1.ASN1Exception;
import com.ibm.util.Hex;

/** This class represents the ASN.1 SEQUENCE type <tt>AddRequest</tt>.
  * For each sequence member, sequence classes contain a
  * public member variable of the corresponding Java type.
  * @author Snacc for Java
  * @version Wed Nov 13 12:00:20 2002
  * 
  */

public class AddRequest implements org.apache.ldap.common.berlib.snacc.ldap_v3.LDAP_V3 {

  /** member variable representing the sequence member entry of type byte[] */
  public byte[] entry;
  /** member variable representing the sequence member attributes of type AttributeList */
  public org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeList attributes = new AttributeList();

  /** default constructor */
  public AddRequest() {}

  /** copy constructor */
  public AddRequest (AddRequest arg) {
    entry = new byte[arg.entry.length];
    System.arraycopy(arg.entry,0,entry,0,arg.entry.length);
    attributes = new AttributeList(arg.attributes);
  }

  /** encoding method.
    * @param enc
    *        encoder object derived from com.ibm.asn1.ASN1Encoder
    * @exception com.ibm.asn1.ASN1Exception 
    *            encoding error
    */
  public void encode (ASN1Encoder enc) throws ASN1Exception {
    enc.nextIsImplicit(enc.makeTag(enc.APPLICATION_TAG_CLASS,8));
    int seq_nr = enc.encodeSequence();
    enc.encodeOctetString(entry);
    attributes.encode(enc);
    enc.endOf(seq_nr);
  }

  /** decoding method.
    * @param dec
    *        decoder object derived from com.ibm.asn1.ASN1Decoder
    * @exception com.ibm.asn1.ASN1Exception 
    *            decoding error
    */
  public void decode (ASN1Decoder dec) throws ASN1Exception {
    dec.nextIsImplicit(dec.makeTag(dec.APPLICATION_TAG_CLASS,8));
    int seq_nr = dec.decodeSequence();
    entry = dec.decodeOctetString();
    attributes.decode(dec);
    dec.endOf(seq_nr);
  }

  /** print method (variable indentation)
    * @param os
    *        PrintStream representing the print destination (file, etc)
    * @param indent
    *        number of blanks that preceed each output line.
    */
  public void print (PrintStream os, int indent) {
    os.println("{ -- SEQUENCE --");
    for(int ii = 0; ii < indent+2; ii++) os.print(' ');
    os.print("entry = ");
    os.print(Hex.toString(entry));
    os.println(',');
    for(int ii = 0; ii < indent+2; ii++) os.print(' ');
    os.print("attributes = ");
    attributes.print(os, indent+2);
    os.println();
    for(int ii = 0; ii < indent; ii++) os.print(' ');
    os.print('}');
  }

  /** default print method (fixed indentation)
    * @param os
    *        PrintStream representing the print destination (file, etc)
    */
  public void print (PrintStream os) {
    print(os,0);
  }

  /** toString method
    * @return the output of {@link #print(PrintStream) print} method (fixed indentation) as a string
    */
  public String toString () {
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    print(ps);
    ps.close();
    return baos.toString();
  }

}

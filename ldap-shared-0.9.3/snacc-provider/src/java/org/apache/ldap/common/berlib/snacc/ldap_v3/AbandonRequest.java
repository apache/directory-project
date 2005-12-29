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
import java.math.BigInteger;

import com.ibm.asn1.ASN1Decoder;
import com.ibm.asn1.ASN1Encoder;
import com.ibm.asn1.ASN1Exception;

/**
 * This class represents the ASN.1 simple definition <tt>AbandonRequest</tt>.
 * Simple classes contain a member variable <tt>value</tt> of the type that
 * is FINALLY referred to.
 * 
 * @author Snacc for Java
 * @version Wed Nov 13 12:00:20 2002
 *  
 */
public class AbandonRequest
        implements
            org.apache.ldap.common.berlib.snacc.ldap_v3.LDAP_V3
{

    public BigInteger value;

    /** default constructor */
    public AbandonRequest()
    {
    }

    public AbandonRequest(BigInteger arg)
    {
        value = arg;
    }

    /** copy constructor */
    public AbandonRequest(AbandonRequest arg)
    {
        value = arg.value;
    }

    /**
	 * encoding method.
	 * 
	 * @param enc
	 *            encoder object derived from com.ibm.asn1.ASN1Encoder
	 * @exception com.ibm.asn1.ASN1Exception
	 *                encoding error
	 */
    public void encode(ASN1Encoder enc) throws ASN1Exception
    {
        enc.nextIsImplicit(enc.makeTag(enc.APPLICATION_TAG_CLASS, 16));
        enc.encodeInteger(value);
    }

    /**
	 * decoding method.
	 * 
	 * @param dec
	 *            decoder object derived from com.ibm.asn1.ASN1Decoder
	 * @exception com.ibm.asn1.ASN1Exception
	 *                decoding error
	 */
    public void decode(ASN1Decoder dec) throws ASN1Exception
    {
        dec.nextIsImplicit(dec.makeTag(dec.APPLICATION_TAG_CLASS, 16));
        value = dec.decodeInteger();
    }

    /**
	 * print method (variable indentation)
	 * 
	 * @param os
	 *            PrintStream representing the print destination (file, etc)
	 * @param indent
	 *            number of blanks that preceed each output line.
	 */
    public void print(PrintStream os, int indent)
    {
        os.print(value.toString());
    }

    /**
	 * default print method (fixed indentation)
	 * 
	 * @param os
	 *            PrintStream representing the print destination (file, etc)
	 */
    public void print(PrintStream os)
    {
        print(os, 0);
    }

    /**
	 * toString method
	 * 
	 * @return the output of {@link #print(PrintStream) print}method (fixed
	 *         indentation) as a string
	 */
    public String toString()
    {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        print(ps);
        ps.close();
        return baos.toString();
    }

}
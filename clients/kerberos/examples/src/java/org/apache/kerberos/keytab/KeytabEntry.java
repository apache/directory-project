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
package org.apache.kerberos.keytab;

import org.apache.kerberos.messages.value.KerberosPrincipalModifier;
import org.apache.kerberos.messages.value.PrincipalNameModifier;

import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.nio.ByteBuffer;

public class KeytabEntry {

    private static boolean isLittleEndian = false;

	private int timestamp;
    private int kt_vno;
    private KerberosKey key;

	public KeytabEntry(int kt_vno, byte[] bytes)
    {
        this.kt_vno = kt_vno;

        ByteBuffer buf = ByteBuffer.wrap(bytes);

        int keyVersionNumber;
	    int encryptionType;
	    byte[] keyBytes;

        KerberosPrincipalModifier modifier = new KerberosPrincipalModifier();
        PrincipalNameModifier nameModifier = new PrincipalNameModifier();

        int count = toShort(buf.getShort());
        if (kt_vno == KeytabStore.VNO_1)
        {
            count--;
        }

        int length = toShort(buf.getShort());
        modifier.setRealm(getString(buf, length));

        int ii = 0;
        while (ii < count)
        {
            length = toShort(buf.getShort());
            nameModifier.addName(getString(buf, length));
            ii++;
        }

        nameModifier.setType(toInt(buf.getInt()));

        timestamp = toInt(buf.getInt());

        keyVersionNumber = buf.get();

        encryptionType = toShort(buf.getShort());

        length = toShort(buf.getShort());

        keyBytes = new byte[length];
        buf.get(keyBytes);

        modifier.setPrincipalName(nameModifier.getPrincipalName());

		KerberosPrincipal principal = modifier.getKerberosPrincipal();
        key = new KerberosKey(principal, keyBytes, encryptionType, keyVersionNumber);
	}

	public int getTimestamp()
    {
		return timestamp;
	}

    public KerberosKey getKerberosKey()
    {
        return key;
    }

    private String getString(ByteBuffer buf, int length)
    {
        byte[] bytes = new byte[length];
        buf.get(bytes);
        return new String(bytes);
    }

	private int toShort(short s)
    {
		return kt_vno == KeytabStore.VNO_2 ? s : htons(s);
	}

	private int toInt(int s)
    {
		return kt_vno == KeytabStore.VNO_2 ? s : htonl(s);
	}

	private short htons(short x)
    {
	    return isLittleEndian ? (short)((0x0000ff00 & x) >>> 8 | x << 8) : x;
	}

    private int htonl(int x)
    {
	    return isLittleEndian ? x >>> 24 | x << 24 | (0x00ff0000 & x) >>> 8 |
	        (0x0000ff00 & x) << 8 : x;
	}
}


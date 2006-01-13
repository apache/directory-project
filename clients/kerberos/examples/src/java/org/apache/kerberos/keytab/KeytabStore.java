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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.kerberos.exceptions.KerberosException;

public class KeytabStore
{
    private static boolean isLittleEndian = false;

	// KeytabStore version - DCE compatible
	public static final int VNO_1 = 0x0501;
	// KeytabStore version - MIT V5 compatible
	public static final int VNO_2 = 0x0502;
	
    private File file;
    private int keytabVersionNumber;

    private Map entries = new HashMap();

	public KeytabStore(String file)
    {
	    this(new File(file));
	}

	public KeytabStore(File file)
    {
	    this.file = file;
	}

	public synchronized void init() throws KerberosException
    {
	    if (!file.exists())
        {
	        throw KeytabException.FILE_NOT_FOUND;
        }

	    try
        {
	        RandomAccessFile raf = new RandomAccessFile(file, "r");

	        keytabVersionNumber = raf.readShort() & 0x0000ffff;

	        if (keytabVersionNumber != VNO_1 && keytabVersionNumber != VNO_2)
            {
    	        throw KeytabException.FILE_VERSION_UNSUPPORTED;
            }

            raf.seek(2);

			KeytabEntry entry = getNextEntry(raf);

			while (entry != null)
            {
                KerberosKey key = entry.getKerberosKey();
                String principalName = key.getPrincipal().getName();

                if (entries.containsKey(principalName))
                {
                    int currentKeyVersion = key.getVersionNumber();
                    int previousKeyVersion = (( KerberosKey ) entries.get(principalName)).getVersionNumber();

    				if (currentKeyVersion > previousKeyVersion)
                    {
		    			entries.put(principalName, key);
				    }
                }
                else
                {
                    entries.put(principalName, key);
                }
				entry = getNextEntry(raf);
			}

            raf.close();
	    }
        catch (IOException e)
        {
	        throw KeytabException.FILE_CORRUPT;
	    }
	}

	public KerberosKey getEntry(KerberosPrincipal principal) {
        return ( KerberosKey ) entries.get( principal.getName() );
	}

	private synchronized KeytabEntry getNextEntry(RandomAccessFile raf) throws KeytabException
    {
	    while (raf != null)
        {
	        int length = 0;

	        try
            {
	            length = raf.readInt();
	        }
            catch (EOFException e)
            {
	            break;
	        }
            catch (IOException e)
            {
	            throw KeytabException.FILE_CORRUPT;
	        }

	        length = toInt(length);
	        boolean used = isUsed(length);
	        length = toInt31(length);

	        try
            {
	            if (used)
                {
	                byte[] bytes = new byte[length];
    	            raf.readFully(bytes);
        	        return new KeytabEntry(keytabVersionNumber, bytes);
    	        }
	            raf.seek(raf.getFilePointer() + length);
	        }
            catch (EOFException e)
            {
	            break;
	        }
            catch (IOException e)
            {
	            throw KeytabException.FILE_CORRUPT;
	        }
	    }
	    return null;
	}

	private int toInt(int s)
    {
	    return keytabVersionNumber == VNO_2 ? s : htonl(s);
	}

	private int toInt31(int x)
    {
	    return 0x07fffffff & x;
	}

	private boolean isUsed(int x)
    {
	    return (0x80000000 & x) == 0;
	}

	private int htonl(int x)
    {
	    return isLittleEndian ? x >>> 24 | x << 24 | (0x00ff0000 & x) >>> 8 |
	        (0x0000ff00 & x) << 8 : x;
	}
}


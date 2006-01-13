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

import org.apache.kerberos.exceptions.KerberosException;

public class KeytabException extends KerberosException
{
	public static final KeytabException FILE_NOT_FOUND = new KeytabException(1, "KeytabStore file not found");
	public static final KeytabException FILE_CORRUPT = new KeytabException(2, "KeytabStore file corrupt");
	public static final KeytabException FILE_VERSION_UNSUPPORTED = new KeytabException(3, "KeytabStore file version unsupported");
	public static final KeytabException FILE_ENTRY_NOT_FOUND = new KeytabException(4, "KeytabStore file entry not found");

	protected KeytabException(int ordinal, String s)
    {
		super(ordinal, s);
	}
}


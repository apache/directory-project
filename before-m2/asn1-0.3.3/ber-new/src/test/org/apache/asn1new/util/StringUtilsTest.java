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
package org.apache.asn1new.util;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StringUtilsTest extends TestCase {

	public void testOneByteChar()
	{
		char res = StringUtils.bytesToChar(new byte[]{0x30});
		
		Assert.assertEquals('0', res);
	}

	public void testOneByteChar00()
	{
		char res = StringUtils.bytesToChar(new byte[]{0x00});
		
		Assert.assertEquals(0x00, res);
	}

	public void testOneByteChar7F()
	{
		char res = StringUtils.bytesToChar(new byte[]{0x7F});
		
		Assert.assertEquals(0x7F, res);
	}

	public void testTwoBytesChar()
	{
		char res = StringUtils.bytesToChar(new byte[]{(byte)0xCE, (byte)0x91});
		
		Assert.assertEquals(0x0391, res);
	}

	public void testThreeBytesChar()
	{
		char res = StringUtils.bytesToChar(new byte[]{(byte)0xE2, (byte)0x89, (byte)0xA2});
		
		Assert.assertEquals(0x2262, res);
	}

	/*
	public void testSixBytesChar()
	{
		char res = StringUtils.bytesToChar(new byte[]{(byte)0xFD, (byte)0x93, (byte)0x91, (byte)0xBA, (byte)0x95, (byte)0xA3});
		
		Assert.assertEquals(0x7347A563, res);
	}
	*/
}

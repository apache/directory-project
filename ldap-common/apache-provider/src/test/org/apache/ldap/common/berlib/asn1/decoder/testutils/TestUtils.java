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
package org.apache.ldap.common.berlib.asn1.decoder.testutils ;


import junit.framework.Assert;

import java.util.Arrays;


/**
 * Utilities functions for use by test cases and for debugging.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class TestUtils
{
    public static void assertEquals( byte[] expected, byte[] actual )
    {
        String msg = "\nexpected <" + toString( expected ) + ">\nbut was  <" + toString( actual ) + ">";
        Assert.assertTrue( msg, Arrays.equals( expected, actual ));
    }

    public static String toString( byte[] bites )
    {
        if ( bites.length == 0 ) return "";

        StringBuffer sb = new StringBuffer( "");
        for ( int i = 0; i < bites.length; i++ )
        {
            byte bite = bites[i];
            sb.append( Integer.toHexString( bite ).toUpperCase( ) ).append( " ");
        }
        sb.setLength( sb.length() - 1 );

        return sb.toString();
    }
}

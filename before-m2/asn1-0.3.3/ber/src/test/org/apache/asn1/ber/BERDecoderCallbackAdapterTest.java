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
package org.apache.asn1.ber ;


import junit.framework.TestCase ;
import org.apache.asn1.ber.BERDecoderCallbackAdapter;


/**
 * Tests for the adapter to pass clover coverage.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDecoderCallbackAdapterTest extends TestCase
{
    BERDecoderCallbackAdapter adapter = null ;
    

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(BERDecoderCallbackAdapterTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        adapter = new BERDecoderCallbackAdapter() ;
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        adapter = null ;
    }

    /**
     * Constructor for BERDecoderCallbackAdapter.
     * @param arg0
     */
    public BERDecoderCallbackAdapterTest(String arg0)
    {
        super(arg0);
    }

    public void testTagDecoded()
    {
        adapter.tagDecoded( null ) ;
    }

    public void testLengthDecoded()
    {
        adapter.lengthDecoded( null ) ;
    }

    public void testPartialValueDecoded()
    {
        adapter.partialValueDecoded( null ) ;
    }

    public void testDecodeOccurred()
    {
        adapter.decodeOccurred( null, null ) ;
    }

}

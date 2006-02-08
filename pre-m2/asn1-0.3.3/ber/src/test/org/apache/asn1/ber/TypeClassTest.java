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


/**
 * Tests TypeClass class.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TypeClassTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TypeClassTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for TypeClassTest.
     * @param arg0
     */
    public TypeClassTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Class to test for TypeClass getTypeClass(String)
     */
    public void testGetTypeClassString()
    {
        assertEquals( TypeClass.APPLICATION, TypeClass.getTypeClass(
                        TypeClass.APPLICATION.getName() ) ) ;
        assertEquals( TypeClass.UNIVERSAL, TypeClass.getTypeClass( 
                        TypeClass.UNIVERSAL.getName() ) ) ;
        assertEquals( TypeClass.PRIVATE, TypeClass.getTypeClass( 
                        TypeClass.PRIVATE.getName() ) ) ;
        assertEquals( TypeClass.CONTEXT_SPECIFIC, TypeClass.getTypeClass( 
                        TypeClass.CONTEXT_SPECIFIC.getName() ) ) ;

        assertEquals( TypeClass.APPLICATION, TypeClass.getTypeClass( 
                        "application") ) ;
        assertEquals( TypeClass.UNIVERSAL, TypeClass.getTypeClass( 
                        "Universal" ) ) ;
        assertEquals( TypeClass.PRIVATE, TypeClass.getTypeClass( 
                        "PRivatE" ) ) ;
        assertEquals( TypeClass.CONTEXT_SPECIFIC, TypeClass.getTypeClass( 
                        "context_specific" ) ) ;
        
        try
        {
            TypeClass.getTypeClass( "asdf" ) ;
            fail( "exception should prevent this failure" ) ;
        }
        catch ( Throwable t )
        { 
            assertNotNull( t ) ;
        }
    }

    /*
     * Class to test for TypeClass getTypeClass(int)
     */
    public void testGetTypeClassint()
    {
        assertEquals( TypeClass.APPLICATION, TypeClass.getTypeClass( 
                        TypeClass.APPLICATION_VAL ) ) ;
        assertEquals( TypeClass.PRIVATE, TypeClass.getTypeClass( 
                        TypeClass.PRIVATE_VAL ) ) ;
        assertEquals( TypeClass.UNIVERSAL, TypeClass.getTypeClass( 
                        TypeClass.UNIVERSAL_VAL ) ) ;
        assertEquals( TypeClass.CONTEXT_SPECIFIC, TypeClass.getTypeClass( 
                        TypeClass.CONTEXT_SPECIFIC_VAL ) ) ;
        
        try
        {
            TypeClass.getTypeClass( 35 ) ;
            fail( "exception should prevent this failure" ) ;
        }
        catch( Throwable t )
        {
            assertNotNull( t ) ;
        }
    }
}

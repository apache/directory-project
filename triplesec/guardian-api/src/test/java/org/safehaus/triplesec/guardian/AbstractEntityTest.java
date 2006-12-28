/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.guardian;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 */
public abstract class AbstractEntityTest extends TestCase {

    private Object a1;
    private Object a2;
    private Object b1;
    private Object b2;
    private Object wrong;

    protected abstract Object newInstanceA1();
    protected abstract Object newInstanceA2();
    protected abstract Object newInstanceB1();
    protected abstract Object newInstanceB2();
    
    protected Object newWrongInstance()
    {
        return new Object();
    }

    public void setUp()
    {
        a1 = newInstanceA1();
        a2 = newInstanceA2();
        b1 = newInstanceB1();
        b2 = newInstanceB2();
        wrong = newWrongInstance();
    }
    
    public void testEquals()
    {
        Assert.assertEquals( a1, a1 );
        Assert.assertEquals( a1, a2 );
        Assert.assertFalse( a1.equals( null ) );
        Assert.assertFalse( a1.equals( b1 ) );
        Assert.assertFalse( a1.equals( b2 ) );
        Assert.assertFalse( a1.equals( wrong ) );
    }
    
    public void testHashCode()
    {
        Assert.assertEquals( a1.hashCode(), a2.hashCode() );
        Assert.assertFalse( a1.hashCode() == b1.hashCode() );
        Assert.assertFalse( a1.hashCode() == b2.hashCode() );
    }
    
    public void testCompareTo()
    {
        if( !( a1 instanceof Comparable ) )
        {
            return;
        }
        
        Comparable a1 = ( Comparable ) this.a1;
        
        Assert.assertTrue( a1.compareTo( a1 ) == 0 );
        Assert.assertTrue( a1.compareTo( a2 ) == 0 );

        try
        {
            a1.compareTo( null );
            Assert.fail( "Execption is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        
        Assert.assertFalse( a1.compareTo( b1 ) == 0 );
        Assert.assertFalse( a1.compareTo( b2 ) == 0 );
        
        try
        {
            a1.compareTo( wrong );
            Assert.fail( "Exception is not thrown." );
        }
        catch( ClassCastException e )
        {
            // OK
        }
    }
    
    public void testClone() throws Exception
    {
        Object a = a1;
        Object b = a1.getClass().getMethod( "clone", null ).invoke( a1, null );
        Assert.assertEquals( a, b );
        _testClone( a, b );
    }
    
    protected void _testClone( Object a, Object b )
    {
    }
    
    public void testToString() throws Exception
    {
        a1.toString();
        a2.toString();
        b1.toString();
        b2.toString();
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AbstractEntityTest.class);
    }

}

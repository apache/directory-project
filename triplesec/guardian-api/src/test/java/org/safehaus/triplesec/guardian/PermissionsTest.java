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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;


/**
 * 
 *
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 *
 */
public class PermissionsTest extends AbstractEntityTest
{
    protected Object newInstanceA1()
    {
        return new Permissions( "app1", new Permission[] {
                new Permission( "app1", "perm1" ),
                new Permission( "app1", "perm2" ),
                new Permission( "app1", "perm3" ),
        });
    }

    protected Object newInstanceA2()
    {
        return new Permissions( "app1", new Permission[] {
                new Permission( "app1", "perm1" ),
                new Permission( "app1", "perm2" ),
                new Permission( "app1", "perm3" ),
        });
    }

    protected Object newInstanceB1()
    {
        return new Permissions( "app1", new Permission[] {
                new Permission( "app1", "perm1" ),
        });
    }

    protected Object newInstanceB2()
    {
        return new Permissions( "app2", new Permission[0] );
    }
    
    public void testInstantiation()
    {
        // Test null values
        try
        {
            new Permissions( null, null );
            Assert.fail( "Execption is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        
        // Test empty values
        try
        {
            new Permissions( "", null );
            Assert.fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // Test null elements
        Permissions perms = new Permissions( "app1", new Permission[] {
                null, null, null,
        });
        Assert.assertTrue( perms.isEmpty() );
        
        // Test mismatching application names
        try
        {
            new Permissions( "app1", new Permission[] {
                    new Permission( "app2", "perm1" ),
            });
            Assert.fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            //OK
        }
        
        Assert.assertTrue( perms.isEmpty() );
    }
    
    public void testProperties()
    {
        Permission p1 = new Permission( "app1", "perm1" );
        Permission p2 = new Permission( "app1", "perm2" );
        Permission p3 = new Permission( "app1", "perm3" );
        Permissions perms = new Permissions( "app1", new Permission[] {
                p1, p2, p3,
        });
        
        Assert.assertEquals( "app1", perms.getApplicationName() );
        Assert.assertEquals( 3, perms.size() );
        Assert.assertTrue( perms.contains( p1 ) );
        Assert.assertTrue( perms.contains( p2 ) );
        Assert.assertTrue( perms.contains( p3 ) );
        Assert.assertTrue( perms.contains( p1.getName() ) );
        Assert.assertTrue( perms.contains( p2.getName() ) );
        Assert.assertTrue( perms.contains( p3.getName() ) );
        Assert.assertEquals( p1, perms.get( p1.getName() ) );
        Assert.assertEquals( p2, perms.get( p2.getName() ) );
        Assert.assertEquals( p3, perms.get( p3.getName() ) );
        
        // Test iterator integrity
        Set allPerms = new HashSet();
        allPerms.add( p1 );
        allPerms.add( p2 );
        allPerms.add( p3 );
        for( Iterator i = perms.iterator(); i.hasNext(); )
        {
            Permission p = ( Permission ) i.next();
            Assert.assertTrue( allPerms.contains( p ) );
            allPerms.remove( p );
        }
    }
    
    public void testSetOperations()
    {
        Permissions perms1 = new Permissions( "app1", new Permission[] {
                new Permission( "app1", "perm1" ),
        });
        Permissions perms2 = new Permissions( "app1", new Permission[] {
                new Permission( "app1", "perm2" ),
        });
        Permissions perms12 = new Permissions( "app1", new Permission[] {
                new Permission( "app1", "perm1" ),
                new Permission( "app1", "perm2" ),
        });
        Permissions wrongPerms = new Permissions( "wrongApp", null );
        
        
        // addAll
        Assert.assertEquals( perms12, perms1.addAll( perms2 ) );
        Assert.assertEquals( perms1, perms1.addAll( perms1 ) );
        try
        {
            perms1.addAll( wrongPerms );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // removeAll
        Assert.assertEquals( perms1, perms12.removeAll( perms2 ) );
        Assert.assertEquals( perms1, perms1.removeAll( perms2 ) );
        try
        {
            perms1.removeAll( wrongPerms );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // retainAll
        Assert.assertEquals( perms1, perms12.retainAll( perms1 ) );
        Assert.assertEquals(
                new Permissions( "app1", null ), perms1.retainAll( perms2 ) );
        try
        {
            perms1.retainAll( wrongPerms );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }

        // containsAll
        Assert.assertTrue( perms12.containsAll( perms12 ) );
        Assert.assertFalse( perms1.containsAll( perms12 ) );
        try
        {
            perms1.containsAll( wrongPerms );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
    }
    
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( PermissionsTest.class );
    }

}

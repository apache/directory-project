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
package org.apache.ldap.common.message;


import junit.framework.TestCase;

import java.util.Collection;
import java.util.Collections;

import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.LockException;


/**
 * TestCase for the CompareRequestImpl class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class CompareRequestImplTest extends TestCase
{
    /**
     * Tests the same object referrence for equality.
     */
    public void testEqualsSameObj()
    {
        CompareRequestImpl req = new CompareRequestImpl( 5 );
        assertTrue( req.equals( req ) );
    }


    /**
     * Tests for equality using exact copies.
     */
    public void testEqualsExactCopy()
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( "cn=admin,dc=example,dc=com" );
        req0.setAttributeId( "objectClass" );
        req0.setAssertionValue( "top" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );
        req1.setAttributeId( "objectClass" );
        req1.setAssertionValue( "top" );

        assertTrue( req0.equals( req1 ) );
        assertTrue( req1.equals( req0 ) );
    }


    /**
     * Test for inequality when only the IDs are different.
     */
    public void testNotEqualDiffId()
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 7 );
        req0.setName( "cn=admin,dc=example,dc=com" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Test for inequality when only the attributeIds are different.
     */
    public void testNotEqualDiffAttributeIds()
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( "cn=admin,dc=apache,dc=org" );
        req0.setAttributeId( "dc" );
        req0.setAssertionValue( "apache.org" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( "cn=admin,dc=apache,dc=org" );
        req1.setAttributeId( "nisDomain" );
        req1.setAssertionValue( "apache.org" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Test for inequality when only the Assertion values are different.
     */
    public void testNotEqualDiffValue()
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( "cn=admin,dc=apache,dc=org" );
        req0.setAttributeId( "dc" );
        req0.setAssertionValue( "apache.org" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( "cn=admin,dc=apache,dc=org" );
        req1.setAttributeId( "dc" );
        req1.setAssertionValue( "nagoya.apache.org" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Tests for equality even when another CompareRequest implementation is used.
     */
    public void testEqualsDiffImpl()
    {
        CompareRequest req0 = new CompareRequest()
        {
            public String getAssertionValue()
            {
                return null;
            }

            public void setAssertionValue( String a_value )
            {

            }

            public String getAttributeId()
            {
                return null;
            }

            public void setAttributeId( String a_attrId )
            {

            }

            public String getName()
            {
                return null;
            }

            public void setName( String a_name )
            {
            }

            public boolean isVersion3()
            {
                return true;
            }

            public boolean getVersion3()
            {
                return true;
            }

            public void setVersion3( boolean a_isVersion3 )
            {
            }

            public MessageTypeEnum getResponseType()
            {
                return MessageTypeEnum.COMPARERESPONSE;
            }

            public boolean hasResponse()
            {
                return true;
            }

            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.COMPAREREQUEST;
            }

            public Collection getControls()
            {
                return Collections.EMPTY_LIST;
            }

            public void add( Control a_control ) throws MessageException
            {
            }

            public void remove( Control a_control ) throws MessageException
            {
            }

            public int getMessageId()
            {
                return 5;
            }

            public Object get( Object a_key )
            {
                return null;
            }

            public Object put( Object a_key, Object a_value )
            {
                return null;
            }

            public Lockable getParent()
            {
                return null;
            }

            public boolean isLocked()
            {
                return false;
            }

            public boolean getLocked()
            {
                return false;
            }

            public void setLocked( boolean a_isLocked ) throws LockException
            {
            }

            public boolean isUnlockable()
            {
                return false;
            }
        };

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        assertTrue( req1.equals( req0 ) );
        assertFalse( req0.equals( req1 ) );
    }
}

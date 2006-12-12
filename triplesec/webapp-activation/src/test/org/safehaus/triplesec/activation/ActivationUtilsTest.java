package org.safehaus.triplesec.activation;

import junit.framework.TestCase;


public class ActivationUtilsTest extends TestCase
{
	public void testGetActivationKey()
	{
		try { ActivationUtils.getActivationKey( null ); fail( "should not get here" ); } 
		catch ( IllegalArgumentException e ) {}

		try { ActivationUtils.getActivationKey( "not forward slash causes error" ); fail( "should not get here" ); } 
		catch ( IllegalArgumentException e ) {}
		
		try { ActivationUtils.getActivationKey( "need/forward/slash/in/front" ); fail( "should not get here" ); } 
		catch ( IllegalArgumentException e ) {}
		
		String key = ActivationUtils.getActivationKey( "/activation/1293847234/HausKeys.jar" );
		assertEquals( "1293847234", key );
	}
}

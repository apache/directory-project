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

/*
 * $Id: ControlTransform.java,v 1.3 2003/05/08 21:26:50 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.berlib.snacc ;


import java.util.Iterator ;
import java.util.Collection ;

import org.apache.ldap.common.message.Message ;
import org.apache.ldap.common.message.ControlImpl ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Control ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;


/**
 * Class1 description here.
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class ControlTransform
{
    /**
     * Utility method to transform a collection of Message API Controls into a
     * snacc based Controls stub instance.
     *
     * @param a_controls list of org.apache.ldap.common.message.Control instances.
     * @return a org.apache.ldap.common.berlib.snacc.ldap_v3.Controls snacc stub instance.
     */
    static Controls transformToSnacc( Collection a_controls )
    {
        org.apache.ldap.common.message.Control l_control = null ;
        Controls l_snaccControls = new Controls() ;
        Control l_snaccControl = null ;

		Iterator l_list = a_controls.iterator() ;
        while( l_list.hasNext() )
        {
            l_control = ( org.apache.ldap.common.message.Control ) l_list.next() ;
            l_snaccControl = new Control() ;
            l_snaccControl.controlType = l_control.getType().getBytes() ;
            l_snaccControl.controlValue = l_control.getValue() ;
            l_snaccControl.criticality = l_control.isCritical() ;
            l_snaccControls.add( l_snaccControl ) ;
        }

        return l_snaccControls ;
    }


    /**
     * Utility method used to convert Snacc4J Controls stubs into a Collection
     * of org.apache.ldap.common.message.Controls.
     *
     * @param a_parent the org.apache.ldap.common.message.Message containing the controls.
     * @param a_snaccControls an instance (to transform) of the Snacc4J compiler
     * generated stub used to contain a sequence of controls
     */
    static void transformFromSnacc( Message a_parent, Controls a_snaccControls )
    {
        ControlImpl l_control = null ;
		Control l_snaccControl = null ;

        if( a_snaccControls == null )
        {
            return ;
        }

        for( int ii = 0; ii < a_snaccControls.size(); ii++ )
        {
			l_snaccControl = (Control) a_snaccControls.get( ii ) ;
			l_control = new ControlImpl( a_parent ) {
                public byte[] getEncodedValue()
                {
                    return null;
                }
            };
            l_control.setCritical( l_snaccControl.criticality ) ;
            l_control.setType( new String( l_snaccControl.controlType ) ) ;
            l_control.setValue( l_snaccControl.controlValue ) ;
            a_parent.add( l_control ) ;
        }
    }
}

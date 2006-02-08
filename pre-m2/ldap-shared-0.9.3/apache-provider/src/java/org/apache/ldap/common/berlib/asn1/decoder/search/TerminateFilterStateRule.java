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
package org.apache.ldap.common.berlib.asn1.decoder.search;


import org.apache.asn1.ber.TypeClass;
import org.apache.ldap.common.filter.ExprNode;


/**
 * Used to terminate the filter processing state.  Registered before the
 * attributes rules are registered to switch state.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class TerminateFilterStateRule extends BaseSearchRequestRule
{
    public TerminateFilterStateRule( )
    {
        super( 3 );
    }


    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass );

        if ( getProcessing().getState() == getProcessing().FILTER_STATE )
        {
            getRequest().setFilter( ( ExprNode ) getDigester().pop() );
            getProcessing().next();
        }

        setEnabled( false );
    }
}

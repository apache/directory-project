/*
 *   Copyright 2006 The Apache Software Foundation
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


package org.apache.directory.shared.ldap.trigger;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;

/**
 * A utility class for working with Triggers Subentries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev:$
 */
public class TriggerUtils
{
    
    public static void loadTriggerSpecification( LdapContext ctx, String cn, String subtree, String triggerSpec ) throws NamingException
    {
        Attributes ap = ctx.getAttributes( "", new String[] { "administrativeRole" } );
        Attribute administrativeRole = ap.get( "administrativeRole" );
        if ( administrativeRole == null || !administrativeRole.contains( "triggerSpecificArea" ) )
        {
            Attributes changes = new BasicAttributes( "administrativeRole", "triggerSpecificArea", true );
            ctx.modifyAttributes( "", DirContext.ADD_ATTRIBUTE, changes );
        }
        
        Attributes subentry = new BasicAttributes( "cn", cn, true );
        Attribute objectClass = new BasicAttribute( "objectClass" );
        subentry.put( objectClass );
        objectClass.add( "top" );
        objectClass.add( "subentry" );
        objectClass.add( "triggerSubentry" );
        subentry.put( "subtreeSpecification", subtree );
        subentry.put( "prescriptiveTrigger", triggerSpec );
        ctx.createSubcontext( "cn=" + cn, subentry );
    }

}
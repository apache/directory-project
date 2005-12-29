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


import org.apache.ldap.common.Lockable;


/**
 *
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SubentryRequestControl extends ControlImpl
{
    /** */
    private final boolean subentryVisibility;


    /**
     *
     * @param subentryVisibility
     */
    public SubentryRequestControl( boolean subentryVisibility )
    {
        super();
        this.subentryVisibility = subentryVisibility;
    }


    /**
     *
     * @param parent
     * @param subentryVisibility
     */
    public SubentryRequestControl( Lockable parent, boolean subentryVisibility )
    {
        super( parent );
        this.subentryVisibility = subentryVisibility;
    }


    /**
     * @todo need to properly implement this
     *
     * @return
     */
    public byte[] getEncodedValue()
    {
        return new byte[0];
    }


    /**
     *
     * @return
     */
    public boolean getSubentryVisibility()
    {
        return subentryVisibility;
    }
}

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
package org.apache.ldap.common.berlib.asn1.decoder.search ;


import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.decoder.ResultResponseRule;


/**
 * A digester rule which fires to build SearchResponseDoneEncoder containment trees.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class SearchResponseDoneRule extends ResultResponseRule
{
    /**
     * Creates a digester rule which fires to build SearchResponseDoneEncoder
     * containment trees.
     */
    public SearchResponseDoneRule()
    {
        super( LdapTag.SEARCH_RESULT_DONE ) ;
    }
}

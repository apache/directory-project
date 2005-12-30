/*
 *   Copyright 2005 The Apache Software Foundation
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

package org.apache.kerberos.store;

import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

/**
 * Interface for search strategies.  Kerberos protocols may search a single
 * base DN for principals or use a catalog to lookup principals in multiple
 * search base DN's.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
interface SearchStrategy
{
    public PrincipalStoreEntry getPrincipal( KerberosPrincipal principal ) throws Exception;

    public String changePassword( KerberosPrincipal principal, KerberosKey newKey ) throws Exception;
}

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
package org.apache.ldap.testsuite.ldaptests.jndi.util;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 * Convenience methods to create attributes for entries of common objectclasses.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AttributesFactory
{
    /**
     * Creates the required attributes for a person as defined in RFC 2256. The
     * result contains an objectClass attribute with values top and person, and
     * the attributes sn and cn.
     * 
     * @param cn
     *            common name of person
     * @param sn
     *            surname of person
     * 
     * @return attributes object with the required attributes for a person
     */
    public static final Attributes createPersonAttributes(String cn, String sn)
    {
        Attributes person = new BasicAttributes();

        Attribute ocls = new BasicAttribute("objectClass");
        ocls.add("top");
        ocls.add("person");
        person.put(ocls);

        person.put("cn", cn);
        person.put("sn", sn);

        return person;
    }

    /**
     * Creates the required attributes for an inetOrgPerson as defined in RFC
     * 2798 ("Definition of the inetOrgPerson LDAP Object Class"). The result
     * contains an objectClass attribute with the appropriate values, and the
     * attributes sn and cn, which are required from person.
     * 
     * @param cn
     *            common name of person
     * @param sn
     *            surname of person
     * 
     * @return attributes object with the required attributes for an inetOrgPerson
     */
    public static final Attributes createInetOrgPersonAttributes(String cn, String sn)
    {
        Attributes person = new BasicAttributes();

        Attribute ocls = new BasicAttribute("objectClass");
        ocls.add("top");
        ocls.add("person");
        ocls.add("organizationalPerson");
        ocls.add("inetOrgPerson");
        person.put(ocls);

        person.put("cn", cn);
        person.put("sn", sn);

        return person;
    }

    /**
     * Creates the required attributes for an organizational unit as defined in
     * RFC 2256. The result contains an objectClass attribute with values top
     * and organizationalUnit, and the mandatory attribute ou.
     * 
     * @param ou
     *            common name of person
     * 
     * @return attributes object with the required attributes for an
     *         organizational unit
     */
    public static final Attributes createOrganizationalUnitAttributes(String ou)
    {
        Attributes person = new BasicAttributes();

        Attribute ocls = new BasicAttribute("objectClass");
        ocls.add("top");
        ocls.add("organizationalUnit");
        person.put(ocls);

        person.put("ou", ou);

        return person;
    }

}

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

package org.apache.directory.ldapstudio.schemas.model;


/**
 * This class is the model for all LDAP schema elements
 *
 */
public interface SchemaElement
{

    /**
     * @return the various aliases of the element
     */
    public String[] getNames();


    /**
     * @return the oid of the element
     */
    public String getOid();


    /**
     * @return the schema model instance defining this element
     */
    public Schema getOriginatingSchema();


    /**
     * @return the description of the element
     */
    public String getDescription();
}
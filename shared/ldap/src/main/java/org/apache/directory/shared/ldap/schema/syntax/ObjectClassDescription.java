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

package org.apache.directory.shared.ldap.schema.syntax;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * RFC 4512 - 4.1.1. Object Class Description
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassDescription
{
    public static enum Kind
    {
        ABSTRACT, STRUCTURAL, AUXILIARY
    }

    private String oid;

    private List<String> names;

    private String description;

    private boolean isObsolete;

    private List<String> superiorObjectClasses;

    private Kind kind;

    private List<String> mustAttributeTypes;

    private List<String> mayAttributeTypes;

    private Map<String, List<String>> extensions;


    public ObjectClassDescription()
    {
        this.oid = "";
        names = new ArrayList<String>();
        description = "";
        isObsolete = false;
        superiorObjectClasses = new ArrayList<String>();
        kind = Kind.STRUCTURAL;
        mustAttributeTypes = new ArrayList<String>();
        mayAttributeTypes = new ArrayList<String>();
        extensions = new LinkedHashMap<String, List<String>>();
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }


    public Map<String, List<String>> getExtensions()
    {
        return extensions;
    }


    public void setExtensions( Map<String, List<String>> extensions )
    {
        this.extensions = extensions;
    }


    public boolean isObsolete()
    {
        return isObsolete;
    }


    public void setObsolete( boolean isObsolete )
    {
        this.isObsolete = isObsolete;
    }


    public List<String> getMayAttributeTypes()
    {
        return mayAttributeTypes;
    }


    public void setMayAttributeTypes( List<String> mayAttributeTypes )
    {
        this.mayAttributeTypes = mayAttributeTypes;
    }


    public List<String> getMustAttributeTypes()
    {
        return mustAttributeTypes;
    }


    public void setMustAttributeTypes( List<String> mustAttributeTypes )
    {
        this.mustAttributeTypes = mustAttributeTypes;
    }


    public List<String> getNames()
    {
        return names;
    }


    public void setNames( List<String> names )
    {
        this.names = names;
    }


    public String getNumericOid()
    {
        return oid;
    }


    public void setOid( String oid )
    {
        this.oid = oid;
    }


    public List<String> getSuperiorObjectClasses()
    {
        return superiorObjectClasses;
    }


    public void setSuperiorObjectClasses( List<String> superiorObjectClasses )
    {
        this.superiorObjectClasses = superiorObjectClasses;
    }


    public Kind getKind()
    {
        return kind;
    }


    public void setKind( Kind kind )
    {
        this.kind = kind;
    }


    public void addSuperiorObjectClass( String oid )
    {
        this.superiorObjectClasses.add( oid );
    }


    public void addMustAttributeType( String oid )
    {
        this.mustAttributeTypes.add( oid );
    }


    public void addMayAttributeType( String oid )
    {
        this.mayAttributeTypes.add( oid );
    }


    public void addExtension( String key, List<String> values )
    {
        this.extensions.put( key, values );
    }

}
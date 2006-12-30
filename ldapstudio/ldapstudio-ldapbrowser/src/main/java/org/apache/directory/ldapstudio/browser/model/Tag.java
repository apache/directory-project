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

package org.apache.directory.ldapstudio.browser.model;


/**
 * This class represent a XML Tag which consists of : - a name - a type (START,
 * END)
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Tag
{

    private String name;

    private int type;

    public static int START = 0;

    public static int END = 1;


    public Tag( String name, int type )
    {
        setName( name );
        setType( type );
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name.toLowerCase();
    }


    public int getType()
    {
        return type;
    }


    public void setType( int type )
    {
        this.type = type;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Tag )
        {
            Tag tag = ( Tag ) obj;
            return ( ( this.name.equals( tag.getName() ) ) && ( this.type == tag.getType() ) );

        }
        else
        {
            return false;
        }
    }


    @Override
    public int hashCode()
    {

        return name.hashCode() + type << 24;
    }


    @Override
    public String toString()
    {
        if ( name != null )
        {
            return "<" + ( ( type == Tag.END ) ? "/" : "" ) + name + ">";
        }
        else
        {
            return "Unknown tag";
        }
    }

}
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
package org.apache.asn1.ber ;


/**
 * Abstract base class for type safe tag enumerations following the agreed upon
 * convention for representing tags as integers.  This way the type safe
 * enumeration also represents a preconstructed tag.  ASN.1 application module
 * specific enumerations should extend this base for use with the digester.
 *
 * @see <a href="">link to a document explaining how we encoded prefab tags</a>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class TagEnum
{
    /** the id for this tag */
    private final int id ;
    /** the name of this enumeration element */
    private final String name;
    /** the value of this enumeration element */
    private final int value;


    protected TagEnum( final String name, final int value, final int id )
    {
        this.id = id ;
        this.name = name;
        this.value = value;
    }


    /**
     * Get's the name of this enumeration element.
     *
     * @return the name of the enumeration element
     */
    public final String getName()
    {
        return this.name;
    }


    /**
     * Get's the value of this enumeration element.
     *
     * @return the value of the enumeration element
     */
    public final int getValue()
    {
        return this.value;
    }


    /**
     * Gets the id of this tag.
     *
     * @return the id of the tag
     */
    public final int getTagId()
    {
        return this.id ;
    }


    /**
     * Gets the primitive version of a tag.
     *
     * @return the primitive version of a tag with the P/C bit set to 0
     */
    public final int getPrimitiveTag()
    {
        return getValue() & 0xDFFFFFFF ;
    }


    /**
     * Gets the constructed version of a tag.
     *
     * @return the constructed version of a tag with the P/C bit set to 1
     */
    public final int getConstructedTag()
    {
        return getValue() | 0x20000000 ;
    }


    /**
     * Gets the type class of a tag.
     *
     * @return the type class for this tag enumeration.
     */
    public final TypeClass getTypeClass()
    {
        return TypeClass.getTypeClass( getValue() >> 24 ) ;
    }
}

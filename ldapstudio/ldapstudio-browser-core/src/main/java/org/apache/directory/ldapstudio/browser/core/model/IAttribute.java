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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.Serializable;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.internal.model.AttributeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.AttributePropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.ConnectionPropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * The IAttribute interface represents an LDAP attribute.
 */
public interface IAttribute extends Serializable, IAdaptable, AttributePropertyPageProvider, EntryPropertyPageProvider,
    ConnectionPropertyPageProvider
{

    /**
     * ( 2.5.18.3 NAME 'creatorsName' EQUALITY distinguishedNameMatch SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.12 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_CREATORS_NAME = "creatorsName"; //$NON-NLS-1$

    /**
     * ( 2.5.18.1 NAME 'createTimestamp' EQUALITY generalizedTimeMatch
     * ORDERING generalizedTimeOrderingMatch SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.24 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP = "createTimestamp"; //$NON-NLS-1$

    /**
     * ( 2.5.18.4 NAME 'modifiersName' EQUALITY distinguishedNameMatch
     * SYNTAX 1.3.6.1.4.1.1466.115.121.1.12 SINGLE-VALUE
     * NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_MODIFIERS_NAME = "modifiersName"; //$NON-NLS-1$

    /**
     * ( 2.5.18.2 NAME 'modifyTimestamp' EQUALITY generalizedTimeMatch
     * ORDERING generalizedTimeOrderingMatch SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.24 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP = "modifyTimestamp"; //$NON-NLS-1$

    /**
     * ( 2.5.21.9 NAME 'structuralObjectClass' EQUALITY
     * objectIdentifierMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.38
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_STRUCTURAL_OBJECT_CLASS = "structuralObjectClass"; //$NON-NLS-1$

    /**
     * ( 2.5.21.10 NAME 'governingStructureRule' EQUALITY integerMatch
     * SYNTAX 1.3.6.1.4.1.1466.115.121.1.27 SINGLE-VALUE
     * NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_GOVERNING_STRUCTURE_RULE = "governingStructureRule"; //$NON-NLS-1$

    /**
     * ( 1.3.6.1.1.16.4 NAME 'entryUUID' DESC 'UUID of the entry' EQUALITY
     * uuidMatch ORDERING uuidOrderingMatch SYNTAX 1.3.6.1.1.16.1
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_ENTRY_UUID = "entryUUID"; //$NON-NLS-1$

    /**
     * ( 2.5.18.10 NAME 'subschemaSubentry' EQUALITY distinguishedNameMatch
     * SYNTAX 1.3.6.1.4.1.1466.115.121.1.12 SINGLE-VALUE
     * NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_SUBSCHEMA_SUBENTRY = "subschemaSubentry"; //$NON-NLS-1$

    /**
     * ( 2.5.18.9 NAME 'hasSubordinates' DESC 'X.501: entry has children'
     * EQUALITY booleanMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.7
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES = "hasSubordinates"; //$NON-NLS-1$

    /**
     * ( 1.3.1.1.4.1.453.16.2.103 NAME 'numSubordinates' DESC 'count of
     * immediate subordinates' EQUALITY integerMatch ORDERING
     * integerOrderingMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.27
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation X-ORIGIN
     * 'numSubordinates Internet Draft' )
     */
    public static final String OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES = "numSubordinates"; //$NON-NLS-1$

    /**
     * ( 2.16.840.1.113719.1.27.4.49 NAME 'subordinateCount' DESC
     * 'Operational Attribute' SYNTAX 1.3.6.1.4.1.1466.115.121.1.27
     * SINGLE-VALUE NO-USER-MODIFICATION USAGE directoryOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT = "subordinateCount"; //$NON-NLS-1$

    /**
     * ( 1.3.6.1.1.4 NAME 'vendorName' EQUALITY caseExactIA5Match SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.15 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * dSAOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_VENDOR_NAME = "vendorName"; //$NON-NLS-1$

    /**
     * ( 1.3.6.1.1.5 NAME 'vendorVersion' EQUALITY caseExactIA5Match SYNTAX
     * 1.3.6.1.4.1.1466.115.121.1.15 SINGLE-VALUE NO-USER-MODIFICATION USAGE
     * dSAOperation )
     */
    public static final String OPERATIONAL_ATTRIBUTE_VENDOR_VERSION = "vendorVersion"; //$NON-NLS-1$

    /**
     * objectClass
     */
    public static final String OBJECTCLASS_ATTRIBUTE = "objectClass"; //$NON-NLS-1$

    /**
     * objectClass
     */
    public static final String OBJECTCLASS_ATTRIBUTE_OID = "2.5.4.0"; //$NON-NLS-1$

    /**
     * ref
     */
    public static final String REFERRAL_ATTRIBUTE = "ref"; //$NON-NLS-1$

    /**
     * aliasedObjectName
     */
    public static final String ALIAS_ATTRIBUTE = "aliasedObjectName"; //$NON-NLS-1$

    /**
     * ;
     */
    public static final String OPTION_DELIMITER = ";"; //$NON-NLS-1$

    /**
     * lang-
     */
    public static final String OPTION_LANG_PREFIX = "lang-"; //$NON-NLS-1$


    /**
     * Returns the entry of this attribute.
     * 
     * @return the entry of this attribute, never null
     */
    public abstract IEntry getEntry();


    /**
     * Returns true if this attribute is consistent. The following
     * conditions must be fulfilled:
     * 
     * <ul>
     * <li>There must be at least one value</li>
     * <li>There mustn't be any empty value</li>
     * </ul>
     * 
     * @return true if the attribute ist consistent
     */
    public abstract boolean isConsistent();


    /**
     * Returns true if this attribute is a must attribute of its entry
     * according to the entry's subschema.
     * 
     * @return true if this attribute is a must attribute of its entry.
     */
    public abstract boolean isMustAttribute();


    /**
     * Returns true if this attribute is a may attribute of its entry
     * according to the entry's subschema.
     * 
     * @return true if this attribute is a may attribute of its entry.
     */
    public abstract boolean isMayAttribute();


    /**
     * Returns true if this attribute is an operational attribute according
     * to the entry's subschema.
     * 
     * @return true if this attribute is an operational attribute.
     */
    public abstract boolean isOperationalAttribute();


    /**
     * Return true if this attribute is the objectclass attribute.
     * 
     * @return true if this attribute is the objectclass attribute.
     */
    public abstract boolean isObjectClassAttribute();


    /**
     * Return true if the attribute is of type String.
     * 
     * @return true if the attribute is of type String.
     */
    public abstract boolean isString();


    /**
     * Return true if the attribute is of type byte[].
     * 
     * @return true if the attribute is of type byte[].
     */
    public abstract boolean isBinary();


    /**
     * Adds an empty value.
     * 
     * @param source
     *                the ModelModifier
     */
    public abstract void addEmptyValue( ModelModifier source );


    /**
     * Removes one empty value if one is present.
     * 
     * @param source
     *                the ModelModifier
     */
    public abstract void deleteEmptyValue( ModelModifier source );


    /**
     * Adds the given value to this attribute. The value's attribute must be
     * this attribute.
     * 
     * @param valueToAdd
     *                the value to add
     * @param source
     *                the ModelModifier
     * @throws ModelModificationException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void addValue( IValue valueToAdd, ModelModifier source ) throws ModelModificationException;


    /**
     * Deletes the given value from this attribute.
     * 
     * @param valueToDelete
     *                the value to delete
     * @param source
     *                the ModelModifier
     * @throws ModelModificationException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void deleteValue( IValue valueToDelete, ModelModifier source ) throws ModelModificationException;


    /**
     * Replaces the old value with the new value.
     * 
     * @param oldValue
     *                the value that should be replaced
     * @param newValue
     *                the value that should be added
     * @param source
     *                the ModelModifier
     * @throws ModelModificationException
     *                 if the value is null or if the value's attribute
     *                 isn't this attribute.
     */
    public abstract void modifyValue( IValue oldValue, IValue newValue, ModelModifier source )
        throws ModelModificationException;


    /**
     * Returns the values of this attribute, wrapped into IValue objects.
     * 
     * @return the values of this attribute, may be an empty array, never
     *         null.
     */
    public abstract IValue[] getValues();


    /**
     * Returns the number of values in this attribute.
     * 
     * @return the number of values in this attribute.
     */
    public abstract int getValueSize();


    /**
     * Returns the description of this attribute.
     * 
     * @return the description of this attribute.
     */
    public abstract String getDescription();


    /**
     * Returns the type of this attribute (description without options).
     * 
     * @return the attribute type.
     */
    public abstract String getType();


    /**
     * Returns all values as byte[]. If the values aren't binary they are
     * converted to byte[] using UTF-8 encoding.
     * 
     * @return The binary values
     */
    public abstract byte[][] getBinaryValues();


    /**
     * Returns the first value as string if one is present, null otherwise
     * 
     * @return The first value if one present, null otherwise
     */
    public abstract String getStringValue();


    /**
     * Returns all values as String. If the values aren't strings they are
     * converted using UTF-8 encoding.
     * 
     * @return The string values
     */
    public abstract String[] getStringValues();


    /**
     * Returns true if the argument is also of type IAttribute and they are
     * equal.
     * 
     * IAttributes are equal if there entries and there attribute
     * description are equal.
     * 
     * @param o
     *                The attribute to compare, must be of type IAttribute
     * @return true if the argument is equal to this.
     */
    public abstract boolean equals( Object o );


    /**
     * Returns the AttributeTypeDescription of this attribute.
     * 
     * @return the AttributeTypeDescription of this attribute, may be the
     *         default or a dummy
     */
    public abstract AttributeTypeDescription getAttributeTypeDescription();


    /**
     * Returns the AttributeDescription of this attribute.
     * 
     * @return the AttributeDescription of this attribute,.
     */
    public abstract AttributeDescription getAttributeDescription();

}

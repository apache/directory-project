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
package org.apache.ldap.common.codec.search.controls;


import org.apache.asn1.ber.IAsn1Container;
import org.apache.asn1.ber.grammar.AbstractGrammar;
import org.apache.asn1.ber.grammar.GrammarAction;
import org.apache.asn1.ber.grammar.GrammarTransition;
import org.apache.asn1.ber.grammar.IGrammar;
import org.apache.asn1.ber.tlv.UniversalTag;
import org.apache.asn1.ber.tlv.Value;
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.util.IntegerDecoder;
import org.apache.asn1.util.IntegerDecoderException;
import org.apache.ldap.common.codec.LdapStatesEnum;
import org.apache.ldap.common.codec.util.LdapString;
import org.apache.ldap.common.codec.util.LdapStringEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This class implements the EntryChangeControl. All the actions are declared in this
 * class. As it is a singleton, these declaration are only done once.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryChangeControlGrammar extends AbstractGrammar implements IGrammar
{
    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( EntryChangeControlGrammar.class );

    /** The instance of grammar. EntryChangeControlGrammar is a singleton */
    private static IGrammar instance = new EntryChangeControlGrammar();

    /**
     * Creates a new EntryChangeControlGrammar object.
     */
    private EntryChangeControlGrammar()
    {
        name = EntryChangeControlGrammar.class.getName();
        statesEnum = EntryChangeControlStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[EntryChangeControlStatesEnum.LAST_EC_STATE][256];

        super.transitions[EntryChangeControlStatesEnum.EC_SEQUENCE_TAG][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.EC_SEQUENCE_TAG,
                EntryChangeControlStatesEnum.EC_SEQUENCE_VALUE, null );

        super.transitions[EntryChangeControlStatesEnum.EC_SEQUENCE_VALUE][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.EC_SEQUENCE_VALUE,
                EntryChangeControlStatesEnum.CHANGE_TYPE_TAG, 
                new GrammarAction( "Init EntryChangeControl" )
                {
                    public void action( IAsn1Container container ) 
                    {
                        EntryChangeControlContainer EntryChangeContainer = ( EntryChangeControlContainer ) container;
                        EntryChangeControl control = new EntryChangeControl();
                        EntryChangeContainer.setEntryChangeControl( control );
                    }
                }
            );

        super.transitions[EntryChangeControlStatesEnum.CHANGE_TYPE_TAG][UniversalTag.ENUMERATED_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.CHANGE_TYPE_TAG,
                EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE, null );

        GrammarAction setChangeTypeAction = new GrammarAction( "Set EntryChangeControl changeType" )
        {
            public void action( IAsn1Container container ) throws DecoderException
            {
                EntryChangeControlContainer EntryChangeContainer = ( EntryChangeControlContainer ) container;
                Value value = EntryChangeContainer.getCurrentTLV().getValue();
                
                try
                {
                    ChangeType changeType = ChangeType.getChangeType( IntegerDecoder.parse( value ) );
                    
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "changeType = " + changeType );
                    }
                    
                    EntryChangeContainer.getEntryChangeControl().setChangeType( changeType );
                }
                catch ( IntegerDecoderException e )
                {
                    String msg = "failed to decode the changeType for EntryChangeControl";
                    log.error( msg, e );
                    throw new DecoderException( msg );
                }
            }
        };
        
        // transition for when we have a previousDN value
        super.transitions[EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE][UniversalTag.ENUMERATED_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE, 
                EntryChangeControlStatesEnum.PREVIOUS_DN_TAG, setChangeTypeAction );

        // transition for when we do not have a previousDN value but we do have a changeNumber
        super.transitions[EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE][UniversalTag.ENUMERATED_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE, 
                EntryChangeControlStatesEnum.CHANGE_NUMBER_TAG, setChangeTypeAction );
        
        // transition for when we do not have a previousDN value nor do we have a changeNumber
        super.transitions[EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE][UniversalTag.ENUMERATED_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.CHANGE_TYPE_VALUE, 
                EntryChangeControlStatesEnum.GRAMMAR_END, setChangeTypeAction );

        super.transitions[EntryChangeControlStatesEnum.PREVIOUS_DN_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.PREVIOUS_DN_TAG,
                EntryChangeControlStatesEnum.PREVIOUS_DN_VALUE, null );

        GrammarAction setPreviousDnAction = new GrammarAction( "Set EntryChangeControl previousDN" )
        {
            public void action( IAsn1Container container ) throws DecoderException
            {
                EntryChangeControlContainer EntryChangeContainer = ( EntryChangeControlContainer ) container;
                Value value = EntryChangeContainer.getCurrentTLV().getValue();
                LdapString previousDn;
                try
                {
                    previousDn = new LdapString( value.getData() );
                }
                catch ( LdapStringEncodingException e )
                {
                    throw new DecoderException( "failed to encode string data" );
                }
                
                if ( log.isDebugEnabled() )
                {
                    log.debug( "previousDN = " + previousDn );
                }
                
                EntryChangeContainer.getEntryChangeControl().setPreviousDn( previousDn );
            }
        };

        // transition if we do have an optional changeNumber after this previousDN
        super.transitions[EntryChangeControlStatesEnum.PREVIOUS_DN_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.PREVIOUS_DN_VALUE, 
                EntryChangeControlStatesEnum.CHANGE_NUMBER_TAG, setPreviousDnAction );

        // transition if we do *NOT* have an optional changeNumber after this previousDN
        super.transitions[EntryChangeControlStatesEnum.PREVIOUS_DN_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.PREVIOUS_DN_VALUE, 
                EntryChangeControlStatesEnum.GRAMMAR_END, setPreviousDnAction );

        // transition for processing changeNumber
        super.transitions[EntryChangeControlStatesEnum.CHANGE_NUMBER_TAG][UniversalTag.INTEGER_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.CHANGE_NUMBER_TAG,
                EntryChangeControlStatesEnum.CHANGE_NUMBER_VALUE, null );

        // transition to finish grammar and set the changeNumber
        super.transitions[EntryChangeControlStatesEnum.CHANGE_NUMBER_VALUE][UniversalTag.INTEGER_TAG] =
            new GrammarTransition( EntryChangeControlStatesEnum.CHANGE_NUMBER_VALUE, 
                LdapStatesEnum.GRAMMAR_END, 
                new GrammarAction( "Set EntryChangeControl changeNumber" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {
                        EntryChangeControlContainer EntryChangeContainer = ( EntryChangeControlContainer ) container;
                        Value value = EntryChangeContainer.getCurrentTLV().getValue();
                        
                        try
                        {
                            int changeNumber = IntegerDecoder.parse( value );
                            
                            if ( log.isDebugEnabled() )
                            {
                                log.debug( "changeNumber = " + changeNumber );
                            }
                            
                            EntryChangeContainer.getEntryChangeControl().setChangeNumber( changeNumber );
                        }
                        catch ( IntegerDecoderException e )
                        {
                            String msg = "failed to decode the changeNumber for EntryChangeControl";
                            log.error( msg, e );
                            throw new DecoderException( msg );
                        }
                    }
                }
            );
    }

    /**
     * This class is a singleton.
     *
     * @return An instance on this grammar
     */
    public static IGrammar getInstance()
    {
        return instance;
    }
}

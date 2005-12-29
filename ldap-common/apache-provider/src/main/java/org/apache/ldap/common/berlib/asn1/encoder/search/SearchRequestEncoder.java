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
package org.apache.ldap.common.berlib.asn1.encoder.search;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.filter.BranchNode;
import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.ExtensibleNode;
import org.apache.ldap.common.filter.LeafNode;
import org.apache.ldap.common.filter.PresenceNode;
import org.apache.ldap.common.filter.SimpleNode;
import org.apache.ldap.common.filter.SubstringNode;
import org.apache.ldap.common.message.SearchRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * A SearchRequest to TupleNode tlv tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SearchRequestEncoder
{
    /** a thread safe instance of this encoder */
    public static final SearchRequestEncoder INSTANCE =
            new SearchRequestEncoder();

    public TupleNode encode( SearchRequest request )
    {
        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the search request sequence of TLV tuple
        DefaultMutableTupleNode searchReq =
                new DefaultMutableTupleNode( new Tuple() );
        searchReq.getTuple().setTag( LdapTag.SEARCH_REQUEST, false );
        searchReq.getTuple().setLength( Length.INDEFINITE );

        // Add the OCTET_STRING baseObject or base DN
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getBase() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        // Add the ENUMERATED scope parameter
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                UniversalTag.ENUMERATED, request.getScope().getLdapValue() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        // Add the ENUMERATED derefAliases parameter
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                UniversalTag.ENUMERATED, request.getDerefAliases().getValue() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        // Add the INTEGER sizeLimit parameter
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                request.getSizeLimit() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        // Add the INTEGER timeLimit parameter
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                request.getTimeLimit() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        // Add the BOOLEAN typesOnly parameter
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                request.getTypesOnly() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        // Add the Filter filter parameter
        encode( searchReq, request.getFilter() );

        // Add the AttributeDescriptionList attributes parameter
        child = encode( request.getAttributes() );
        searchReq.addLast( child );
        child.setParent( searchReq );

        top.addLast( searchReq );
        searchReq.setParent( top );
        return top;
    }


    /**
     * Encodes a SimpleNode as a TupleNode Tree into another top TupleNode tree.
     *
     * @param top the TupleNode that is having nodes added as we encode
     * @param tag the tag enumeration to use for the TupleNode to add
     * @param node a filter expression tree's simple node
     */
    public void encode( DefaultMutableTupleNode top, TagEnum tag,
                        SimpleNode node )
    {
        DefaultMutableTupleNode child = null;
        DefaultMutableTupleNode parent =
                new DefaultMutableTupleNode( new Tuple() );
        parent.getTuple().setTag( tag, false );
        parent.getTuple().setLength( Length.INDEFINITE );

        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( node.getAttribute() );
        parent.addLast( child );
        child.setParent( parent );

        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( node.getValue() );
        parent.addLast( child );
        child.setParent( parent );

        top.addLast( parent );
        parent.setParent( top );
    }


    public void encode( DefaultMutableTupleNode top, TagEnum tag,
                        SubstringNode node )
    {
        DefaultMutableTupleNode child = null;
        DefaultMutableTupleNode parent =
                new DefaultMutableTupleNode( new Tuple() );
        parent.getTuple().setTag( tag, false );
        parent.getTuple().setLength( Length.INDEFINITE );

        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( node.getAttribute() );
        parent.addLast( child );
        child.setParent( parent );


        if ( node.getInitial() != null || node.getFinal() != null ||
                node.getAny().size() > 0 )
        {
            DefaultMutableTupleNode seq =
                    new DefaultMutableTupleNode( new Tuple() );
            seq.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
            seq.getTuple().setLength( Length.INDEFINITE );

            if ( node.getInitial() != null )
            {
                child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                        LdapTag.CONTEXT_SPECIFIC_TAG_0, node.getInitial() );
                seq.addLast( child );
                child.setParent( seq );
            }

            if ( node.getAny().size() > 0 )
            {
                for( int ii = 0; ii < node.getAny().size(); ii++ )
                {
                    child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                            LdapTag.CONTEXT_SPECIFIC_TAG_1,
                            ( String ) node.getAny().get( ii ) );
                    seq.addLast( child );
                    child.setParent( seq );
                }
            }

            if ( node.getFinal() != null )
            {
                child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                        LdapTag.CONTEXT_SPECIFIC_TAG_2, node.getFinal() );
                seq.addLast( child );
                child.setParent( seq );
            }

            parent.addLast( seq );
            seq.setParent( parent );
        }

        top.addLast( parent );
        parent.setParent( top );
    }


    public void encode( DefaultMutableTupleNode parent, TagEnum tag,
                        PresenceNode node )
    {
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( LdapTag.CONTEXT_SPECIFIC_TAG_7,
                        node.getAttribute() );
        parent.addLast( child );
        child.setParent( parent );
    }


    public void encode( DefaultMutableTupleNode top, TagEnum tag,
                        ExtensibleNode node )
    {
        DefaultMutableTupleNode child = null;
        DefaultMutableTupleNode parent =
                new DefaultMutableTupleNode( new Tuple() );
        parent.getTuple().setTag( tag, false );
        parent.getTuple().setLength( Length.INDEFINITE );


        if ( node.getMatchingRuleId() != null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                    LdapTag.CONTEXT_SPECIFIC_TAG_1, node.getMatchingRuleId() );
            parent.addLast( child );
            child.setParent( parent );
        }

        if ( node.getAttribute() != null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                    LdapTag.CONTEXT_SPECIFIC_TAG_2, node.getAttribute() );
            parent.addLast( child );
            child.setParent( parent );
        }


        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                LdapTag.CONTEXT_SPECIFIC_TAG_3, node.getValue() );
        parent.addLast( child );
        child.setParent( parent );

        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                LdapTag.CONTEXT_SPECIFIC_TAG_4, node.dnAttributes() );
        parent.addLast( child );
        child.setParent( parent );

        top.addLast( parent );
        parent.setParent( top );
    }


    public void encodeBranchNode( DefaultMutableTupleNode parent,
                                  BranchNode node )
    {
        DefaultMutableTupleNode child =
                new DefaultMutableTupleNode( new Tuple() );

        if ( node.isNegation() ) // NOT (!)
        {
            child.getTuple().setTag( LdapTag.CONTEXT_SPECIFIC_TAG_2, false );
            child.getTuple().setLength( Length.INDEFINITE );
            encode( child, node.getChild() );
            parent.addLast( child );
            child.setParent( parent );
            return;
        }


        if ( node.isConjunction() ) // AND (&)
        {
            child.getTuple().setTag( LdapTag.CONTEXT_SPECIFIC_TAG_0, false );
        }
        else if ( node.isDisjunction() ) // OR (|)
        {
            child.getTuple().setTag( LdapTag.CONTEXT_SPECIFIC_TAG_1, false );
        }

        child.getTuple().setLength( Length.INDEFINITE );

        ArrayList children = node.getChildren();
        for( int ii = 0; ii < children.size(); ii++ )
        {
            encode( child, ( ExprNode ) children.get( ii ) );
        }

        parent.addLast( child );
        child.setParent( parent );
    }


    /**
     * Encodes a Filter expression tree composed of ExprNodes into a parent
     * tlv tree node.
     *
     * @param parent the parent TupleNode to add TupleNodes to while encoding
     * @param filter the root of the filter expression tree
     */
    private void encode( DefaultMutableTupleNode parent, ExprNode filter )
    {
        if ( filter.isLeaf() )
        {
            LeafNode node = ( LeafNode ) filter;

            switch( node.getAssertionType() )
            {
                case( LeafNode.EQUALITY ):
                    encode( parent, LdapTag.CONTEXT_SPECIFIC_TAG_3,
                            ( SimpleNode ) node );
                    break;
                    
                case( LeafNode.SUBSTRING ):
                    encode( parent, ( SubstringNode ) node );
                    break;
                    
                case( LeafNode.GREATEREQ ):
                    encode( parent, LdapTag.CONTEXT_SPECIFIC_TAG_5,
                            ( SimpleNode ) node );
                    break;
                    
                case( LeafNode.LESSEQ ):
                    encode( parent, LdapTag.CONTEXT_SPECIFIC_TAG_6,
                            ( SimpleNode ) node );
                    break;
                    
                case( LeafNode.PRESENCE ):
                    PresenceNode presence = ( PresenceNode ) node;
                    encode( parent, LdapTag.CONTEXT_SPECIFIC_TAG_7, presence );
                    break;
                    
                case( LeafNode.APPROXIMATE ):
                    encode( parent, LdapTag.CONTEXT_SPECIFIC_TAG_8,
                            ( SimpleNode ) node );
                    break;
                    
                case( LeafNode.EXTENSIBLE ):
                    encode( parent, ( ExtensibleNode ) node );
                    break;
                    
                default:
                    throw new IllegalArgumentException(
                            "Unrecognized assertion type value: "
                            + node.getAssertionType() );
            }

            return;
        }

        encodeBranchNode( parent, ( BranchNode ) filter );
    }


    /**
     * Encodes a SEQUENCE OF AttributeDescriptions (OCTET_STRINGs) representing
     * the attributes of interest to return in a search request.
     *
     * @param attributes the sequence of AttributeDescriptions
     * @return the encoded root TupleNode of the tlv tree
     */
    private DefaultMutableTupleNode encode( Collection attributes )
    {
        DefaultMutableTupleNode child = null;
        DefaultMutableTupleNode parent =
                new DefaultMutableTupleNode( new Tuple() );
        parent.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        parent.getTuple().setLength( Length.INDEFINITE );

        Iterator list = attributes.iterator();
        while ( list.hasNext() )
        {
            child = ( DefaultMutableTupleNode )
                    EncoderUtils.encode( ( String ) list.next() );
            parent.addLast( child );
            child.setParent( parent );
        }

        return parent;
    }
}

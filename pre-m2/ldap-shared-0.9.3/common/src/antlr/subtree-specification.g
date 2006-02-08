header
{
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


package org.apache.ldap.common.subtree;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import javax.naming.Name;
import javax.naming.NamingException;

import org.apache.ldap.common.name.DnParser;
import org.apache.ldap.common.name.NameComponentNormalizer;
import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.LeafNode;
import org.apache.ldap.common.filter.SimpleNode;
import org.apache.ldap.common.filter.BranchNode;
import org.apache.ldap.common.filter.AbstractExprNode;
import org.apache.ldap.common.subtree.SubtreeSpecification;
import org.apache.ldap.common.subtree.SubtreeSpecificationModifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
}


// ----------------------------------------------------------------------------
// parser class definition
// ----------------------------------------------------------------------------

/**
 * The antlr generated subtree specification parser.
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc3672.html">RFC 3672</a>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
class AntlrSubtreeSpecificationParser extends Parser;


// ----------------------------------------------------------------------------
// parser options
// ----------------------------------------------------------------------------

options
{
    k = 2;

    defaultErrorHandler = false;
}


// ----------------------------------------------------------------------------
// parser initialization
// ----------------------------------------------------------------------------

{
    private static final Logger log = LoggerFactory.getLogger( AntlrSubtreeSpecificationParser.class );
    private DnParser dnParser;
    
    private boolean isNormalizing = false;
    NameComponentNormalizer normalizer;
    
    private Set chopBeforeExclusions = new HashSet();
    private Set chopAfterExclusions = new HashSet();

    SubtreeSpecificationModifier ssModifier = null;

    /**
     * Creates a (normalizing) subordinate DnParser for parsing LocalNames.
     * This method MUST be called for each instance while we cannot do
     * constructor overloading for this class.
     *
     * @return the DnParser to be used for parsing LocalNames
     */
    public void init()
    {
        try
        {
            if( isNormalizing )
            {
                dnParser = new DnParser( normalizer );
            }
            else
            {
                dnParser = new DnParser();
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed to initialize the subordinate DnParser for this AntlrSubtreeSpecificationParser";

            // We throw a NPE since this variable cannot be null for proper operation
            // so we can catch the null pointer before the dnParser is even used.

            throw new NullPointerException( "dnParser is null: " + msg );
        }
    }

    /**
     * Sets the NameComponentNormalizer for this parser's dnParser.
     */
    public void setNormalizer(NameComponentNormalizer normalizer)
    {
        this.normalizer = normalizer;
        this.isNormalizing = true;
    }
}


// ----------------------------------------------------------------------------
// parser productions
// ----------------------------------------------------------------------------

wrapperEntryPoint returns [SubtreeSpecification ss]
{
    log.debug( "entered wrapperEntryPoint()" );
    ss = null;
    SubtreeSpecification tempSs = null;
} :
    tempSs=subtreeSpecification "end"
    {
        ss = tempSs;
    }
    ;


subtreeSpecification returns [SubtreeSpecification ss]
{
    log.debug( "entered subtreeSpecification()" );
    // clear out ss and ssModifier in case something is left from the last parse
    ss = null;
    ssModifier = new SubtreeSpecificationModifier();
} :
    LBRACKET ( SP )*
        (
            (
              ss_base ss_base_follower
            | ss_specificExclusions ss_specificExclusions_follower
            | ss_minimum ss_minimum_follower
            | ss_maximum ss_maximum_follower
            | ss_specificationFilter
            )
            ( SP )*
        )?
    RBRACKET
    {
        ss = ssModifier.getSubtreeSpecification();
    }
    ;

    
ss_base_follower
    :
    ( SEP ( SP )*
        (
            ss_specificExclusions ss_specificExclusions_follower
            | ss_minimum ss_minimum_follower
            | ss_maximum ss_maximum_follower
            | ss_specificationFilter
        )
    )?
    ;

    
ss_specificExclusions_follower
    :
    ( SEP ( SP )*
        (
            ss_minimum ss_minimum_follower
            | ss_maximum ss_maximum_follower
            | ss_specificationFilter
        )
    )?
    ;

    
ss_minimum_follower
    :
    ( SEP ( SP )*
        (
            ss_maximum ss_maximum_follower
            | ss_specificationFilter
        )
    )?
    ;

    
ss_maximum_follower
    :
    ( SEP ( SP )*
        (
            ss_specificationFilter
        )
    )?
    ;


ss_base
{
    log.debug( "entered ss_base()" );
    Name base = null;
} :
    "base" ( SP )+ base=localName
    {
        ssModifier.setBase( base );
    }
    ;


ss_specificExclusions
{
    log.debug( "entered ss_specificExclusions()" );
} :
    "specificExclusions" ( SP )+ specificExclusions
    {
        ssModifier.setChopBeforeExclusions( chopBeforeExclusions );
        ssModifier.setChopAfterExclusions( chopAfterExclusions );
    }
    ;


specificExclusions
{
    log.debug( "entered specificExclusions()" );
} :
    LBRACKET
        ( ( SP )* specificExclusion
            ( SEP ( SP )* specificExclusion )*
        )?
    SP RBRACKET
    ;


specificExclusion
{
    log.debug( "entered specificExclusion()" );
} :
    chopBefore | chopAfter
    ;


chopBefore
{
    log.debug( "entered chopBefore()" );
    Name chopBeforeExclusion = null;
} :
    "chopBefore" COLON chopBeforeExclusion=localName
    {
        chopBeforeExclusions.add( chopBeforeExclusion );
    }
    ;


chopAfter
{
    log.debug( "entered chopAfter()" );
    Name chopAfterExclusion = null;
} :
    "chopAfter" COLON chopAfterExclusion=localName
    {
        chopAfterExclusions.add( chopAfterExclusion );
    }
    ;


ss_minimum
{
    log.debug( "entered ss_minimum()" );
    int minimum = 0;
} :
    "minimum" ( SP )+ minimum=baseDistance
    {
        ssModifier.setMinBaseDistance( minimum );
    }
    ;


ss_maximum
{
    log.debug( "entered ss_maximum()" );
    int maximum = 0;
} :
    "maximum" ( SP )+ maximum=baseDistance
    {
        ssModifier.setMaxBaseDistance( maximum );
    }
    ;


ss_specificationFilter
{
    log.debug( "entered ss_specificationFilter()" );
    ExprNode theRefinement = null;
}:
    "specificationFilter" ( SP )+ theRefinement=refinement
    {
        ssModifier.setRefinement( theRefinement );
    }
    ;


localName returns [Name name] 
{
    log.debug( "entered localName()" );
    name = null;
} :
    token:DQUOTEDSTRING
    {
        name = dnParser.parse( token.getText() );
    }
    ;
    exception
    catch [Exception e]
    {
        throw new RecognitionException( "dnParser failed." + e.getMessage() );
    }


baseDistance returns [int distance]
{
    log.debug( "entered baseDistance()" );
    distance = 0;
} :
    token:NUMBER
    {
        distance = Integer.parseInt( token.getText() );
    }
    ;


refinement returns [ExprNode node]
{
    log.debug( "entered refinement()" );
    node = null;
} :
    node=item | node=and | node=or | node=not
    ;


item returns [LeafNode node]
{
    log.debug( "entered item()" );
    node = null;
    String oid = null;
} :
    "item" COLON oid=objectIdentifier
    {
        node = new SimpleNode( "objectClass" , oid , AbstractExprNode.EQUALITY );
    }
    ;


objectIdentifier returns [String oid]
{
    oid = null;
} :
    token1:DESCR
    {
        oid = token1.getText();
    }
    |
    token2:NUMERICOID
    {
        oid = token2.getText();
    }
    ;


and returns [BranchNode node]
{
    log.debug( "entered and()" );
    node = null;
    ArrayList children = null; 
} :
    "and" COLON children=refinements
    {
        node = new BranchNode( AbstractExprNode.AND , children );
    }
    ;


or returns [BranchNode node]
{
    log.debug( "entered or()" );
    node = null;
    ArrayList children = null; 
} :
    "or" COLON children=refinements
    {
        node = new BranchNode( AbstractExprNode.OR , children );
    }
    ;


not returns [BranchNode node]
{
    log.debug( "entered not()" );
    node = null;
    ArrayList children = null;
} :
    "not" COLON children=refinements
    {
        node = new BranchNode( AbstractExprNode.NOT , children );
    }
    ;


refinements returns [ArrayList children]
{
    log.debug( "entered refinements()" );
    children = null;
    ExprNode child = null;
    ArrayList tempChildren = new ArrayList();
} :
    LBRACKET
    ( SP
        child=refinement
        {
            tempChildren.add( child );
        }
        ( SEP ( SP )* child=refinement
        {
            tempChildren.add( child );
        } )*
    )? ( SP )* RBRACKET
    {
        children = tempChildren;
    }
    ;


// ----------------------------------------------------------------------------
// lexer class definition
// ----------------------------------------------------------------------------

/**
 * The parser's primary lexer.
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc3672.html">RFC 3672</a>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
class AntlrSubtreeSpecificationLexer extends Lexer;


// ----------------------------------------------------------------------------
// lexer options
// ----------------------------------------------------------------------------

options
{
    k = 2;

    charVocabulary = '\u0001'..'\u0127';

    testLiterals = false;
}


//----------------------------------------------------------------------------
// lexer initialization
//----------------------------------------------------------------------------

{
    private static final Logger log = LoggerFactory.getLogger( AntlrSubtreeSpecificationLexer.class );
}


// ----------------------------------------------------------------------------
// attribute description lexer rules from models
// ----------------------------------------------------------------------------

SP : ' ';

COLON : ':' { log.debug( "matched COLON(':')" ); } ;

LBRACKET : '{' { log.debug( "matched LBRACKET('{')" ); } ;

RBRACKET : '}' { log.debug( "matched RBRACKET('}')" ); } ;

DQUOTE : '"' { log.debug( "matched DQUOTE('\"')" ); } ;

SEP : ',' { log.debug( "matched SEP(',')" ); } ;

DQUOTEDSTRING : DQUOTE! ( SAFEUTF8CHAR )* DQUOTE! { log.debug( "matched DQUOTEDSTRING: \"" + getText() + "\"" ); } ;

DESCR options { testLiterals = true; } : ALPHA ( ALPHA | DIGIT | '-' )* { log.debug( "matched DESCR" ); } ;

// This rule is required to prevent nondeterminism problem caused by NUMBER and NUMERICOID rules.

NUMBER_OR_NUMERICOID
    :
    ( NUMBER DOT ) => NUMERICOID
    {
        $setType(NUMERICOID);
    }
    |
    NUMBER
    {
        $setType(NUMBER);
    }
    ;

protected NUMBER: DIGIT | ( LDIGIT ( DIGIT )+ ) { log.debug( "matched NUMBER: " + getText() ); } ;

protected NUMERICOID: NUMBER ( DOT NUMBER )+ { log.debug( "matched NUMERICOID: " + getText() ); } ;

protected DOT: '.' ;

protected DIGIT: '0' | LDIGIT ;

protected LDIGIT: '1'..'9' ;

protected ALPHA: 'A'..'Z' | 'a'..'z' ;

// This is all messed up - could not figure out how to get antlr to represent
// the safe UTF-8 character set from RFC 3642 for production SafeUTF8Character

protected SAFEUTF8CHAR:
    '\u0001'..'\u0021' |
    '\u0023'..'\u007F' |
    '\u00c0'..'\u00d6' |
    '\u00d8'..'\u00f6' |
    '\u00f8'..'\u00ff' |
    '\u0100'..'\u1fff' |
    '\u3040'..'\u318f' |
    '\u3300'..'\u337f' |
    '\u3400'..'\u3d2d' |
    '\u4e00'..'\u9fff' |
    '\uf900'..'\ufaff' ;

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
package org.apache.asn1.ber.digester ;


import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.asn1.ber.BERDecoder;
import org.apache.asn1.ber.BERDecoderCallback;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.AbstractStatefulDecoder;
import org.apache.asn1.codec.stateful.StatefulDecoder;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.primitives.BooleanStack;
import org.apache.commons.collections.primitives.ByteStack;
import org.apache.commons.collections.primitives.CharStack;
import org.apache.commons.collections.primitives.DoubleStack;
import org.apache.commons.collections.primitives.FloatStack;
import org.apache.commons.collections.primitives.IntStack;
import org.apache.commons.collections.primitives.LongStack;
import org.apache.commons.collections.primitives.ShortStack;


/**
 * A special BER TLV event rulesBase.  This class was inspired by the XML
 * rulesBase in Jakarta Commons.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDigester extends AbstractStatefulDecoder
{
    /**
     * For now this corresponds to a tag of class Universal with an id
     * of 15.  This has been reserved for future use but to my knowledge
     * currently it is not being used for anything specifically.  Hence
     * we use it as the return value instead of throwing an empty stack
     * exception when the top of the tag stack is requested.
     */
    public static final int NO_TOP_TAG = 0x0f000000 ;

    /** the underlying decoder used by this rulesBase */
    private BERDecoder decoder ;
    /** the object stack where rules push and pop ASN.1 POJO stubs */
    private ArrayStack objectStack ;
    /** the primitive boolean stack where rules push and pop booleans */
    private BooleanStack booleanStack ;
    /** the primitive char stack where rules push and pop chars */
    private CharStack charStack ;
    /** the primitive float stack where rules push and pop floats */
    private FloatStack floatStack ;
    /** the primitive double stack where rules push and pop doubles */
    private DoubleStack doubleStack ;
    /** the primitive int stack where rules push and pop ints */
    private IntStack intStack ;
    /** the primitive byte stack where rules push and pop bytes */
    private ByteStack byteStack ;
    /** the primitive long stack where rules push and pop longs */
    private LongStack longStack ;
    /** the primitive short stack where rules push and shorts */
    private ShortStack shortStack ;
    /** the tag stack used to store the nesting pattern */
    private IntStack tagStack ;
    /** the rules base used by this digester */
    private Rules rules ;
    /** the currently matched rules */
    private List matched ;
    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load Digester itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    private ClassLoader classLoader = null ;
    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects.  Default is <code>false</code>.
     */
    private boolean useContextClassLoader = false ;
    /**
     * The "root" element of the stack (in other words, the last object
     * that was popped.
     */
    private Object root = null ;
    /** The monitor used by this digester */
    private BERDigesterMonitor monitor = null ;


    /**
     * Creates a BER TLV event rulesBase.
     */
    public BERDigester()
    {
        this.rules = new RulesBase() ;
        this.rules.setDigester( this ) ;
        this.tagStack = new IntStack() ;
        this.objectStack = new ArrayStack() ;
        this.booleanStack = new BooleanStack() ;
        this.charStack = new CharStack() ;
        this.byteStack = new ByteStack() ;
        this.shortStack = new ShortStack() ;
        this.intStack = new IntStack() ;
        this.longStack = new LongStack() ;
        this.floatStack = new FloatStack() ;
        this.doubleStack = new DoubleStack() ;
        this.decoder = new BERDecoder() ;
        this.decoder.setCallback( new DigesterCallback() ) ;
        this.monitor = new BERDigesterLoggingMonitor() ;
    }
    
    
    // ------------------------------------------------------------------------
    // StatefulDecoder implementation
    // ------------------------------------------------------------------------


    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder
     * #decode(java.lang.Object)
     */
    public void decode( Object encoded ) throws DecoderException
    {
        decoder.decode( encoded ) ;
    }


    // ------------------------------------------------------------------------
    // BERDecoderCallback implementation
    // ------------------------------------------------------------------------


    class DigesterCallback implements BERDecoderCallback
    {
        /* (non-Javadoc)
         * @see org.apache.asn1.ber.BERDecoderCallback#tagDecoded(
         * org.apache.asn1.ber.Tuple)
         */
        public void tagDecoded( Tuple tlv )
        {
            tagStack.push( tlv.getRawPrimitiveTag() ) ;
            matched = rules.match( tagStack ) ;
            fireTagEvent( tlv.getId(), tlv.isPrimitive(), tlv.getTypeClass() ) ;
        }


        /* (non-Javadoc)
         * @see org.apache.asn1.ber.BERDecoderCallback#lengthDecoded(
         * org.apache.asn1.ber.Tuple)
         */
        public void lengthDecoded( Tuple tlv )
        {
            fireLengthEvent( tlv.getLength() ) ;
        }


        /* (non-Javadoc)
         * @see org.apache.asn1.ber.BERDecoderCallback#partialValueDecoded(
         * org.apache.asn1.ber.Tuple)
         */
        public void partialValueDecoded( Tuple tlv )
        {
            fireValueEvent( tlv.getLastValueChunk() ) ;
        }


        /* (non-Javadoc)
         * @see org.apache.asn1.codec.stateful.DecoderCallback#decodeOccurred(
         * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Object)
         */
        public void decodeOccurred( StatefulDecoder decoder, Object decoded )
        {
            /*
             * must reset the matched rules here because the nested TLVs
             * overwrite the matched rules of a constructed TLV so it must
             * be set once again
             */
            matched = rules.match( tagStack ) ;
            fireFinishEvent() ;
            tagStack.pop() ;
            
            if ( BERDigester.this.tagStack.empty() )
            {
                BERDigester.this.decodeOccurred( getRoot() ) ;
            }
        }
    }


    // ------------------------------------------------------------------------
    // Digester like methods
    // ------------------------------------------------------------------------


    /**
     * Register a new <code>Rule</code> matching the specified pattern.
     *
     * @param pattern tag nesting pattern
     * @param rule the Rule to add to this BERDigester
     */
    public void addRule( int[] pattern, Rule rule )
    {
        rules.add( pattern, rule ) ;
        rule.setDigester( this ) ;
    }
               
    
    /**
     * Return the Rules implementation object containing our rules collection 
     * and associated matching policy.
     * 
     * @return the entire set of rules
     */
    public Rules getRules()
    {
        return rules ;
    }
             

    /**
     * Clear the current contents of the object stack.
     */
    public void clear()
    {
        root = null ;
        tagStack.clear() ;
        objectStack.clear() ;
        booleanStack.clear() ;
        byteStack.clear() ;
        shortStack.clear() ;
        intStack.clear() ;
        longStack.clear() ;
        floatStack.clear() ;
        doubleStack.clear() ;
    }


    // ------------------------------------------------------------------------
    // Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the object stack.
     * 
     * @return the size of the object stack
     */
    public int getCount()
    {
        return objectStack.size() ;
    }
    
    
    /**
     * Return the top object on the stack without removing it.
     * 
     * @return the top object
     * @throws EmptyStackException if there are no objects on the stack
     */
    public Object peek() 
    {
        return ( objectStack.peek() ) ;
    }
    
    
    /**
     * Return the n'th object down the stack, where 0 is the top element and 
     * [getCount()-1] is the bottom element.
     * 
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no objects on the stack
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public Object peek( int n )
    {
        return ( objectStack.peek( n ) ) ;
    }
              
    
    /**
     * Pop the top object off of the stack, and return it.
     * 
     * @return the top object off of the stack
     * @throws EmptyStackException if there are no objects on the stack
     */
    public Object pop()
    {
        return ( objectStack.pop() ) ;
    }
              
    
    /**
     * Push a new object onto the top of the object stack.
     * 
     * @param object the object to push onto the stack
     */
    public void push( Object object )
    {
        if ( objectStack.size() == 0 ) 
        {
            root = object ;
        }

        objectStack.push( object ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive boolean Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the boolean stack.
     *
     * @return the size of the boolean stack
     */
    public int getBooleanCount()
    {
        return booleanStack.size() ;
    }


    /**
     * Return the top boolean on the stack without removing it.
     *
     * @return the top boolean
     * @throws EmptyStackException if there are no more boolean elements left
     */
    public boolean peekBoolean()
    {
        return ( booleanStack.peek() ) ;
    }


    /**
     * Return the n'th boolean down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more boolean elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public boolean peekBoolean( int n )
    {
        return ( booleanStack.peek( n ) ) ;
    }


    /**
     * Pop the top boolean off of the stack, and return it.
     *
     * @return the top boolean off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public boolean popBoolean()
    {
        return ( booleanStack.pop() ) ;
    }


    /**
     * Push a new boolean onto the top of the boolean stack.
     *
     * @param bit the boolean to push onto the stack
     */
    public void pushBoolean( boolean bit )
    {
        booleanStack.push( bit ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive char Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the char stack.
     *
     * @return the size of the char stack
     */
    public int getCharCount()
    {
        return charStack.size() ;
    }


    /**
     * Return the top char on the stack without removing it.
     *
     * @return the top char
     * @throws EmptyStackException if there are no more char elements left
     */
    public char peekChar()
    {
        return ( charStack.peek() ) ;
    }


    /**
     * Return the n'th char down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more char elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public char peekChar( int n )
    {
        return ( charStack.peek( n ) ) ;
    }


    /**
     * Pop the top char off of the stack, and return it.
     *
     * @return the top char off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public char popChar()
    {
        return ( charStack.pop() ) ;
    }


    /**
     * Push a new char onto the top of the char stack.
     *
     * @param ch the char to push onto the stack
     */
    public void pushChar( char ch )
    {
        charStack.push( ch ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive byte Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the byte stack.
     *
     * @return the size of the byte stack
     */
    public int getByteCount()
    {
        return byteStack.size() ;
    }


    /**
     * Return the top byte on the stack without removing it.
     *
     * @return the top byte
     * @throws EmptyStackException if there are no more byte elements left
     */
    public byte peekByte()
    {
        return ( byteStack.peek() ) ;
    }


    /**
     * Return the n'th byte down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more byte elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public byte peekByte( int n )
    {
        return ( byteStack.peek( n ) ) ;
    }


    /**
     * Pop the top byte off of the stack, and return it.
     *
     * @return the top byte off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public byte popByte()
    {
        return ( byteStack.pop() ) ;
    }


    /**
     * Push a new byte onto the top of the byte stack.
     *
     * @param bite the byte to push onto the stack
     */
    public void pushByte( byte bite )
    {
        byteStack.push( bite ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive short Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the short stack.
     *
     * @return the size of the short stack
     */
    public int getShortCount()
    {
        return shortStack.size() ;
    }


    /**
     * Return the top short on the stack without removing it.
     *
     * @return the top short
     * @throws EmptyStackException if there are no more short elements left
     */
    public short peekShort()
    {
        return ( shortStack.peek() ) ;
    }


    /**
     * Return the n'th short down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more short elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public short peekShort( int n )
    {
        return ( shortStack.peek( n ) ) ;
    }


    /**
     * Pop the top short off of the stack, and return it.
     *
     * @return the top short off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public short popShort()
    {
        return ( shortStack.pop() ) ;
    }


    /**
     * Push a new short onto the top of the short stack.
     *
     * @param element the short to push onto the stack
     */
    public void pushShort( short element )
    {
        shortStack.push( element ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive int Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the int stack.
     *
     * @return the size of the int stack
     */
    public int getIntCount()
    {
        return intStack.size() ;
    }


    /**
     * Return the top int on the stack without removing it.
     *
     * @return the top int
     * @throws EmptyStackException if there are no more int elements left
     */
    public int peekInt()
    {
        return ( intStack.peek() ) ;
    }


    /**
     * Return the n'th int down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more int elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public int peekInt( int n )
    {
        return ( intStack.peek( n ) ) ;
    }


    /**
     * Pop the top int off of the stack, and return it.
     *
     * @return the top int off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public int popInt()
    {
        return ( intStack.pop() ) ;
    }


    /**
     * Push a new int onto the top of the int stack.
     *
     * @param element the int to push onto the stack
     */
    public void pushInt( int element )
    {
        intStack.push( element ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive long Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the long stack.
     *
     * @return the size of the long stack
     */
    public int getLongCount()
    {
        return longStack.size() ;
    }


    /**
     * Return the top long on the stack without removing it.
     *
     * @return the top long
     * @throws EmptyStackException if there are no more long elements left
     */
    public long peekLong()
    {
        return ( longStack.peek() ) ;
    }


    /**
     * Return the n'th long down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more long elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public long peekLong( int n )
    {
        return ( longStack.peek( n ) ) ;
    }


    /**
     * Pop the top long off of the stack, and return it.
     *
     * @return the top long off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public long popLong()
    {
        return ( longStack.pop() ) ;
    }


    /**
     * Push a new long onto the top of the long stack.
     *
     * @param element the long to push onto the stack
     */
    public void pushLong( long element )
    {
        longStack.push( element ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive float Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the float stack.
     *
     * @return the size of the float stack
     */
    public int getFloatCount()
    {
        return floatStack.size() ;
    }


    /**
     * Return the top float on the stack without removing it.
     *
     * @return the top float
     * @throws EmptyStackException if there are no more float elements left
     */
    public float peekFloat()
    {
        return ( floatStack.peek() ) ;
    }


    /**
     * Return the n'th float down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more float elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public float peekFloat( int n )
    {
        return ( floatStack.peek( n ) ) ;
    }


    /**
     * Pop the top float off of the stack, and return it.
     *
     * @return the top float off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public float popFloat()
    {
        return ( floatStack.pop() ) ;
    }


    /**
     * Push a new float onto the top of the float stack.
     *
     * @param element the float to push onto the stack
     */
    public void pushFloat( float element )
    {
        floatStack.push( element ) ;
    }


    // ------------------------------------------------------------------------
    // Primitive double Stack Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the double stack.
     *
     * @return the size of the double stack
     */
    public int getDoubleCount()
    {
        return doubleStack.size() ;
    }


    /**
     * Return the top double on the stack without removing it.
     *
     * @return the top double
     * @throws EmptyStackException if there are no more double elements left
     */
    public double peekDouble()
    {
        return ( doubleStack.peek() ) ;
    }


    /**
     * Return the n'th double down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the element index
     * @return the element at the index
     * @throws EmptyStackException if there are no more double elements left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public double peekDouble( int n )
    {
        return ( doubleStack.peek( n ) ) ;
    }


    /**
     * Pop the top double off of the stack, and return it.
     *
     * @return the top double off of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public double popDouble()
    {
        return ( doubleStack.pop() ) ;
    }


    /**
     * Push a new double onto the top of the double stack.
     *
     * @param element the double to push onto the stack
     */
    public void pushDouble( double element )
    {
        doubleStack.push( element ) ;
    }


    /**
     * This method allows you to access the root object that has been
     * created after decoding.
     * 
     * @return the root object that has been created after decoding or null if 
     * the rulesBase has not decoded any PDUs yet.
     */
    public Object getRoot()
    {
        return this.root ;
    }
    

    /**
     * Set the Rules implementation object containing our rules collection 
     * and associated matching policy.
     * 
     * @param rules the rules to add to this rulesBase
     */
    public void setRules( Rules rules )
    {
        this.rules = rules ;
        this.rules.setDigester( this ) ;
    }
    
    
    // ------------------------------------------------------------------------
    // Read Only Tag Stack (Primitive IntStack) Operations
    // ------------------------------------------------------------------------


    /**
     * Return the current depth of the Tag stack.
     *
     * @return the size of the Tag stack
     */
    public int getTagCount()
    {
        return tagStack.size() ;
    }


    /**
     * Return the n'th tag down the Tag stack, where 0 is the top element and
     * [getCount()-1] is the bottom element.
     *
     * @param n the Tag index
     * @return the Tag at the index
     * @throws EmptyStackException if there are no more int Tags left
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public int getTag( int n )
    {
        return ( tagStack.peek( n ) ) ;
    }


    /**
     * Gets the raw int for the tag of the TLV currently being processed hence
     * the tag on the top of the stack.  The tag's int has the primitive flag
     * dubbed out so it appears to represent a primitive TLV even when the TLV
     * may be constructed.
     * 
     * @return the raw int for the tag of the TLV currently being processed, or
     * NO_TOP_TAG if there is no TLV currently being processed.
     */
    public int getTopTag()
    {
        if ( tagStack.size() <= 0 )
        {
            return NO_TOP_TAG ;
        }

        return tagStack.peek() ;
    }

    
    // ------------------------------------------------------------------------
    // ClassLoader Related Methods
    // ------------------------------------------------------------------------


    /**
     * Return the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     <code>useContextClassLoader</code> property is set to true</li>
     * <li>The class loader used to load the Digester class itself.
     * </ul>
     */
    public ClassLoader getClassLoader() 
    {
        if ( classLoader != null ) 
        {
            return ( classLoader ) ;
        }

        if ( useContextClassLoader) 
        {
            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader() ;

            if ( classLoader != null )
            {
                return ( classLoader ) ;
            }
        }

        return ( getClass().getClassLoader() ) ;
    }


    /**
     * Set the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or <code>null</code>
     *  to revert to the standard rules
     */
    public void setClassLoader( ClassLoader classLoader ) 
    {
        this.classLoader = classLoader ;
    }


    /**
     * Return the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() 
    {
        return useContextClassLoader ;
    }


    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes that are defined in various rules.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use Context ClassLoader.
     */
    public void setUseContextClassLoader( boolean use ) 
    {
        useContextClassLoader = use ; 
    }


    // ------------------------------------------------------------------------
    // Event fireing routines that trigger rules
    // ------------------------------------------------------------------------
    
    
    void fireTagEvent( int id, boolean isPrimitive, TypeClass typeClass )
    {
        Iterator rules = null ;

        if ( matched == null )
        {
            rules = Collections.EMPTY_LIST.iterator() ;
        }
        else
        {
            rules = matched.iterator() ;
        }

        while ( rules.hasNext() )
        {
            Rule rule = ( Rule ) rules.next() ;
            
            try 
            {
                rule.tag( id, isPrimitive, typeClass ) ;
            } 
            catch ( RuntimeException e ) 
            {
                monitor.ruleFailed( this, rule, "Rule.tag() threw exception", e ) ;
                throw e ;
            }
            catch ( Error e ) 
            {
                monitor.ruleFailed( this, rule, "Rule.tag() threw error", e ) ;
                throw e ;
            }
        }
    }
    
    
    void fireLengthEvent( int length )
    {
        Iterator rules = null ;

        if ( matched == null )
        {
            rules = Collections.EMPTY_LIST.iterator() ;
        }
        else
        {
            rules = matched.iterator() ;
        }

        while ( rules.hasNext() )
        {
            Rule rule = ( Rule ) rules.next() ;
            
            try 
            {
                rule.length( length ) ;
            } 
            catch ( RuntimeException e )
            {
                monitor.ruleFailed( this, rule, "Rule.length() threw exception", e ) ;
                throw e ;
            }
            catch ( Error e ) 
            {
                monitor.ruleFailed( this, rule, "Rule.length() threw error", e ) ;
                throw e ;
            }
        }
    }
    
    
    void fireValueEvent( ByteBuffer buf )
    {
        Iterator rules = null ;

        if ( matched == null )
        {
            rules = Collections.EMPTY_LIST.iterator() ;
        }
        else
        {
            rules = matched.iterator() ;
        }

        while ( rules.hasNext() )
        {
            Rule rule = ( Rule ) rules.next() ;
            
            try 
            {
                rule.value( buf ) ;

                // need to rewind the buffer after rule exhausts it
                buf.rewind() ;
            }
            catch ( RuntimeException e )
            {
                monitor.ruleFailed( this, rule, "Rule.value() threw exception", e ) ;
                throw e ;
            }
            catch ( Error e ) 
            {
                monitor.ruleFailed( this, rule, "Rule.value() threw exception", e ) ;
                throw e ;
            }
        }
    }
    
    
    void fireFinishEvent()
    {
        Rule rule = null ;
        HashSet seen = null ;

        if ( matched != null )
        {
            seen = new HashSet() ;
            
            for ( int i = 0; i < matched.size(); i++ ) 
            {
                try
                {
                    rule = ( Rule ) matched.get( i ) ;
                    rule.finish() ;
                    monitor.ruleCompleted( this, rule ) ;
                    seen.add( rule ) ;
                } 
                catch ( RuntimeException e )
                {
                    monitor.ruleFailed( this, rule, "Rule.finish() threw exception", e ) ;
                    throw e ;
                }
                catch ( Error e ) 
                {
                    monitor.ruleFailed( this, rule, "Rule.finish() threw error", e ) ;
                    throw e ;
                }
            }
        }
    }
}

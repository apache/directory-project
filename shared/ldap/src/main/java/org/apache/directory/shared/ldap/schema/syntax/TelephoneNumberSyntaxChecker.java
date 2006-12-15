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
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.naming.NamingException;


import org.apache.directory.shared.ldap.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * A SyntaxChecker which verifies that a value is a TelephoneNumber according to ITU
 * recommendation E.123 (which is quite vague ...).
 * 
 * A valid Telephone number respect more or less this syntax :
 * 
 * " *[+]? *((\([0-9- ]+\))|[0-9- ]+)+"
 * 
 * If needed, and to allow more syntaxes, a list of regexps has been added
 * which can be initialized to other values
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class TelephoneNumberSyntaxChecker implements SyntaxChecker
{
    /** The Syntax OID, according to RFC 4517, par. 3.3.31 */
    public static final String OID = "1.3.6.1.4.1.1466.115.121.1.50";
    
    /** Other regexps to extend the initial one */
    private List<String> regexps;
    
    /** Other regexp to extend the initial one, compiled */
    private List<Pattern> compiledREs;
    
    /** The default pattern used to check a TelephoneNumber */
    private final String DEFAULT_REGEXP = "^ *[+]? *((\\([0-9- ]+\\))|[0-9- ]+)+$";
    
    /** The compiled default pattern */
    private Pattern defaultPattern =  Pattern.compile( DEFAULT_REGEXP );
    
    /** A flag set when only the default regexp should be tested */
    protected boolean defaultMandatory = false;
    
    /**
     * Creates a new instance of TelephoneNumberSyntaxChecker.
     */
    public TelephoneNumberSyntaxChecker()
    {
    }

    /**
     * Add a new valid regexp for a Telephone number
     * @param regexp The new regexp to check
     */
    public void addRegexp( String regexp )
    {
        if ( defaultMandatory )
        {
            return;
        }
        
        try
        {
            Pattern compiledRE = Pattern.compile( regexp );

            if ( regexps == null )
            { 
                regexps = new ArrayList<String>();
                compiledREs = new ArrayList<Pattern>();
            }
            
            regexps.add( regexp );
            compiledREs.add( compiledRE );
        }
        catch ( PatternSyntaxException pse )
        {
            return;
        }
    }
    
    /**
     * 
     */
    public void setDefaultRegexp( String regexp )
    {
        try
        {
            defaultPattern = Pattern.compile( regexp );

            defaultMandatory = true;
            regexps = null;
            compiledREs = null;
        }
        catch ( PatternSyntaxException pse )
        {
            return;
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.SyntaxChecker#assertSyntax(java.lang.Object)
     */
    public void assertSyntax( Object value ) throws NamingException
    {
        if ( ! isValidSyntax( value ) )
        {
            throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.SyntaxChecker#getSyntaxOid()
     */
    public String getSyntaxOid()
    {
        return OID;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.SyntaxChecker#isValidSyntax(java.lang.Object)
     */
    public boolean isValidSyntax( Object value )
    {
        String strValue;

        if ( value == null )
        {
            return false;
        }
        
        if ( value instanceof String )
        {
            strValue = ( String ) value;
        }
        else if ( value instanceof byte[] )
        {
            strValue = StringTools.utf8ToString( ( byte[] ) value ); 
        }
        else
        {
            strValue = value.toString();
        }

        if ( strValue.length() == 0 )
        {
            return false;
        }
        
        // We will use a regexp to check the TelephoneNumber.
        if ( defaultMandatory )
        {
            // We have a unique regexp to check, the default one
            return defaultPattern.matcher( strValue ).matches();
        }
        else
        {
            if ( defaultPattern.matcher( strValue ).matches() )
            {
                return true;
            }
            else
            {
                if ( compiledREs == null )
                {
                    return false;
                }
                
                // The default is not enough, let's try
                // the other regexps
                for ( Pattern pattern:compiledREs )
                {
                    if ( pattern.matcher( strValue ).matches() )
                    {
                        return true;
                    }
                }
                
                return false;
            }
        }
    }
}

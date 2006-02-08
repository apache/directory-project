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
package org.apache.asn1.ber.digester;




/**
 * Document this class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class RuleRegistration
{
    /** the pattern that is used to register a rule with */
    private final int[] pattern;
    /** the rule registered with the pattern */
    private final Rule rule;


    /**
     * Create a new rule registration used to track the rules and patterns that
     * are registered with the digester.
     *
     * @param pattern the pattern used to register a rule with
     * @param rule the rule registered with the pattern
     */
    public RuleRegistration( int[] pattern, Rule rule )
    {
        this.rule = rule;
        this.pattern = pattern;
    }


    /**
     * Gets the pattern used to register a rule.
     *
     * @return the pattern that is used to register a rule
     */
    public int[] getPattern()
    {
        return pattern;
    }


    /**
     * Gets the rule registered with the pattern.
     *
     * @return the rule registered with the pattern
     */
    public Rule getRule()
    {
        return rule;
    }
}

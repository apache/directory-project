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


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * A logging BER digestor monitor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDigesterLoggingMonitor implements BERDigesterMonitor
{
    /** logging facility for the digester */
    private static Logger log = LoggerFactory.getLogger( "BERDigester" ) ;


    /**
     * Callback used to log a rule callback failure when triggered by the
     * digester.
     *
     * @param digester the digester triggering the rule
     * @param rule     the rule that failed
     * @param msg      a message regarding the failure
     * @param fault    the fault that caused the failure
     */
    public void ruleFailed( BERDigester digester, Rule rule, String msg,
                            Throwable fault )
    {
        if ( log.isErrorEnabled() )
        {
            log.error( "Error while triggering rule " + rule
                    + " with digester " + digester + ": " + msg, fault ) ;
        }
    }


    /**
     * Callback used to monitor successful rule firing.
     *
     * @param digester the digester triggering the rule
     * @param rule     the rule that completed firing successfully
     */
    public void ruleCompleted( BERDigester digester, Rule rule )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Rule " + rule + " fired successfully by digester "
                    + digester ) ;
        }
    }
}

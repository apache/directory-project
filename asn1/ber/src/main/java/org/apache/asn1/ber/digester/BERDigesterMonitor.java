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




/**
 * The monitor interface for a BER digester.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public interface BERDigesterMonitor
{
    /**
     * Callback used to monitor rule callback failures on triggered
     * rules.
     *
     * @param digester the digester triggering the rule
     * @param rule the rule that failed
     * @param msg a message regarding the failure
     * @param fault the fault that caused the failure
     */
    void ruleFailed( BERDigester digester, Rule rule, String msg,
                     Throwable fault ) ;

    /**
     * Callback used to monitor successful rule firing.
     *
     * @param digester the digester triggering the rule
     * @param rule the rule that completed firing successfully
     */
    void ruleCompleted( BERDigester digester, Rule rule ) ;
}

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
package org.apache.kerberos.kdc;

import org.apache.protocol.common.chain.Context;
import org.apache.protocol.common.chain.impl.CommandBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorContext extends CommandBase
{
    /** the log for this class */
    private static final Logger log = LoggerFactory.getLogger( MonitorContext.class );

    public boolean execute( Context context ) throws Exception
    {
        KdcContext kdcContext = (KdcContext) context;

        if ( log.isDebugEnabled() )
        {
            log.debug( "Monitoring context:"
                    + "\n\tconfig:                 " + kdcContext.getConfig()
                    + "\n\tstore:                  " + kdcContext.getStore()
                    + "\n\trequest:                " + kdcContext.getRequest()
                    + "\n\treply:                  " + kdcContext.getReply()
                     );
        }
        
        return CONTINUE_CHAIN;
    }
}

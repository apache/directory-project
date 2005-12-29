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
package org.apache.ldap.common.berlib.asn1.decoder.modifydn ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.message.ModifyDnRequest;
import org.apache.ldap.common.util.StringTools;

import java.nio.ByteBuffer;


/**
 * A BERDigester rule to set the new RDN for the ModifyDnRequest's newRdn
 * field.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class ModifyDnRequestNewRdnRule extends PrimitiveOctetStringRule
{
    private boolean byPass = false ;
    // the current ModifyDnRequest underneath whose octets we set
    private ModifyDnRequest req = null ;



    public ModifyDnRequestNewRdnRule()
    {
        super( UniversalTag.OCTET_STRING ) ;
    }

    
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        // peek at the ModifyDnRequest underneath whose octets we set
        req = ( ModifyDnRequest ) getDigester().peek() ;

        // byPass this rule firing if the newRdn has already been set or
        // if the name has not yet been set - because then the data is the name
        if ( req.getNewRdn() != null || req.getName() == null )
        {
            byPass = true ;
            return ;
        }

        super.tag( id, isPrimitive, typeClass ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#length(int)
     */
    public void length( int length )
    {
        if ( byPass )
        {
            return ;
        }

        super.length( length ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        if ( byPass )
        {
            return ;
        }

        super.value( buf ) ;
    }


    /**
     * Allows the super method to push a ByteBuffer onto the top of the stack
     * which contains the drained contents of the superclass' ByteAccumulator.
     * This ByteBuffer is popped first then used to populate the credentials.
     * There is no need to copy this buffer since it will not be used again
     * by the ByteAccumulator of the superclass so we should be able to use
     * the byte[] based backing store if one is present.  However it might
     * have to be copied even then.  Situations requiring a copy are when the
     * buffer has a limit less than the capacity or when there is no
     * accessible array to the buffer.
     *
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        if ( byPass )
        {
            req = null ;
            byPass = false ;
            return ;
        }

        // pushes a ByteBuffer onto the stack
        super.finish() ;

        // pop the ByteBuffer the super method pushed
        ByteBuffer buf = ( ByteBuffer ) getDigester().pop() ;

        byte[] octets = null ;
        
        if ( buf.limit() == buf.capacity() && buf.hasArray() )
        {
            // use the backing store
            octets = buf.array() ;
        }
        else
        {
            // copy because we don't have accessible array or data < array
            octets = new byte[buf.remaining()] ;
            buf.get( octets ) ;
        }

        req.setNewRdn( StringTools.utf8ToString( octets ) ) ;
        byPass = false ;
        req = null ;
    }
}

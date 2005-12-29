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
package org.apache.ldap.common.berlib.asn1.decoder.modify ;


import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.message.LockableAttributeImpl;
import org.apache.ldap.common.message.ModifyRequest;

import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.nio.ByteBuffer;


/**
 * A BERDigester rule to create and push ModifyRequest's attribute.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class ModificationItemRule extends PrimitiveOctetStringRule
{
    public ModificationItemRule()
    {
        super( UniversalTag.OCTET_STRING ) ;
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
        if ( isConstructed() )
        {
            return ;
        }

        // drain the accumulator
        ByteBuffer buf = ( ByteBuffer ) getAccumulator().drain( 0 ) ;

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


        int op = getDigester().popInt() ;
        int modCode = -1 ;

        switch( op )
        {
            case( 0 ):
                modCode = DirContext.ADD_ATTRIBUTE ;
                break ;
            case( 1 ):
                modCode = DirContext.REMOVE_ATTRIBUTE ;
                break ;
            case( 2 ):
                modCode = DirContext.REPLACE_ATTRIBUTE ;
                break ;
            default:
                throw new IllegalStateException(
                    "Expecting 0, 1, 2 int value for add, delete, or replace"
                    + " operation on entry attribute but got a " + op ) ;
        }

        LockableAttributeImpl attr =
                new LockableAttributeImpl( new String( octets ) ) ;
        ModifyRequest req = ( ModifyRequest ) getDigester().peek() ;
        ModificationItem mods = new ModificationItem( modCode, attr ) ;
        req.addModification( mods ) ;
        getDigester().push( mods ) ;

        // clean up
        setConstructed( false ) ;
    }
}

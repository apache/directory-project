/*
 *   Copyright 2006 The Apache Software Foundation
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
package org.apache.directory.server.ldap.support.extended;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.directory.shared.ldap.codec.extended.operations.StoredProcedure;
import org.apache.directory.shared.ldap.codec.extended.operations.StoredProcedure.StoredProcedureParameter;
import org.apache.directory.shared.ldap.util.ClassUtils;
import org.apache.directory.shared.ldap.util.SpringClassUtils;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$ $Date$
 */
public class JavaStoredProcedureExtendedOperationHandler implements LanguageSpecificStoredProceureExtendedOperationHandler
{
    public void handleStoredProcedureExtendedOperation( LdapContext ctx, StoredProcedure pojo ) throws ClassNotFoundException, NamingException
    {
        List types = new ArrayList( pojo.getParameters().size() );
        List values = new ArrayList( pojo.getParameters().size() );
        
        Iterator it = pojo.getParameters().iterator();
        while ( it.hasNext() )
        {
            StoredProcedureParameter pPojo = ( StoredProcedureParameter ) it.next();
            
            // Get type from String even if it holds a primitive type name
            Class type = SpringClassUtils.forName( StringTools.utf8ToString( pPojo.getType() ) ); 
            
            types.add( type );
            
            byte[] value = pPojo.getValue();
            
            /**
             * If the type name refers to a Java primitive then
             * we know that the value is encoded as its String representation and
             * we retrieve the String and initialize a wrapper of the primitive.
             * 
             * Note that this is just how we prefer Java Specific Stored Procedures
             * to handle parameters. Of course we do not have to it this way.
             */
            if ( type.isPrimitive() )
            {
                values.add( getInitializedPrimitiveWrapperInstance( type, value ) );
            }
            /**
             * If the type is a complex Java type then
             * just deserialize the object.
             */
            else
            {
                try
                {
                    // TODO Irritating syntax! Just wanted to see how it looks like..
                    values.add
                    ( 
                        (
                            new ObjectInputStream
                            ( 
                                new ByteArrayInputStream( value )
                            ) 
                        ).readObject()
                    );
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }
        
        // TODO determine what to do with the exception
        /**
         * BIG BANG!!!
        ctx.executeProcedure( 
                StringTools.utf8ToString( pojo.getProcedure() ), 
                ( Class[] ) types.toArray(),
                values.toArray()
                );
        */
    }

    private Object getInitializedPrimitiveWrapperInstance( Class type, byte[] value )
    {
        Object instance = null;
        try
        {
            instance = ClassUtils
                    .primitiveToWrapper( type )
                    .getConstructor( new Class[] {String.class} )
                    .newInstance( new Object[] { StringTools.utf8ToString( value ) } );
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return instance;
    }
}

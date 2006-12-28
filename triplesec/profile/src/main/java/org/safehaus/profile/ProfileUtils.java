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
package org.safehaus.profile;


import java.io.*;


/**
 * Utility functions dealing with account Profiles.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ProfileUtils
{


    /**
     * Generates the serialized representation of a Profile.
     *
     * @param profile the Profile to serialize using the record format
     * @return the serialized Profile
     */
    public static byte[] serialize( Profile profile ) throws IOException
    {
        ByteArrayOutputStream arrayOut = null;

        DataOutputStream dataOut = null;

        try
        {
            arrayOut = new ByteArrayOutputStream();

            dataOut = new DataOutputStream( arrayOut );

            /*
             * We write the members in the following order:
             *
             * 1). the label value
             * 2). moving factor
             * 3). additional account information if any at all
             * 4). the shared secret
             */

            dataOut.writeUTF( profile.getLabel() );

            dataOut.writeLong( profile.getFactor() );

            if ( profile.getInfo() == null )
            {
                dataOut.writeUTF( "" );
            }
            else
            {
                dataOut.writeUTF( profile.getInfo() );
            }

            dataOut.write( profile.getSecret() );

            dataOut.flush();
        }
        finally
        {
            if ( dataOut != null )
            {
                dataOut.close();
            }

            if ( arrayOut != null )
            {
                arrayOut.close();
            }
        }

        return arrayOut.toByteArray();
    }


    /**
     * Creates a Profile by resusitating a serialized profile from a record
     * format.
     *
     * @param rec the serialized Profile record
     * @throws IOException if there are problems resusitating the fields
     */
    public static final Profile create( byte[] rec ) throws IOException
    {
        BaseProfileModifier modifier = new BaseProfileModifier();

        ByteArrayInputStream arrayIn = null;

        DataInputStream dataIn = null;

        try
        {
            arrayIn = new ByteArrayInputStream( rec );

            dataIn = new DataInputStream( arrayIn );

            /*
             * We read the members in the following order which is in the same
             * order we write them:
             *
             * 1). the label value
             * 2). moving factor
             * 3). additional account information if any at all
             * 4). the shared secret
             */

            modifier.setLabel( dataIn.readUTF() );

            modifier.setFactor( dataIn.readLong() );

            modifier.setInfo( dataIn.readUTF() );

            byte[] buf = new byte[100];
            int ammount = dataIn.read( buf );
            byte[] resized = new byte[ammount];
            System.arraycopy( buf, 0, resized, 0, ammount );
            modifier.setSecret( resized );
        }
        finally
        {
            if ( arrayIn != null )
            {
                arrayIn.close();
            }

            if ( dataIn != null )
            {
                dataIn.close();
            }
        }

        return modifier.getProfile();
    }


    /**
     * Gets the label of a Profile from the raw record without creating a
     * Profile object.  This is a very efficient method to use while filtering
     * trying to match for specific Profiles by label.
     *
     * @param rec the raw serialized Profile
     * @return the Profile record's label field
     * @throws java.io.IOException if there is a problem accessing the serialized data
     */
    public static final String getLabel( byte[] rec ) throws IOException
    {
        ByteArrayInputStream arrayIn = null;

        DataInputStream dataIn = null;

        String label = null;


        try
        {
            arrayIn = new ByteArrayInputStream( rec );

            dataIn = new DataInputStream( arrayIn );

            label = dataIn.readUTF();
        }
        finally
        {
            if ( dataIn != null )
            {
                dataIn.close();
            }

            if ( arrayIn != null )
            {
                arrayIn.close();
            }
        }                  

        return label;
    }
}

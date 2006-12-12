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


/**
 * Some pre-fab test profiles for use in various tests, demos and applications.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ProfileTestData
{
    /** bogus test account profile */
    private static final ServerProfile BANK_ONE = new BaseServerProfile( "bankone",
            "EXAMPLE.COM", "BankOne",
            18237017371834L, new byte[] { 'a','s','d','f',
                                          'a','d','s','f',
                                          'a','d','f','a',
                                          'd','f','a','f',
                                          's','f','d','f'}, "1234", 
                                          new byte[] { 's', 'e', 'c', 'r', 'e', 't' }
    );

    /** bogus test account profile */
    private static final ServerProfile CITI_401K = new BaseServerProfile( "citi401k",
            "EXAMPLE.COM", "Citi401k",
            27934524L, new byte[] { 'x','a','x','1',
                                    'a','x','s','d',
                                    'f','g','c','f',
                                    'g','4','a','3',
                                    'f','f','y','*'}, "1234", 
                                    new byte[] { 's', 'e', 'c', 'r', 'e', 't' }
    );

    /** bogus test account profile */
    private static final ServerProfile APACHE = new BaseServerProfile( "apache",
            "EXAMPLE.COM", "Apache",
            513417813624832L, new byte[] { 'S','s','5','(',
                                           '.','d','-','s',
                                           'K','z','f','s',
                                           'd','z','d','a',
                                           's','z','?','f'}, "1234", 
                                           new byte[] { 's', 'e', 'c', 'r', 'e', 't' }
    );

    /** bogus test account profile */
    private static final ServerProfile CODEHAUS = new BaseServerProfile( "codehaus",
            "EXAMPLE.COM", "Codehaus",
            123984713378815745L, new byte[] { '5','x','g','>',
                                              'a','v','s','.',
                                              'x','Q','4','a',
                                              'd','z',',','m',
                                              'z','$','=','%'}, "1234", 
                                              new byte[] { 's', 'e', 'c', 'r', 'e', 't' }
    );

    /** bogus test account profile */
    private static final ServerProfile OFFICE = new BaseServerProfile( "officew2k",
            "EXAMPLE.COM", "OfficeW2K",
            999372763L, new byte[] { 'g','$','7','x',
                                     'a','c','s','j',
                                     'a','m','f','O',
                                     'd','@','a','(',
                                     's','-','d','.'}, "1234", 
                                     new byte[] { 's', 'e', 'c', 'r', 'e', 't' }
    );

    /** bogus test account profile */
    public static final ServerProfile[] PROFILES = new ServerProfile[]
    {
        BANK_ONE, CITI_401K, APACHE, CODEHAUS, OFFICE
    };
}

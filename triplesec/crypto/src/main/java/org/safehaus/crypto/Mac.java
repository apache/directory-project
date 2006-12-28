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
package org.safehaus.crypto;


/**
 * The base interface for implementations of message authentication codes (MACs).
 */
public interface Mac
{
    /**
     * Initialise the MAC.
     *
     * @param params the key and other data required by the MAC.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(CipherParameters params)
        throws IllegalArgumentException;

    /**
     * Return the name of the algorithm the MAC implements.
     *
     * @return the name of the algorithm the MAC implements.
     */
    public String getAlgorithmName();

    /**
     * Return the block size for this cipher (in bytes).
     *
     * @return the block size for this cipher in bytes.
     */
    public int getMacSize();

    /**
     * add a single byte to the mac for processing.
     *
     * @param in the byte to be processed.
     * @exception IllegalStateException if the MAC is not initialised.
     */
    public void update(byte in)
        throws IllegalStateException;

    /**
     * @param in the array containing the input.
     * @param inOff the index in the array the data begins at.
     * @param len the length of the input starting at inOff.
     * @exception IllegalStateException if the MAC is not initialised.
     * @exception DataLengthException if there isn't enough data in in.
     */
    public void update(byte[] in, int inOff, int len)
        throws DataLengthException, IllegalStateException;

    /**
     * Compute the final statge of the MAC writing the output to the out
     * parameter.
     * <p>
     * doFinal leaves the MAC in the same state it was after the last init.
     *
     * @param out the array the MAC is to be output to.
     * @param outOff the offset into the out buffer the output is to start at.
     * @exception org.cainenable.softcard.crypto.DataLengthException if there isn't enough space in out.
     * @exception IllegalStateException if the MAC is not initialised.
     */
    public int doFinal(byte[] out, int outOff)
        throws DataLengthException, IllegalStateException;

    /**
     * Reset the MAC. At the end of resetting the MAC should be in the
     * in the same state it was after the last init (if there was one).
     */
    public void reset();
}

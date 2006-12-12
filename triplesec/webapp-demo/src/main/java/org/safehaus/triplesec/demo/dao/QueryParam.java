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
package org.safehaus.triplesec.demo.dao;

/**
 * Encapsulates the query parameters to be passed to the daos.
 */
public class QueryParam
{
    private int first;
    private int count;
    private String sort;
    private boolean sortAsc;

    /**
     * Set to return <tt>count</tt> elements, starting at the
     * <tt>first</tt> element.
     *
     * @param first first element to return
     * @param count number of elements to return
     */
    public QueryParam( int first, int count )
    {
        this( first, count, null, true );
    }

    /**
     * Set to return <tt>count</tt> sorted elements, starting at the
     * <tt>first</tt> element.
     *
     * @param first first element to return
     * @param count number of elements to return
     * @param sort column to sort on
     * @param sortAsc sort ascending or descending
     */
    public QueryParam( int first, int count, String sort, boolean sortAsc )
    {
        this.first = first;
        this.count = count;
        this.sort = sort;
        this.sortAsc = sortAsc;
    }

    public int getFirst()
    {
        return first;
    }

    public int getCount()
    {
        return count;
    }

    public String getSort()
    {
        return sort;
    }

    public boolean isSortAsc()
    {
        return sortAsc;
    }

    public boolean hasSort()
    {
        return sort != null;
    }
}

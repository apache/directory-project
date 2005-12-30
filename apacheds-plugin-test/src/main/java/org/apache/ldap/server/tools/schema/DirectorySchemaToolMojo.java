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
package org.apache.ldap.server.tools.schema;


import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * Maven 2 plugin mojo wrapper for directory plugin.
 * 
 * @goal sayhi
 * @description Says "Hi" to the user
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DirectorySchemaToolMojo extends AbstractMojo
{
    /**
     * The directory containing the OpenLDAP schema files.
     */
    private File schemaSourcesDir;
    

    public void execute() throws MojoExecutionException 
    {
        try
        {
            getLog().info( "schemaSourcesDir = " + schemaSourcesDir.getCanonicalPath() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}

#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
#   https://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License. 

# This script finds all modules by looking at pom.xmls starting from
# where it's invoked and generates an index page with a table of project
# names and descriptions. The generated document is a complete xdoc doc.
# If there is no name element in project then it takes the artifactId.
# If there is no description element in the project then it prints
# "To be described...".

modules=$(pommodules.sh ./pom.xml)

project_name=$(pomname.sh ./pom.xml)
if [ "$project_name" == "" ]
then
	project_name=$(pomartifactId.sh ./pom.xml)
fi

echo '<?xml version="1.0" encoding="UTF-8"?>'
echo '<document>'
echo ' <properties>'
echo '  <title>'${project_name}' Modules</title>'
echo ' </properties>'
echo ' <body>'
echo '  <section name="'${project_name}' Modules">'
echo '   <p>'${project_name}' is composed of several modules. Here is the list of them with brief descriptions:</p>'

echo '   <table>'
echo '    <tr><th>Name</th><th>Description</th></tr>'

for module in $modules
do
	pom=$module/pom.xml
	
	project_name=$(pomname.sh $pom)
	
	project_artifactId=$(pomartifactId.sh $pom)
	
	if [ "$project_name" == "" ]
	then
		project_name=$project_artifactId
	fi
	
	project_description=$(pomdesc.sh $pom)
	if [ "$project_description" == "" ]
        then
                project_description="To be described..."
        fi

	echo "    <tr> <td>${project_name}</td> <td>${project_description}</td> </tr>"
done

echo '   </table>'
echo '  </section>'
echo ' </body>'
echo '</document>'

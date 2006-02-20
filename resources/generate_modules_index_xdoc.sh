#!/bin/sh

# One of the ugliest hacks you can find around!
#
# This script finds all subprojects by looking at pom.xmls starting from
# where it's invoked and generates an index page with a table of project
# names and descriptions. The generated document is a complete xdoc doc.
# If there is no name element in project then it takes the artifactId.
# If there is no description element in the project then it prints
# "To be described...".

poms=$(for pom in $(find . -name "pom.xml" | sed "s/^\.\/pom.xml$//"); do echo "${pom}"; done)

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

for pom in $poms
do
	#project_dir=$(echo $pom | sed 's/\.\/\(.*\)\/pom\.xml/\1/')
	
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

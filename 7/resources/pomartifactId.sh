#!/bin/sh

# This script prints the artifactId of a project looking
# at the pom whose name given as a parameter.

if [ $# -ne 1 ]
then
	echo -e "Error!\tUsage: artifactId.sh <pom_xml_file>"
	exit 1
fi

xmlstarlet sel -t -m "/project/artifactId" -v "text()" -n $1 | grep -v '^[ tab]*$'

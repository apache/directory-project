#!/bin/sh

# This script prints the groupId of a project looking
# at the pom whose name given as a parameter.

if [ $# -ne 1 ]
then
	echo -e "Error!\tUsage: groupId.sh <pom_xml_file>"
	exit 1
fi

xmlstarlet sel -t -m "/project/groupId" -v "text()" -n $1 | grep -v '^[ tab]*$'

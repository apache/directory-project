#!/bin/sh

# This script lists the modules of a project looking
# at the pom whose name given as a parameter.

if [ $# -ne 1 ]
then
	echo -e "Error!\tUsage: modules.sh <pom_xml_file>"
	exit 1
fi

# xml_grep "/project/modules/module" $1  | grep '<module>.*</module>' | sed 's/.*>\(.*\)<.*/\1/g' | sort

xmlstarlet sel -t -m "//modules/module" -v "text()"  -n $1 | grep -v '^$' | sort -u

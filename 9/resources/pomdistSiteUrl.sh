#!/bin/sh

# This script prints the distribution site url of a project
# looking at the pom whose name given as a parameter.

if [ $# -ne 1 ]
then
	echo -e "Error!\tUsage: distSiteUrl.sh <pom_xml_file>"
	exit 1
fi

xmlstarlet sel -t -m "/project/distributionManagement/site/url" -v "text()" -n $1 | grep -v '^[ tab]*$'

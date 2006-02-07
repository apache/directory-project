#!/bin/sh

# This script lists the modules of a project looking
# at the pom whose name given as a parameter.

xml_grep "/project/modules/module" $1  | grep '<module>.*</module>' | sed 's/.*>\(.*\)<.*/\1/g'

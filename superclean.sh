#!/bin/sh

# This script is supposed to be run from 'directory/trunks'
# with the command 'sh-scripts/superclean.sh'

find . -name target | xargs rm -rf
rm -rf ~/.m2/repository/org/apache/directory/


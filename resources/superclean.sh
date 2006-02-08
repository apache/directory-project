#!/bin/sh

# This script is supposed to be run from 'directory/trunks'
# with the command 'resources/superclean.sh'

find . -type d -name target | xargs rm -rf

if [ `uname` == "CYGWIN_NT-5.1" ]; then
  rm -rf $(cygpath "$HOME_PATH"/.m2/repository/org/apache/directory);
else
  rm -rf ~/.m2/repository/org/apache/directory/
fi


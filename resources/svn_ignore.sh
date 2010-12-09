#!/bin/bash

PROPFILE=`echo $0 | sed -e 's/svn_ignore.sh/svn_ignore.txt/'`

for dir in `find . -type d`; do
  if [[ -f $dir/pom.xml && -d $dir/.svn ]]; then
    svn propset svn:ignore -F $PROPFILE $dir 
  fi;
done;


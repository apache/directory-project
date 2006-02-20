#!/bin/sh

# This script is supposed to be run from 'directory/trunks'
# with the command 'resources/codesize.sh'

echo Total lines of Java code under $PWD is $(find . -type f -regex '.*\.java' | xargs cat | wc -l).

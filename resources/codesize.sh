#!/bin/sh

# This script is supposed to be run from 'directory/trunks'
# with the command 'sh-scripts/codesize.sh'

echo Total lines of Java code is $(find .. -type f -regex '.*\.java' | xargs cat | wc -l).

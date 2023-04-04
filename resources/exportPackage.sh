#!/bin/sh
grep -r "^package " src/main | sed 's/^.*java:package //' | sort -u 


#!/bin/sh

echo Total lines of Java code is $(find . -type f -regex '.*\.java' | xargs cat | wc -l).

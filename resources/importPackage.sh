#!/bin/sh

grep -r "^import" src/main | sed 's/.*java:import //' | sed 's/\.[A-Z][a-zA-Z]*;$//' | sort -u | grep -v "^java\..*$"

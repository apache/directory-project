#!/bin/sh

maven -o war:inplace
cp -rf src/webapp/* .deployables/triplesec-webapp/


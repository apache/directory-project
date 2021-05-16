#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

#
# This script list all the import used in test, main and antlr generated code
#
# It produces 3 files :
#   resMain.txt
#   resAntlr.txt
#   resTest.txt
#
grep -r "import " src/main/java | grep -v .svn | sed -e "s/^.*import //" | sed -e "s/\.[A-Z][a-zA-Z0-9_]*;$//" | sed -e "s/\.[A-Z][a-zA-Z0-9_]*$//" | sed -e "/^java.*/d" | sort -u > resMain.txt
grep -r "import " target/generated-sources/antlr/ | grep -v .svn | sed -e "s/^.*import //" | sed -e "s/\.[A-Z][a-zA-Z0-9_]*;$//" | sed -e "s/\.[A-Z][a-zA-Z0-9_]*$//" | sed -e "/^java.*/d" | sort -u > resAntlr.txt
grep -r "import " src/test/java | grep -v .svn | sed -e "s/^.*import //" | sed -e "s/\.[A-Z][a-zA-Z0-9_]*;$//" | sed -e "s/\.[A-Z][a-zA-Z0-9_]*$//" | sed -e "/^java.*/d" | sort -u > resTest.txt

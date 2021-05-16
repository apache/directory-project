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


# This script lists the modules of a project looking
# at the pom whose name given as a parameter.

if [ $# -ne 1 ]
then
	echo -e "Error!\tUsage: modules.sh <pom_xml_file>"
	exit 1
fi

# xml_grep "/project/modules/module" $1  | grep '<module>.*</module>' | sed 's/.*>\(.*\)<.*/\1/g' | sort

xmlstarlet sel -t -m "//modules/module" -v "text()"  -n $1 | grep -v '^$' | sort -u

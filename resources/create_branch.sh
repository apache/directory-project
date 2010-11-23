#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License. 


# This script creates a new branch for the ApacheDS project. 
# It requires the following parameters:
#
#     create_branch.sh branchName comment
#

BRANCH_NAME=$1
COMMENT=$2

if [ $# -ne 2 ]; then
    echo "$0 : Provide two arguments: branchName and a quoted comment."
    exit 1
fi

APACHEDS_SVN=https://svn.apache.org/repos/asf/directory/apacheds
SHARED_SVN=https://svn.apache.org/repos/asf/directory/shared
PROJECT_SVN=https://svn.apache.org/repos/asf/directory/project
KERBEROS_SVN=https://svn.apache.org/repos/asf/directory/clients/kerberos
MANUALS_SVN=https://svn.apache.org/repos/asf/directory/apacheds-manuals

CHECKSTYLE_SVN=https://svn.apache.org/repos/asf/directory/buildtools/trunk/checkstyle-configuration
JUNIT_SVN=https://svn.apache.org/repos/asf/directory/buildtools/trunk/junit-addons

svn cp -m $COMMENT $APACHEDS_SVN/trunk $APACHEDS_SVN/branches/$BRANCH_NAME
svn cp -m $COMMENT $SHARED_SVN/trunk $SHARED_SVN/branches/$BRANCH_NAME
svn cp -m $COMMENT $PROJECT_SVN/trunk $PROJECT_SVN/branches/$BRANCH_NAME
svn cp -m $COMMENT $KERBEROS_SVN/trunk $KERBEROS_SVN/branches/$BRANCH_NAME
svn cp -m $COMMENT $MANUELS_SVN/trunk $MANUELS_SVN/branches/$BRANCH_NAME

svn mkdir -m $COMMENT $APACHEDS_SVN/branches/$BRANCH_NAME-with-dependencies

# Now we create the svn:externals property value file
echo apacheds  $APACHEDS_SVN/branches/$BRANCH_NAME            > VALFILE
echo shared    $SHARED_SVN/branches/$BRANCH_NAME              >> VALFILE
echo project   $PROJECT_SVN/branches/$BRANCH_NAME             >> VALFILE
echo kerberos-client $KERBEROS_SVN/branches/$BRANCH_NAME      >> VALFILE
echo apacheds-manuals $MANUELS_SVN/branches/$BRANCH_NAME      >> VALFILE
echo checkstyle-configuration $CHECKSTYLE_SVN                 >> VALFILE
echo junit-addons $JUNIT_SVN                                  >> VALFILE

svn propset svn:externals -F ./VALFILE $APACHEDS_SVN/branches/$BRANCH_NAME-with-dependencies 

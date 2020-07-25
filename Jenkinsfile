/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
pipeline {
  agent none
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
    timeout(time: 8, unit: 'HOURS')
  }
  triggers {
    cron('@weekly')
    pollSCM('@daily')
  }
  stages {
    stage ('Build and Deploy') {
      options {
        timeout(time: 2, unit: 'HOURS')
        retry(2)
      }
      agent {
        label 'ubuntu'
      }
      // https://cwiki.apache.org/confluence/display/INFRA/JDK+Installation+Matrix
      // https://cwiki.apache.org/confluence/display/INFRA/Maven+Installation+Matrix
      steps {
        sh '''
        export JAVA_HOME=/home/jenkins/tools/java/latest1.8
        export MAVEN_OPTS="-Xmx512m"
        /home/jenkins/tools/maven/latest3/bin/mvn -V clean deploy
        '''
      }
      post {
        always {
          deleteDir()
        }
      }
    }
  }
  post {
    failure {
      mail to: 'notifications@directory.apache.org',
      subject: "Jenkins pipeline failed: ${currentBuild.fullDisplayName}",
      body: "Jenkins build URL: ${env.BUILD_URL}"
    }
    fixed {
      mail to: 'notifications@directory.apache.org',
      subject: "Jenkins pipeline fixed: ${currentBuild.fullDisplayName}",
      body: "Jenkins build URL: ${env.BUILD_URL}"
    }
  }
}


#!/bin/sh
# Really simple script to launch triplesec tools with RPM installer

TRIPLESEC_HOME=/usr/local/${app}-${app.version}
$JAVA_HOME/bin/java -jar $TRIPLESEC_HOME/bin/triplesec-tools.jar $@

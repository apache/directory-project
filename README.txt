                  W O R K   I N   P R O G R E S S
                  ===============================

This is the trunk for all directory projects.  

Building
--------

 o Maven 2.0.1 is used for the build at this point in time.
 o To build subprojects like mina just cd into 
   <subproject-prefix>-build and do a mvn package or mvn install.
   So to build all of mina just cd into mina-build and issue any
   one of these commands.
 o To build all issue a mvn package or mvn install inside trunk.
 o To generate eclipse or idea descriptors for entire trunk just
   issue a mvn eclipse:eclipse or a mvn idea:idea respectively in
   the trunk.  For subproject IDE descriptor generation cd into
   <subproject-prefix>-build and issue one of these commands.
 o If a project does not have subprojects list asn1 then just 
   cd into it and issue mvn package or mvn install.

Conventions
-----------

 o Project structure is flat with master parent pom and group poms.
 o If a sub project is not prefixed with a <prefix>-foo then it is the
   core artifact rather than an auxillary package.  For example mina is
   the core project for the mina core artifact.  Other supporting 
   projects for mina are mina-spring and mina-ssl for example all of
   which are mina- prefixed.
 o For each group id a <prefix>-build directory contains the POM for
   building only the subset of projects within that POM.  An example
   of this is mina-build.  To build and install it just cd into this
   directory and issue a mvn install.

Branches
--------

 o Take a look at the README.txt in the branches folder under the
   directory svn repo base.

Releases
--------

 o Tagged releases are contained under the releases folder under
   directory svn repo base.  Take a look at the README.txt file
   there for more info.

This is the trunk for all directory projects.  

Building
--------

 o Maven 2.0.1 is used for the build at this point in time.

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

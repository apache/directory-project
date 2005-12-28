This is the trunk for all directory projects.  

Building
--------

 o Maven 2.0.1 is used for the build at this point in time.

Conventions
-----------

 o Project structure is flat with master parent pom.
 o If a sub project is not prefixed with a <prefix>-foo then it is the
   core artifact rather than an auxillary package.  For example mina is
   the core project for the mina core artifact.  Other supporting 
   projects for mina are mina-spring and mina-ssl for example.


Apache Directory Project
========================

This repository contains the Maven parent POM and some shared resources of 
the [Apache Directory project](https://directory.apache.org/).


Release Process
---------------

(See also <https://cwiki.apache.org/confluence/display/DIRxDEV/Top+Level+Pom+Management+Policy>)

### Prepare the POM

    mvn release:prepare -DdryRun=true

### Deploy a snapshot

    mvn deploy

This is useful to verify your settings in ~/.m2/settings.xml (Nexus password and GPG key)

### Prepare the release

    mvn release:clean
    mvn release:prepare

When asked for the SCM release tag please remove the project prefix, the version number is enough.

This creates a tag here: <https://gitbox.apache.org/repos/asf?p=directory-project.git>

### Stage the release

    mvn release:perform

This deploys the POM to a staging repository. Go to <https://repository.apache.org/index.html#stagingRepositories> and close the staging repository.

### Inform dev list

Inform the dev list to publish (release) this POM. After a 4 hour grace period the POM can be released, therefore go to <https://repository.apache.org/index.html#stagingRepositories> and release the staging repository.


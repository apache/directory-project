
   Example: Interceptor 
 Objective: Demonstrate ApacheDS embedded configuration with LDAP service,
            extra partition, and a custom interceptor for a changelog/audit
            trail. 
  To Build: mvn compile
    To Run: mvn test
   Summary: The purpose of this demolet is to show how custom interceptors 
            can be used in apacheds by building a custom interceptor for 
            keeping a change log.
      Tips: This is a cumulative example and requires all the other examples
            except for the kerberos example.  Take a look inside server-work
            to see the changes.log file for this interceptor.  Tail -f the
            file and start changing the directory.


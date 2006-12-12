                        Triplesec Strong Identity Server 
                        ================================

Documentation
-------------

Documentation in the form of a User's Guide and tutorials are available here:

     http://triplesec.safehaus.org


Running
-------

The server is designed to run as a Windows Service or as a UNIX Daemon (also
on MacOSX).  

You can start, and stop the daemon on UNIX using the /etc/init.d/triplesec 
script if you chose to install it via the installer.  Otherwise you can use
another script in the installation home under the bin directory called
server.init.  

Both these scripts take a single argument of either start, stop or debug.
The debug command starts the server without the daemon.  It can be
used to attach to the server using a debugger and to dump output to the
console.  Only in debug mode can the diagnostic screens be launched.  In
daemon mode the proper DISPLAY parameter must be set to launch the diagnostics
on startup.

On windows the server can be started like any other service using the services
console via Microsoft Management Console.  It can also be started, stopped and
configured using the procrun service manager installed for it: see
 
        Start->All Programs->Triplesec->Service Setttings

A tray icon can also be launched to monitor it and to control the service: see
 
        Start->All Programs->Triplesec->Tray Monitor

On Windows the server can also be started in a special test mode where it 
dumps output to the command line in a cmd window rather than to the log files.  
You can launch the server in this mode by selecting 

        Start->All Programs->Triplesec->Test Service

The server can also be started in test mode by running the triplesec.exe 
executable from the commandline.  Likewise the service manager can be started 
from the command line by invoking apachedsw.exe.


Tool Support
------------

Two tool come with Triplesec.  A commandline client tool and GUI based
administration tool called Triplesec Admin Tool.  Both these tools can be
found withing the bind directory.  Both are executable jar files.  So you
can execute them like so:

  java -jar triplesec-tools.jar help 
  java -jar triplesec-admin.jar /path/to/installation

The command line tool, triplesec-tools.jar, can be run to perform maintenance
operation on the server.  Here's what the 'help' command lists for the tool:

   help             displays help message 
   notifications    listens to the server for disconnect msgs
   dump             dumps partitions in LDIF format for recovery and backup
   graceful         starts graceful shutdown with shutdown delay & timeoffline
   diagnostic       launches diagnostic UI for inspecting server partitions
                    and client sessions

The Triplesec Administration Tool is a Swing application which allows you to 
manage your realm.  With it you can add, remove and modify, applications,
roles, users, groups, and permissions.  For more information about the admin
tool you can consult it's User's Guide here:

   http://docs.safehaus.org/display/TRIPLESEC/Administration+Tool+User's+Guide

Thanks and enjoy,
Triplesec Development Team


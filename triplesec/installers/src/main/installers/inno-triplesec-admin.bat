@REM --------------------------------------------------------------------------
@REM Triplesec Admin Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM TSEC_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM TSEC_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM TSEC_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug the admin itself, use
@REM set TSEC_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM ----------------------------------------------------------------------------

%JAVA_HOME%\bin\java.exe -jar %1 %2


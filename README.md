# What is this?

This is a JDBC backend for the
[ADONotebook](https://github.com/adamnew123456/adonotebook) protocol, see that
project's page to learn more. This document only covers how to build and run
the JDBC server.

# Building

Run `mvn compile assembly:single` to download all the necessary dependencies,
compile, and produce a JAR file in the target directory.

# Running

Navigate to the target directory and run 
`java -cp "server-xxx-SNAPSHOT-jar-with-dependencies.jar;..." org.adamnew123456.JDBCNotebook.App -j <class-name> <connection-string>`.

The `<class-name>` should be the JDBC driver class of a driver that was included
in your classpath.

#!/usr/bin/env bash
mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar
mvn install:install-file -Dfile=numberfield7-0.0.6.jar -DgroupId=tm.kod -DartifactId=numberfield7 -Dversion=0.0.6 -Dpackaging=jar
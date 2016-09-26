#!/bin/sh
# Generate printaddon jars and sign the applet
# keystore and key password is: passwd
#cp -rf classes/* bin/
#src/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet_pol.jar
JAR_NAME=printapplet-0.0.5.jar
JAR_LOC=src/main/resources/dk/apaq/vaadin/addon/printservice/gwt/public
FIN_JAR_LOC=../../$JAR_LOC/$JAR_NAME
cd target/classes/

mkdir -p $JAR_LOC
jar cf $FIN_JAR_LOC org/vaadin/applet/* org/vaadin/applet/client/ui/* dk/apaq/vaadin/addon/printapplet/* org/vaadin/applet/sample/* org/json/* org/sanjose/textprinter/*
jar ufm $FIN_JAR_LOC ../../APP_MANIFEST.MF
cd ../../
jarsigner -keystore ./.keystore $JAR_LOC/$JAR_NAME Pol
cp $JAR_LOC/$JAR_NAME ../vasja-ui/src/main/webapp/VAADIN/widgetsets/org.sanjose.MyAppWidgetset/

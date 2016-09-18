#!/bin/sh

# Generate printaddon jars and sign the applet
# keystore and key password is: passwd
cp -rf classes/* bin/
#src/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet_pol.jar
cd target/classes/
#jar cfm ../src/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet_pol.jar ../AMANIFEST.MF org/vaadin/applet/* org/vaadin/applet/client/ui/* dk/apaq/vaadin/addon/printapplet/* org/vaadin/applet/sample/* org/json/*
jar cf ../../src/main/resources/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet-0.0.5.jar org/vaadin/applet/* org/vaadin/applet/client/ui/* dk/apaq/vaadin/addon/printapplet/* org/vaadin/applet/sample/* org/json/*

jar ufm ../../src/main/resources/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet-0.0.5.jar ../../APP_MANIFEST.MF
cd ../../
jarsigner -keystore ./.keystore src/main/resources/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet-0.0.5.jar Pol
cp src/main/resources/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet-0.0.5.jar ../vasja-ui/src/main/webapp/VAADIN/widgetsets/org.sanjose.MyAppWidgetset/

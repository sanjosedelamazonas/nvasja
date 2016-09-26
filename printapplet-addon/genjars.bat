REM Generate printaddon jars and sign the applet
REM keystore and key password is: passwd
REM src/dk/apaq/vaadin/addon/printservice/gwt/public/printapplet_pol.jar

set JAR_NAME=printapplet-0.0.5.jar
set JAR_LOC=src\main\resources\dk\apaq\vaadin\addon\printservice\gwt\public\
set FIN_JAR_LOC=..\..\%JAR_LOC%%JAR_NAME%

md src\main\resources\dk\apaq\vaadin\addon\printservice\gwt
md %JAR_LOC%

cd target\classes\

jar cf %FIN_JAR_LOC% org/vaadin/applet/* org/vaadin/applet/client/ui/* dk/apaq/vaadin/addon/printapplet/* org/vaadin/applet/sample/* org/json/* org/sanjose/textprinter/*
jar ufm %FIN_JAR_LOC% ..\..\APP_MANIFEST.MF
cd ..\..\
jarsigner -keystore .keystore %JAR_LOC%%JAR_NAME% Pol
copy %JAR_LOC%\%JAR_NAME% ..\vasja-ui\src\main\webapp\VAADIN\widgetsets\org.sanjose.MyAppWidgetset\

Vicariato Apostolico San Jose del Amazonas
==============
Cashflow Management System


Project Structure
=================

The project consists of the following modules:

- parent project: common metadata and configuration
- vasja-widgetset: widgetset, custom client side code and dependencies to widget add-ons
- vasja-ui: main application module, development time
- vasja-production: module that produces a production mode WAR for deployment
- vasja-fonts: font package for Jasper Reports
- printapplet-addon: Java applet that enables printing reports at the client side

The production mode module recompiles the widgetset (obfuscated, not draft), activates production mode for Vaadin with a context parameter in web.xml and contains a precompiled theme. The ui module WAR contains an unobfuscated widgetset, and is meant to be used at development time only.

Workflow
========

Other basic workflow steps:
- intellijIdea - git pull

- compiling the whole project
  - install jars not available in public maven repository:
    - cd jars
    - "install_jars.bat" or sh "install_jars.sh" respectively
  - run "mvn install" in parent project
- developing the application
  - edit code in the ui module
  - compile the vasja-ui module and start the application in debug mode:
    - cd vasja-ui
    - mvn spring-boot:run
  - open http://localhost:8080/
- client side changes or add-ons
  - edit code/POM in widgetset module
  - run "mvn install" in widgetset module
- editing printapplet-addon module
  - when making changes to the server part it is enough to run "mvn install"
  - if you modify the applet part you need to recompile and sign the jar. The exact steps are described in printapplet-addon/README.txt

PRODUCTION MODE:
- mvn -Pproduction clean install
Output will be:
vasja-ui/target/vasja-ui-1.0-SNAPSHOT.jar
- start it in production mode:
java -jar -Dspring.profiles.active=production vasja-ui/target/vasja-ui-1.0-SNAPSHOT.jar

JAVA Security problem connecting to SQL Server:
java -jar -Djava.security.properties="%BASE%\java.security" -Dspring.profiles.active=production vasja-ui/target/vasja-ui-1.0-SNAPSHOT.jar

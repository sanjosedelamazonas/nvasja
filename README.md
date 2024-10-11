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
  - open http://localhost:8000/
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

DEBUG MODE:
cd vasja-ui
mvn -Pdebug spring-boot:run
Open Intellij and connect using the displayed port number


MS SQL SERVER on Ubuntu 22.04:

Thanks! This was very helpful and solved my problem. For anyone looking for explicit commands on an Ubuntu 22.04 installation,
$ cd /opt/mssql/lib
$ ls -la
$ sudo rm libcrypto.so libssl.so
$ sudo ln -s /usr/lib/x86_64-linux-gnu/libcrypto.so.1.1 libcrypto.so
$ sudo ln -s /usr/lib/x86_64-linux-gnu/libssl.so.1.1 libssl.1.1

If you're missing version 1.1 of these files, you can first do
$ sudo apt install libssl1.1

Restore database on Ubuntu:

docker cp BACKUP_SCP_FULL_10042023_024559.BAK vasjasql:/var/opt/mssql/data
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P Vasja123456 -No -Q "RESTORE DATABASE [SCP] FROM DISK = N'/var/opt/mssql/data/bkp-2024-10-10.bak' WITH FILE = 1, NOUNLOAD, REPLACE, RECOVERY, STATS = 5, MOVE 'SCP_VASJA' TO '/var/opt/mssql/data/SCP_VASJA', MOVE 'SCP_VASJA_log' TO '/var/opt/mssql/data/SCP_VASJA_log'"

CREATE LOGIN vasja WITH PASSWORD = 'Vasja123456'
GO
CREATE USER vasja FOR LOGIN vasja
GO


exec sp_addrolemember 'db_owner', 'vasja'
GO

MS-SQL Server 2019

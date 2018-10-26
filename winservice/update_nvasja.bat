rem Update and restart NVASJA SERVICE
set INSTALL_DIR=D:\vasjacaja
set PROJ_DIR=d:\IdeaProjects_nvasja
set VASJA_JAR=vasja-ui*.jar
set VASJA_SERVICE=vasjacaja
set INSTALL_DRV=D:

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_191
set MVN_DIR=d:\vasjacaja\apache-maven-3.5.4
set PATH=%JAVA_HOME%\bin;%MVN_DIR%\bin;%PATH%

rem 1. Update Project from git
%INSTALL_DRV%
cd %PROJ_DIR%
git pull
rem 2. Build project
rem mvn clean install
call mvn -Pproduction clean install
rem 3. Stop service and rename old JAR to *_OLD
net stop vasjacaja
if exist %INSTALL_DIR%\%VASJA_JAR%_OLD del /q %INSTALL_DIR%\%VASJA_JAR%_OLD
if exist %INSTALL_DIR%\%VASJA_JAR% ren %INSTALL_DIR%\%VASJA_JAR% *_OLD
rem 4. Move new JAR to INSTALL_DIR
xcopy /s/y %PROJ_DIR%\vasja-ui\target\%VASJA_JAR% %INSTALL_DIR%\
rem 5. copy reports to INSTALL_DIR
xcopy /s/y %PROJ_DIR%\vasja-reports\reports\* %INSTALL_DIR%\reports\
rem 6. Start service
net start vasjacaja

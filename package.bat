@echo off
echo author itning
set ROOT_DIR=%~dp0
set RELEASE_VERSION=1.0.0.RELEASE
set COPY_TARGET_DIR=%ROOT_DIR%release\
set PROJECT_NAMES=smp-admin-server,smp-class,smp-config,smp-eureka,smp-gateway,smp-hystrix-dashboard,smp-info,smp-leave,smp-room,smp-security

echo ---------------------------------------------
echo start bat
echo ---------------------------------------------
for %%i in (%PROJECT_NAMES%) do call:work_func %%i
echo ---------------------------------------------
echo end bat
echo ---------------------------------------------
exit

::--------------------------------------------------------
::-- This function will execute mvn clean and mvn package
::-- command without unit test and it will copy jar to dir.
::--------------------------------------------------------
:work_func
set PROJECT_NAME=%~1
echo Start %PROJECT_NAME% project
cd /d %ROOT_DIR%%PROJECT_NAME%
call mvn clean
call mvn package -DskipTests
copy /y %ROOT_DIR%%PROJECT_NAME%\target\%PROJECT_NAME%-%RELEASE_VERSION%.jar %COPY_TARGET_DIR%
echo End %PROJECT_NAME% project
goto:eof
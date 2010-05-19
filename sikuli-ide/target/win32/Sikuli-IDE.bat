@echo off
PATH=%PATH%;%~d0%~p0tmplib
set JAVA_EXE="java"
REM 64bit Windows users: please uncomment the following line and make sure the path is pointed to your 32bit JRE
REM set JAVA_EXE="C:\Program Files (x86)\Java\jre6\bin\java.exe"
%JAVA_EXE% -Xms64M -Xmx512M -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-script.jar/" -jar %~d0%~p0sikuli-ide.jar %*



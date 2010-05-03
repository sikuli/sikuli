@echo off
PATH=%PATH%;%~d0%~p0tmplib
set JAVA_EXE="java"
REM 64bit Windows users: please uncomment the following line and make sure the path is pointed to your 32bit JRE
REM set JAVA_EXE="C:\Program Files (x86)\Java\jre6\bin\java.exe"
%JAVA_EXE% -Xms256M -Xmx100:2:1200P -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-script.jar/" -cp "%~d0%~p0jintellitype-1.3.2.jar;%~d0%~p0junit-3.8.1.jar;%~d0%~p0sikuli-ide.jar;%~d0%~p0sikuli-script.jar;%~d0%~p0swing-layout-1.0.1.jar" edu.mit.csail.uid.SikuliIDE %*



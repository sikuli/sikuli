@echo off
PATH=%PATH%;%~d0%~p0tmplib
set JAVA_EXE="java"
set JAVA_EXE_X86="C:\Program Files (x86)\Java\jre6\bin\java.exe"
IF EXIST %JAVA_EXE_X86% SET JAVA_EXE=%JAVA_EXE_X86%

%JAVA_EXE% -Xms64M -Xmx512M -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-script.jar/" -jar "%~d0%~p0sikuli-ide.jar" %*



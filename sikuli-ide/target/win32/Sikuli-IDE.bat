@echo off

PATH=%PATH%;%~d0%~p0libs
set JAVA_EXE="java"
if defined PROGRAMFILES(X86) set JAVA_EXE="%PROGRAMFILES(X86)%\Java\jre6\bin\java.exe"
%JAVA_EXE% -Xms64M -Xmx512M -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-script.jar/" -jar "%~d0%~p0sikuli-ide.jar" %*

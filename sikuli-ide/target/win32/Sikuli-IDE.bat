@echo off

PATH=%PATH%;%~d0%~p0tmplib
set JAVA_EXE="javaw"
if defined PROGRAMFILES(X86) set JAVA_EXE="%PROGRAMFILES(X86)%\Java\jre6\bin\javaw.exe"
start /B "Silkuli-IDE Loader...." %JAVA_EXE% -Xms64M -Xmx512M -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-script.jar/" -jar "%~d0%~p0sikuli-ide.jar" %*

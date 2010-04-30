@echo off
PATH=%PATH%;%~d0%~p0tmplib
java -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-script.jar/" -cp "%~d0%~p0jintellitype-1.3.2.jar;%~d0%~p0junit-3.8.1.jar;%~d0%~p0sikuli-ide.jar;%~d0%~p0sikuli-script.jar;%~d0%~p0swing-layout-1.0.1.jar" edu.mit.csail.uid.SikuliIDE %*



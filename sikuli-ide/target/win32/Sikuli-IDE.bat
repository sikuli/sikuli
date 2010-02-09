@echo off
PATH=%PATH%;%~d0%~p0tmplib
java -Dfile.encoding=UTF-8 -Dpython.path="%~d0%~p0sikuli-ide-full.jar\Lib" -cp "%~d0%~p0sikuli-ide-full.jar" edu.mit.csail.uid.SikuliIDE %*

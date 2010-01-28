@echo off
PATH=%PATH%;"%~d0%~p0\tmplib"
java -Dpython.path="%~d0%~p0\sikuli-ide-full.jar\Lib" -cp "%~d0%~p0\sikuli-ide-full.jar" edu.mit.csail.uid.SikuliIDE %*

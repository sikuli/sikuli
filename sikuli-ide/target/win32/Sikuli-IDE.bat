@echo off
PATH=%PATH%;.\tmplib
java -Dpython.path=sikuli-script.jar\Lib -cp junit-3.8.1.jar;sikuli-script.jar;sikuli-ide.jar;jintellitype-1.3.2.jar edu.mit.csail.uid.SikuliIDE %*

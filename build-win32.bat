@ECHO OFF

SET script_build_dir=%~dp0\sikuli-script\build
SET ide_build_dir=%~dp0\sikuli-ide\build
SET tessdata_dir=%~dp0\sikuli-script\target\jartessdata

:: Setup the Visual Studio command prompt tools
CALL "%PROGRAMFILES(X86)%\Microsoft Visual Studio 9.0\VC\bin\vcvars32.bat"

:: Start with a clean build
IF EXIST %script_build_dir% RD /S /Q %script_build_dir%
IF EXIST %ide_build_dir% RD /S /Q %ide_build_dir%
IF EXIST %tessdata_dir% RD /S /Q %tessdata_dir%

:: Create the build directory, generate makefiles, and build
IF NOT EXIST %script_build_dir% MKDIR %script_build_dir%
PUSHD %script_build_dir%
cmake -G "NMake Makefiles" -D CMAKE_BUILD_TYPE=Release ..
nmake
POPD

:: Create the build directory, generate makefiles, and build
IF NOT EXIST %ide_build_dir% MKDIR %ide_build_dir%
PUSHD %ide_build_dir%
cmake -G "NMake Makefiles" -D CMAKE_BUILD_TYPE=Release ..
nmake package
POPD
:: ============================================================================
:: PREREQUISITES
:: ----------------------------------------------------------------------------
:: 1. Visual C++ 9.0
:: 2. JDK 6 x86 (http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u30-download-1377139.html)
:: 3. CMake 2.8+ (http://www.cmake.org/)
:: 4. OpenCV 2.1+ (http://opencv.willowgarage.com/wiki/)
:: 5. Tesseract-OCR 2.04 Source (http://code.google.com/p/tesseract-ocr/)
:: 6. SWIG 1.3+ (http://www.swig.org/)
:: ============================================================================

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
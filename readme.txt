= How To Build Sikuli =

Prerequisite: CMake 2.8+, OpenCV 2.0+, Tesseract-OCR 2.4+

Assume you are in the top directory of Sikuli's source tree.

1. make a "build" directory 
  mkdir build

2. generate makefiles with CMake
  cd build; cmake ..

3. generate a release package. The package will be generated in "build".
  make package

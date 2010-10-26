= How To Build Sikuli on Mac and Linux =

Prerequisite: CMake 2.8+, OpenCV 2.1+, Tesseract-OCR 2.04 (3.0 won't work), SWIG 1.3+

Note for Linux: The Ubuntu package tesseract-ocr doesn't have headers and libraries for development. You need to compile and install tesseract-ocr from its official site. Besides, you also need libtiff-dev on Linux.


Assume you are in the top directory of Sikuli's source tree.

1. make a "build" directory 
  mkdir build

2. generate makefiles with CMake
  cd build
  cmake ..

3. generate a release package. The package will be generated in "build".
  make package


= Build Sikuli on Windows =

Prerequisite: CMake 2.8+, OpenCV 2.1+, Tesseract-OCR 2.04 (3.0 won't work), SWIG 1.3+, Visual C++

Note for old users: Cygwin is not required anymore. Sikuli can be built only with NMake and Visual C++ now.

1. make a "build" directory 
  mkdir build

2. generate makefiles with CMake
  cd build
  cmake -G "NMake Makefiles" -D CMAKE_BUILD_TYPE=Release ..

3. generate a release package. The package will be generated in "build".
  nmake package


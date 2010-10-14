ENABLE_LANGUAGE(CXX)

SET(CMAKE_OSX_ARCHITECTURES i386 x86_64)
SET(LIBRARY_OUTPUT_PATH "${BINARY_DIR}/lib")

SET(OpenCV_DIR "/opt/opencv/share/opencv")

FIND_PACKAGE(OpenCV REQUIRED)
FIND_PACKAGE(JNI REQUIRED)

INCLUDE_DIRECTORIES(${JNI_INCLUDE_DIRS})
INCLUDE_DIRECTORIES(${OPENCV_INCLUDE_DIR})


SET(BUILD_TARGETS
      ScreenMatchProxy
      VDictProxy
)

ADD_LIBRARY(VDictProxy SHARED
   VDictProxy.cc
)

ADD_LIBRARY(ScreenMatchProxy SHARED
   EventManagerProxy.cc
   FinderProxy.cc
   finder.cc
   event-manager.cc
   TimingBlock.cc
   pyramid-template-matcher.cpp
   ocr.cpp
   cv-util.cpp
)


foreach(BUILD_TARGET ${BUILD_TARGETS})
   TARGET_LINK_LIBRARIES(${BUILD_TARGET} ${OpenCV_LIBS})
   if(APPLE)
      set_target_properties(${BUILD_TARGET} PROPERTIES SUFFIX ".jnilib")
   endif(APPLE)
endforeach(BUILD_TARGET ${BUILD_TARGETS})

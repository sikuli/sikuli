ENABLE_LANGUAGE(CXX)

SET(CMAKE_VERBOSE_MAKEFILE 1)

SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../../../../cmake_modules") #REMOVE ME
SET(OpenCV_DIR "/opt/opencv/share/opencv") #REMOVE ME
SET(CMAKE_OSX_ARCHITECTURES i386 x86_64)

FIND_PROGRAM ( JAVA_JAVA_H javah PATHS ${JAVA_BIN_PATH} )
SET(JNI_HEADERS
#   edu_mit_csail_uid_EventManager.h
#   edu_mit_csail_uid_Finder.h
#   edu_mit_csail_uid_VDictProxy.h
#   edu_mit_csail_uid_Win32Util.h
)

SET(LIBRARY_OUTPUT_PATH ${BINARY_LIB_DIR})

FIND_PACKAGE(OpenCV REQUIRED)
FIND_PACKAGE(Tesseract REQUIRED)
FIND_PACKAGE(JNI REQUIRED)

INCLUDE_DIRECTORIES(${JNI_INCLUDE_DIRS})
INCLUDE_DIRECTORIES(${OPENCV_INCLUDE_DIR})
INCLUDE_DIRECTORIES(${Tesseract_INCLUDE_DIR})


SET(BUILD_TARGETS
      VisionProxy
      #      VDictProxy
)

#ADD_LIBRARY(VDictProxy SHARED
#   VDictProxy.cc
#   edu_mit_csail_uid_VDictProxy.h
#)

ADD_LIBRARY(VisionProxy SHARED
   TimingBlock.cc
   cvgui.cpp
   finder.cpp
   imgdb.cpp
   pyramid-template-matcher.cpp
   tessocr.cpp
   vision.cpp
)
TARGET_LINK_LIBRARIES(VisionProxy ${Tesseract_LIBRARY})

foreach(JNI_HEADER ${JNI_HEADERS})
   STRING(REGEX REPLACE "_" "." JNI_CLASS ${JNI_HEADER})
   STRING(REGEX REPLACE "\\.h$" "" JNI_CLASS ${JNI_CLASS})
   STRING(REGEX REPLACE "\\." "/" JNI_JAVA_SOURCE ${JNI_CLASS})
   SET(JNI_JAVA_SOURCE "${JNI_JAVA_SOURCE}.java")
   ADD_CUSTOM_COMMAND(
      OUTPUT ${CMAKE_CURRENT_SOURCE_DIR}/${JNI_HEADER}
      COMMAND ${JAVA_JAVA_H} -d ${CMAKE_CURRENT_SOURCE_DIR} 
                             -classpath ${BINARY_CLASS_DIR} ${JNI_CLASS}
      DEPENDS ${JAVA_SRC_DIR}/${JNI_JAVA_SOURCE}
   )
endforeach(JNI_HEADER ${JNI_HEADERS})


foreach(BUILD_TARGET ${BUILD_TARGETS})
   TARGET_LINK_LIBRARIES(${BUILD_TARGET} ${OpenCV_LIBS})
   if(APPLE)
      set_target_properties(${BUILD_TARGET} PROPERTIES SUFFIX ".jnilib")
   endif(APPLE)
endforeach(BUILD_TARGET ${BUILD_TARGETS})

add_custom_target(${JAR_FILE}.libs-in-jar
   COMMAND ${CMAKE_COMMAND} -E make_directory ${JAR_DIR}/META-INF
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${BINARY_LIB_DIR} ${JAR_DIR}/META-INF/lib
)

add_dependencies(${JAR_FILE}.libs-in-jar ${BUILD_TARGETS})

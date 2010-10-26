ENABLE_LANGUAGE(CXX)

SET(CMAKE_VERBOSE_MAKEFILE 1)

SET(CMAKE_OSX_ARCHITECTURES i386 x86_64)

FIND_PROGRAM ( JAVA_JAVA_H javah PATHS ${JAVA_BIN_PATH} )
SET(JNI_HEADERS
   edu_mit_csail_uid_EventManager.h
   edu_mit_csail_uid_VDictProxy.h
   edu_mit_csail_uid_Win32Util.h
)

SET(LIBRARY_OUTPUT_PATH ${BINARY_LIB_DIR})

FIND_PACKAGE(OpenCV REQUIRED)
FIND_PACKAGE(JNI REQUIRED)
FIND_PACKAGE(SWIG REQUIRED)

INCLUDE_DIRECTORIES(${JNI_INCLUDE_DIRS})
INCLUDE_DIRECTORIES(${OPENCV_INCLUDE_DIR})
INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR})
INCLUDE(${SWIG_USE_FILE})

if(NOT WIN32)
   FIND_PACKAGE(Tesseract REQUIRED)
   INCLUDE_DIRECTORIES(${Tesseract_INCLUDE_DIR})
else()
   SET(TESSERACT_SRC_DIRS ccmain ccutil ccstruct viewer 
                          image textord wordrec dict cutil
                          classify aspirin dict display pageseg)
   FOREACH(DIR ${TESSERACT_SRC_DIRS})
      FILE(GLOB T_SRCS 
         ${TESSERACT_SRC_DIR}/${DIR}/*.cpp
      )
      SET(TESSERACT_SOURCES ${TESSERACT_SOURCES} ${T_SRCS})
      INCLUDE_DIRECTORIES(${TESSERACT_SRC_DIR}/${DIR})
   ENDFOREACH()
   LIST(REMOVE_ITEM TESSERACT_SOURCES ${TESSERACT_SRC_DIR}/ccmain/tessembedded.cpp)
   LIST(REMOVE_ITEM TESSERACT_SOURCES ${TESSERACT_SRC_DIR}/ccutil/scanutils.cpp)
   LIST(REMOVE_ITEM TESSERACT_SOURCES ${TESSERACT_SRC_DIR}/viewer/svpaint.cpp)

   INCLUDE_DIRECTORIES($ENV{INCLUDE})
   LINK_DIRECTORIES($ENV{LIB})
   SET(Tesseract_INCLUDE_DIR "")
   SET(Tesseract_LIBRARY user32 ws2_32)
   SET(CMAKE_CXX_FLAGS "/D__MSW32__ /DWIN32 /D_WINDOWS /D_CRT_SECURE_NO_WARNINGS")
endif()


LINK_DIRECTORIES(${OpenCV_LIBRARY_DIR})

SET(CMAKE_SWIG_FLAGS -package ${PACKAGE})
SET(SWIG_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})

IF(LINUX)
   FIND_LIBRARY(TIFF_LIBS NAMES tiff)
   LIST(APPEND OpenCV_LIBS ${TIFF_LIBS})
ENDIF()

SET(BUILD_TARGETS
   #TODO move VDictProxy to swig
   VDictProxy
)

IF(WIN32)
   LIST(APPEND BUILD_TARGETS Win32Util)

   ADD_LIBRARY(Win32Util SHARED
      Win32Util.cc
      edu_mit_csail_uid_Win32Util.h
   )
ENDIF()

SET(NATIVE_LIBS
   VisionProxy
   ${BUILD_TARGETS}
)

ADD_LIBRARY(VDictProxy SHARED
   VDictProxy.cc
   edu_mit_csail_uid_VDictProxy.h
)


## SWIG: generate a JNI wrapper 
SET_SOURCE_FILES_PROPERTIES(vision.i PROPERTIES CPLUSPLUS ON)
SWIG_ADD_MODULE(VisionProxy java vision.i
   TimingBlock.cc
   cvgui.cpp
   finder.cpp
   imgdb.cpp
   pyramid-template-matcher.cpp
   tessocr.cpp
   vision.cpp
   EventManagerProxy.cc
   event-manager.cc
   sikuli-debug.cpp
   edu_mit_csail_uid_EventManager.h
   ${TESSERACT_SOURCES}
) 
SWIG_LINK_LIBRARIES(VisionProxy ${Tesseract_LIBRARY} ${OpenCV_LIBS})  


#TODO: replace jni custom commands by swig
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

add_dependencies(${JAR_FILE}.libs-in-jar ${NATIVE_LIBS})

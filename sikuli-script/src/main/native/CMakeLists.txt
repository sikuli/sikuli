# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
ENABLE_LANGUAGE(CXX)

IF(LINUX)
   # -O2/O3 seems incorrectly optimizes some SWIG generated code
   SET(CMAKE_CXX_FLAGS_RELEASE "-O1")
ENDIF(LINUX)

SET(CMAKE_VERBOSE_MAKEFILE 1)

SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../../../../cmake_modules/")
INCLUDE("${CMAKE_MODULE_PATH}/common.cmake")


FIND_PROGRAM ( JAVA_JAVA_H javah PATHS ${JAVA_BIN_PATH} )
SET(JNI_HEADERS
   org_sikuli_script_VDictProxy.h
   org_sikuli_script_Win32Util.h
   org_sikuli_script_MacUtil.h
   org_sikuli_script_internal_hotkey_MacHotkeyManager.h
)

SET(LIBRARY_OUTPUT_PATH ${BINARY_LIB_DIR})

FIND_PACKAGE(OpenCV REQUIRED)
FIND_PACKAGE(JNI REQUIRED)
FIND_PACKAGE(SWIG REQUIRED)

INCLUDE_DIRECTORIES(${JNI_INCLUDE_DIRS})
INCLUDE_DIRECTORIES(${OPENCV_INCLUDE_DIR})
INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR})
INCLUDE(${SWIG_USE_FILE})

# profiling
#LIST(APPEND CMAKE_CXX_FLAGS "-DENABLE_TIMING")
# visual logger for debugging
#LIST(APPEND CMAKE_CXX_FLAGS "-DENABLE_VISUAL_LOG")
# OCR matching debug
#LIST(APPEND CMAKE_CXX_FLAGS "-DENABLE_OCR_DEBUG")

IF(NOT WIN32)
   FIND_PACKAGE(Tesseract REQUIRED)
   INCLUDE_DIRECTORIES(${Tesseract_INCLUDE_DIR})
ELSE()
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
ENDIF()


LINK_DIRECTORIES(${OpenCV_LIBRARY_DIR})

# tom SET(CMAKE_SWIG_FLAGS -package ${PACKAGE})
SET(CMAKE_SWIG_FLAGS -package ${NATIVE_PACKAGE})
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
      org_sikuli_script_Win32Util.h
   )
   TARGET_LINK_LIBRARIES(Win32Util ${JAVA_AWT_LIBRARY})
ENDIF()


IF(APPLE)
   LIST(APPEND BUILD_TARGETS MacUtil)
   FIND_LIBRARY(JAVA_LIBRARY JavaVM)
   FIND_LIBRARY(COCOA_LIBRARY Cocoa)
   FIND_LIBRARY(CARBON_LIBRARY Carbon)
   FIND_LIBRARY(COREFOUNDATION_LIBRARY CoreFoundation)
   SET(EXTRA_LIBS ${JAVA_LIBRARY} ${COCOA_LIBRARY} ${CARBON_LIBRARY} ${COREFOUNDATION_LIBRARY})

   ADD_LIBRARY(MacUtil SHARED
      MacUtil.m
      org_sikuli_script_MacUtil.h
   )
   TARGET_LINK_LIBRARIES(MacUtil ${EXTRA_LIBS})

   SET(BUILD_TARGETS
      MacHotkeyManager
   )

   ADD_LIBRARY(MacHotkeyManager SHARED
      MacHotkeyManager.cc
      sikuli-debug.cpp
      org_sikuli_script_internal_hotkey_MacHotkeyManager.h
   )
   TARGET_LINK_LIBRARIES(MacHotkeyManager ${EXTRA_LIBS})
ENDIF()

SET(NATIVE_LIBS
   VisionProxy
   ${BUILD_TARGETS}
)

ADD_LIBRARY(VDictProxy SHARED
   VDictProxy.cc
   org_sikuli_script_VDictProxy.h
)
TARGET_LINK_LIBRARIES(VDictProxy ${OpenCV_LIBS})


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
   sikuli-debug.cpp
   ${TESSERACT_SOURCES}
) 
SWIG_LINK_LIBRARIES(VisionProxy ${Tesseract_LIBRARY} ${OpenCV_LIBS})  


#TODO: replace jni custom commands by swig
foreach(JNI_HEADER ${JNI_HEADERS})
   STRING(REGEX REPLACE "_" "." JNI_CLASS ${JNI_HEADER})
   STRING(REGEX REPLACE "\\.h$" "" JNI_CLASS ${JNI_CLASS})
   STRING(REGEX REPLACE "\\." "/" JNI_JAVA_SOURCE ${JNI_CLASS})
   SET(JNI_JAVA_CLASS "${JNI_JAVA_SOURCE}.class")
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

add_dependencies(${JAR_FILE}.libs-in-jar ${NATIVE_LIBS})

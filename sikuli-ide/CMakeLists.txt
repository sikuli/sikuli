# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
PROJECT (sikuli-ide Java)

SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../cmake_modules")
INCLUDE(${CMAKE_MODULE_PATH}/common.cmake)
INCLUDE(${CMAKE_MODULE_PATH}/detect_osx_ver.cmake)
INCLUDE(${CMAKE_MODULE_PATH}/find_dep_libs.cmake)


ENABLE_TESTING()

SET(CMAKE_VERBOSE_MAKEFILE 0)

FIND_PACKAGE(Java 1.6 REQUIRED)
SET( NON_NATIVE_TARGET TRUE)

IF(UNIX)
   SET(SEP ":")
ELSE()
   SET(SEP ";")
ENDIF()


SET(BASE_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
SET(JAVA_SRC_DIR "${BASE_DIR}/src/main/java")
SET(NATIVE_SRC_DIR "${BASE_DIR}/src/main/native")
SET(COMMON_LIB_DIR "${BASE_DIR}/../lib")
SET(INCLUDE_LIB_DIR "${BASE_DIR}/lib")
SET(BINARY_DIR "${BASE_DIR}/target")
SET(BINARY_CLASS_DIR "${BINARY_DIR}/classes")
SET(BINARY_LIB_DIR "${BINARY_DIR}/lib")
SET(JAR_DIR "${BINARY_DIR}/jar")
SET(RESOURCE_DIR "${BASE_DIR}/resources")
SET(RELEASE_DIR "${BASE_DIR}/../release")

SET(JAVA_TEST_DIR "${BASE_DIR}/src/test/java")
SET(TEST_RESOURCE_DIR "test-res")
SET(JAVA_TEST_FOR_HUDSON_XML "../hudson-test.xml")
SET(TEST_CLASS_DIR "${CMAKE_BINARY_DIR}/test") 

SET(SIKULI_SCRIPT_JAR "${COMMON_LIB_DIR}/sikuli-script.jar")
SET(JUNIT_JAR "${COMMON_LIB_DIR}/junit-3.8.1.jar")
SET(SWING_LAYOUT_JAR "${COMMON_LIB_DIR}/swing-layout-1.0.1.jar")
SET(COMMONS_CLI_JAR "${COMMON_LIB_DIR}/commons-cli-1.2.jar")
SET(JSON_SIMPLE_JAR "${COMMON_LIB_DIR}/json_simple-1.1.jar")
SET(SWINGX_JAR "${COMMON_LIB_DIR}/swingx-core-1.6.2.jar")
SET(MAC_WIDGETS_JAR "${COMMON_LIB_DIR}/mac_widgets.jar")
SET(FORMS_JAR "${COMMON_LIB_DIR}/forms-1.2.1.jar")


FILE(MAKE_DIRECTORY ${BINARY_DIR})
FILE(MAKE_DIRECTORY ${BINARY_CLASS_DIR})
FILE(MAKE_DIRECTORY ${JAR_DIR})
FILE(MAKE_DIRECTORY ${TEST_CLASS_DIR})

## Sikuli IDE

SET(MAIN_CLASS org.sikuli.ide.SikuliIDE)
SET(RUN_USER_ARGS -Dsikuli.console=true -Dfile.encoding=UTF-8 
             -Dsikuli.testing=yes
             -Xms64m -Xmx512m 
   CACHE STRING 
   "The arguments for running Sikuli IDE with 'make run-user'"
)
SET(RUN_ARGS -Dsikuli.console=false -Dsikuli.Debug=3 -Dfile.encoding=UTF-8 
             -Dsikuli.testing=yes
             -Xms64m -Xmx512m 
             #-Duser.language=zh -Duser.region=TW 
   CACHE STRING 
   "The arguments for running Sikuli IDE with 'make run'"
)
IF(APPLE)
   LIST(APPEND RUN_ARGS -Xdock:name='Sikuli IDE') 
ENDIF()

#SET(ARGS -s -r ../sample-scripts/mod1.sikuli --args a1 a2) # example args
SET(ARGS "")

SET(CLASSPATH 
   ${SIKULI_SCRIPT_JAR}${SEP}${JUNIT_JAR}${SEP}${SWING_LAYOUT_JAR}${SEP}${COMMONS_CLI_JAR}${SEP}${JSON_SIMPLE_JAR}${SEP}${SWINGX_JAR}${SEP}${MAC_WIDGETS_JAR}${SEP}${FORMS_JAR}
)

SET(JAR_FILE sikuli-ide.jar)
SET(RESOURCES icons META-INF scripts i18n)

IF(LINUX)
   SET(MANIFEST ${RESOURCE_DIR}/META-INF/MANIFEST-linux.MF)
   SET(CLASSPATH ${CLASSPATH}${SEP}${JXGRABKEY_JAR})
ELSEIF(WIN32)
   SET(MANIFEST ${RESOURCE_DIR}/META-INF/MANIFEST-win32.MF)
   #SET(CLASSPATH ${CLASSPATH}${SEP}${JINTELLITYPE_JAR})
ELSE()
   SET(MANIFEST ${RESOURCE_DIR}/META-INF/MANIFEST.MF)
ENDIF()


add_custom_target( ${JAR_FILE} 
   COMMAND ${JAVA_ARCHIVE} cfm ${BINARY_DIR}/${JAR_FILE}
           ${MANIFEST} -C ${JAR_DIR} *
   COMMAND ${CMAKE_COMMAND} -E copy ${BINARY_DIR}/${JAR_FILE} ${COMMON_LIB_DIR}/${JAR_FILE}
   COMMENT "Packaging ${JAR_FILE}"
   WORKING_DIRECTORY ${JAR_DIR}
)

add_custom_target( ${JAR_FILE}.resources
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${RESOURCE_DIR} ${JAR_DIR}
   COMMENT "Copying IDE resources"
)

add_custom_target( ${JAR_FILE}.prepare
   COMMENT "Preparing ${JAR_FILE}"
)

add_custom_target( run-user
   COMMAND ${JAVA_RUNTIME} ${RUN_USER_ARGS} 
                        -classpath "${CLASSPATH}${SEP}${JAR_DIR}"
                        ${MAIN_CLASS} ${ARGS}
)

add_custom_target( run
   COMMAND ${JAVA_RUNTIME} ${RUN_ARGS} 
                        -classpath "${CLASSPATH}${SEP}${JAR_DIR}"
                        ${MAIN_CLASS} ${ARGS}
)

if(NOT DEFINED SIKULI_VERSION OR SIKULI_VERSION STREQUAL "")
   file(READ ${BASE_DIR}/latest-ver SIKULI_VERSION)
   STRING(STRIP ${SIKULI_VERSION} SIKULI_VERSION)
ENDIF()
message("Sikuli Version ${SIKULI_VERSION}")

SET(JYTHON_CLASS org.python.util.jython)
add_custom_target( update-version
      COMMAND ${JAVA_RUNTIME} -cp "${CLASSPATH}" ${JYTHON_CLASS} ${BASE_DIR}/../utils/update-version.py  ${JAVA_SRC_DIR}/org/sikuli/ide/IDESettings.java "${SIKULI_VERSION}"
   COMMENT "Update version number in Sikuli-IDE"
)

SET(INCLUDE_JARS ${SIKULI_SCRIPT_JAR} ${JUNIT_JAR} 
                 ${SWING_LAYOUT_JAR} ${COMMONS_CLI_JAR}
                 ${BINARY_DIR}/${JAR_FILE} ${JSON_SIMPLE_JAR}
                 ${SWINGX_JAR} ${MAC_WIDGETS_JAR} ${FORMS_JAR}
)

## Windows Package

IF(WIN32)
   SET(OS "win32")
   SET(APP_FILE "Sikuli-IDE")
   SET(BINARY_APP_DIR  "${BINARY_DIR}/${OS}")
   SET(JAR_IN_APP_DIR  ${BINARY_APP_DIR}/${APP_FILE})
   SET(FRAMEWORKS_IN_APP_DIR  ${JAR_IN_APP_DIR}/libs)
   SET(JAR_LIB_DIR  ${JAR_DIR}/META-INF/lib)

   #LIST(APPEND INCLUDE_JARS ${JINTELLITYPE_JAR})
   FILE(GLOB sikuli_script_jnilibs "${BASE_DIR}/../sikuli-script/target/lib/*.dll")
   SET(jnilibs ${sikuli_script_jnilibs})
ENDIF()

## Linux Package

IF(LINUX)
   SET(OS "linux")
   SET(APP_FILE "Sikuli-IDE")
   SET(BINARY_APP_DIR  "${BINARY_DIR}/${OS}")
   SET(JAR_IN_APP_DIR  ${BINARY_APP_DIR}/${APP_FILE})
   SET(FRAMEWORKS_IN_APP_DIR  ${JAR_IN_APP_DIR}/libs)
   SET(JAR_LIB_DIR  ${JAR_DIR}/META-INF/lib)

   LIST(APPEND INCLUDE_JARS ${JXGRABKEY_JAR})
   SET(jnilibs "")
   FILE(GLOB sikuli_script_jnilibs "${BASE_DIR}/../sikuli-script/target/lib/*.so")
   LIST(APPEND jnilibs ${sikuli_script_jnilibs})
ENDIF(LINUX)

## Sikuli-IDE.App for Mac OS X

IF(APPLE)
   IF(APPLE_SNOW_LEOPARD)
      SET(OSX_VER "osx-10.6")
   ELSEIF(APPLE_LEOPARD)
      SET(OSX_VER "osx-10.5")
   ENDIF()
   SET(OS ${OSX_VER})

   SET(APP_FILE  "Sikuli-IDE.app")
   SET(BINARY_APP_DIR  "${BINARY_DIR}/${OS}")
   SET(JAR_IN_APP_DIR  ${BINARY_APP_DIR}/${APP_FILE}/Contents/Resources/Java)
   SET(FRAMEWORKS_IN_APP_DIR  ${BINARY_APP_DIR}/${APP_FILE}/Contents/Frameworks)

   FIND_PACKAGE(OpenCV REQUIRED)
   #message("opencv lib: ${OpenCV_LIB_DIR}")
   LIST(APPEND CMAKE_LIBRARY_PATH ${OpenCV_LIB_DIR})

   FILE(GLOB jnilibs "${BINARY_LIB_DIR}/*.jnilib")
   FILE(GLOB sikuli_script_jnilibs "${BASE_DIR}/../sikuli-script/target/lib/*.jnilib")
   LIST(APPEND jnilibs ${sikuli_script_jnilibs})

   find_dep_libs("${jnilibs}" deplibs)
   find_dep_libs("${deplibs}" deplibs)
   #MESSAGE("Dep: ${deplibs}")

ENDIF(APPLE)

SET(RELEASE_FILE "Sikuli-${SIKULI_VERSION}-${OS}")

## Packaging App
add_custom_target( ${APP_FILE}
   COMMENT "Generate ${BINARY_DIR}/${APP_FILE}"
)

add_dependencies( ${APP_FILE} ${JAR_FILE} )

IF(LINUX)
   EXEC_PROGRAM(uname ARGS -m OUTPUT_VARIABLE SYSTEM_ARCH)
   SET(RELEASE_FILE "${RELEASE_FILE}-${SYSTEM_ARCH}")
   if(SYSTEM_ARCH MATCHES "x86_64")
      SET(LIB_GRABKEY "${COMMON_LIB_DIR}/jxgrabkey/lib/libJXGrabKey-64.so")
   else()
      SET(LIB_GRABKEY "${COMMON_LIB_DIR}/jxgrabkey/lib/libJXGrabKey-32.so")
   endif()

   add_custom_target( ${APP_FILE}.framework 
      COMMAND ${CMAKE_COMMAND} -E make_directory ${FRAMEWORKS_IN_APP_DIR}
      COMMAND cp ${jnilibs} ${FRAMEWORKS_IN_APP_DIR}
      COMMAND cp ${LIB_GRABKEY} ${FRAMEWORKS_IN_APP_DIR}/libJXGrabKey.so
      COMMENT "Packaging Frameworks in App"
   )
   add_dependencies( ${APP_FILE} ${APP_FILE}.framework ) 

ENDIF(LINUX)

IF(WIN32)
   SET(APP_FILES Sikuli-IDE-w.bat Sikuli-IDE.bat Sikuli-IDE.exe ide-icon.ico)
   SET(WIN32_LIBS ${COMMON_LIB_DIR}/win32)
   SET(OPENCV_LIBS cv210 cvaux210 cxcore210 highgui210 ml210)

   SET(LIB_GRABKEY "${COMMON_LIB_DIR}/jintellitype-1.3.6/JIntellitype.dll")
   add_custom_target( ${JAR_FILE}.framework 
      COMMAND ${CMAKE_COMMAND} -E make_directory ${JAR_LIB_DIR}
      COMMAND ${CMAKE_COMMAND} -E copy ${LIB_GRABKEY} ${JAR_LIB_DIR}/
      COMMENT "Packaging Frameworks in Jar"
   )
   add_dependencies( ${JAR_FILE} ${JAR_FILE}.framework ) 

   add_custom_target( ${APP_FILE}.framework 
      COMMAND ${CMAKE_COMMAND} -E make_directory ${FRAMEWORKS_IN_APP_DIR}
      COMMAND ${CMAKE_COMMAND} -E copy_directory ${WIN32_LIBS} ${FRAMEWORKS_IN_APP_DIR}
      COMMENT "Packaging Frameworks in App"
   )
   add_dependencies( ${APP_FILE} ${APP_FILE}.framework ) 

   #libs

   install(FILES ${jnilibs}
           DESTINATION ${FRAMEWORKS_IN_APP_DIR})
   FOREACH(F ${OPENCV_LIBS})
      install(FILES ${OpenCV_DIR}/bin/${F}.dll
              DESTINATION ${FRAMEWORKS_IN_APP_DIR})
   ENDFOREACH()

   FOREACH(F ${APP_FILES})
      install(FILES ${BINARY_APP_DIR}/${F}
              DESTINATION ${JAR_IN_APP_DIR})

   ENDFOREACH()
ENDIF(WIN32)

IF(APPLE)
   add_custom_target( ${APP_FILE}.framework 
      COMMAND ${CMAKE_COMMAND} -E make_directory ${FRAMEWORKS_IN_APP_DIR}
      COMMAND cp -f ${jnilibs} ${FRAMEWORKS_IN_APP_DIR}
      COMMAND cp -f ${deplibs} ${FRAMEWORKS_IN_APP_DIR}
      COMMAND ${CMAKE_COMMAND} -E chdir ${FRAMEWORKS_IN_APP_DIR} ./change-lib-path.sh
      COMMENT "Packaging Frameworks"
   )

   add_custom_target( ${APP_FILE}.update_ver
	COMMAND defaults write ${BINARY_APP_DIR}/${APP_FILE}/Contents/Info CFBundleVersion `echo ${SIKULI_VERSION} | awk '{print $$1}'`
	COMMAND plutil -convert xml1 ${BINARY_APP_DIR}/${APP_FILE}/Contents/Info.plist
   )

   add_dependencies( ${APP_FILE} ${APP_FILE}.framework ${APP_FILE}.update_ver)

ENDIF(APPLE)

install(DIRECTORY ${BINARY_APP_DIR}/${APP_FILE}
   USE_SOURCE_PERMISSIONS
   DESTINATION .
)


add_custom_target( app ALL
)


FOREACH(JAR ${INCLUDE_JARS})
   get_filename_component(JAR_FILENAME ${JAR} NAME)
   get_filename_component(target ${JAR} NAME_WE)
   set(target "include_${target}")
   add_custom_target( ${target}
      COMMAND ${CMAKE_COMMAND} -E copy ${JAR} ${JAR_IN_APP_DIR}/${JAR_FILENAME}
                  
      COMMENT "Copying ${JAR_FILENAME}"
   )
   add_dependencies( ${APP_FILE} ${target} )
ENDFOREACH(JAR ${INCLUDE_JARS})


add_dependencies( app ${APP_FILE})

## Language files

add_custom_target( update-po
   COMMAND prop2po ${RESOURCE_DIR}/i18n/IDE_en_US.properties > ${RESOURCE_DIR}/i18n/IDE/IDE.pot
               
   COMMENT "Update the IDE.pot file"
)

add_custom_target( update-prop
   COMMAND ${BASE_DIR}/../utils/update-prop.sh ${RESOURCE_DIR}
   COMMENT "Update the IDE language properties."
)


## Packaging

if(APPLE)
      SET(CPACK_GENERATOR DragNDrop )
      #   SET(CPACK_GENERATOR ZIP )
else(APPLE)
   SET(CPACK_GENERATOR 
      ZIP
   )
endif(APPLE)

SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY "Sikuli ${SIKULI_VERSION}")
SET(CPACK_PACKAGE_VENDOR "Sikuli Development Team")
SET(CPACK_PACKAGE_DESCRIPTION_FILE "${BASE_DIR}/../readme.txt")
SET(CPACK_RESOURCE_FILE_LICENSE "${BASE_DIR}/../copyright.txt")
SET(CPACK_PACKAGE_FILE_NAME ${RELEASE_FILE})

INCLUDE(CPack)

install(FILES 
   ${CPACK_PACKAGE_DESCRIPTION_FILE}
   ${CPACK_RESOURCE_FILE_LICENSE}
   DESTINATION .
)

# type "make package" to generate release packages


## tests

SET(MAIN_TEST_SUITE ${PACKAGE}.SikuliIDETestSuite)

add_custom_target( java-test.test-res
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${BASE_DIR}/${TEST_RESOURCE_DIR} ${CMAKE_BINARY_DIR}/${TEST_RESOURCE_DIR}
   COMMENT "Copy test resource"
)

add_custom_target( java-test
   #   COMMAND ${JAVA_RUNTIME} -cp ${TEST_CLASS_DIR}${SEP}${BINARY_DIR}/${JAR_FILE}${SEP}${JUNIT_JAR} junit.textui.TestRunner ${MAIN_TEST_SUITE}
   COMMAND ant -f ${JAVA_TEST_FOR_HUDSON_XML}
   COMMENT "Run Java unit tests"
)

add_dependencies( java-test
        ${JAR_FILE}
        ${PROJECT_NAME}.test.classes
        java-test.test-res
)

add_custom_target( all-test
)

add_dependencies( all-test
   java-test   
)

## Dependencies

add_dependencies( ${JAR_FILE}.prepare
        ${JAR_FILE}.resources
        ${JAR_FILE}.classes-in-jar 
        #        ${JAR_FILE}.libs-in-jar
)

add_dependencies( ${JAR_FILE} ${JAR_FILE}.prepare )
add_dependencies( run-user ${JAR_FILE})
add_dependencies( run ${JAR_FILE})
add_dependencies( app ${APP_FILE}) 


ADD_SUBDIRECTORY( ${JAVA_SRC_DIR} )
#ADD_SUBDIRECTORY( ${NATIVE_SRC_DIR} )
ADD_SUBDIRECTORY( ${JAVA_TEST_DIR} )



SET(SIKULI_VERSION  CACHE INTERNAL "" FORCE)


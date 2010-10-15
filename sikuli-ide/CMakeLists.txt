SET(OpenCV_DIR "/opt/opencv")
SET(CMAKE_LIBRARY_PATH "${OpenCV_DIR}/lib")

INCLUDE(${CMAKE_CURRENT_SOURCE_DIR}/../cmake_modules/detect_osx_ver.cmake)
INCLUDE(${CMAKE_CURRENT_SOURCE_DIR}/../cmake_modules/find_dep_libs.cmake)

CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
PROJECT (sikuli-ide Java)

ENABLE_TESTING()

SET(CMAKE_VERBOSE_MAKEFILE 0)

FIND_PACKAGE(Java)
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

SET(SIKULI_SCRIPT_JAR "${COMMON_LIB_DIR}/sikuli-script.jar")
SET(JUNIT_JAR "${COMMON_LIB_DIR}/junit-3.8.1.jar")
SET(SWING_LAYOUT_JAR "${COMMON_LIB_DIR}/swing-layout-1.0.1.jar")
SET(COMMONS_CLI_JAR "${COMMON_LIB_DIR}/commons-cli-1.2.jar")


FILE(MAKE_DIRECTORY ${BINARY_DIR})
FILE(MAKE_DIRECTORY ${BINARY_CLASS_DIR})
FILE(MAKE_DIRECTORY ${JAR_DIR})

## Sikuli IDE

SET(MAIN_CLASS edu.mit.csail.uid.SikuliIDE)
SET(RUN_ARGS -Dsikuli.console=false -Dsikuli.Debug=3 -Dfile.encoding=UTF-8 
             -Xms64m -Xmx512m
             #-Duser.language=zh -Duser.region=TW
)
IF(APPLE)
   SET(RUN_ARGS ${RUN_ARGS} -Xdock:name='Sikuli IDE') 
ENDIF()

SET(CLASSPATH 
   ${SIKULI_SCRIPT_JAR}${SEP}${JUNIT_JAR}${SEP}${SWING_LAYOUT_JAR}${SEP}${COMMONS_CLI_JAR}
)

SET(JAR_FILE sikuli-ide.jar)
SET(RESOURCES icons META-INF scripts i18n)

IF(LINUX)
   SET(MANIFEST ${RESOURCE_DIR}/META-INF/MANIFEST-linux.MF)
ELSE()
   SET(MANIFEST ${RESOURCE_DIR}/META-INF/MANIFEST.MF)
ENDIF()


add_custom_target( ${JAR_FILE} ALL
   COMMAND ${JAVA_ARCHIVE} cfm ${BINARY_DIR}/${JAR_FILE}
           ${MANIFEST} -C ${JAR_DIR} *
   COMMAND ${CMAKE_COMMAND} -E copy ${BINARY_DIR}/${JAR_FILE} ${COMMON_LIB_DIR}/${JAR_FILE}
   COMMENT "Packaging ${JAR_FILE}"
   WORKING_DIRECTORY ${JAR_DIR}
)

add_custom_target( ${JAR_FILE}.resources
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${RESOURCE_DIR} ${BINARY_CLASS_DIR}
   COMMENT "Copying IDE resources"
)

add_custom_target( ${JAR_FILE}.prepare
   COMMENT "Preparing ${JAR_FILE}"
)

add_custom_target( run
   COMMAND ${JAVA_RUNTIME} ${RUN_ARGS} 
                        -classpath ${CLASSPATH}${SEP}${JAR_DIR} 
                        ${MAIN_CLASS} ${ARGS}
)


## Sikuli-IDE.App for Mac OS X

SET(INCLUDE_JARS ${SIKULI_SCRIPT_JAR} ${JUNIT_JAR} 
                 ${SWING_LAYOUT_JAR} ${COMMONS_CLI_JAR}
                 ${BINARY_DIR}/${JAR_FILE}
)

IF(APPLE)
   IF(APPLE_SNOW_LEOPARD)
      SET(OSX_VER "osx-10.6")
   ELSEIF(APPLE_LEOPARD)
      SET(OSX_VER "osx-10.5")
   ENDIF()

   SET(APP_FILE  "Sikuli-IDE.app")
   SET(BINARY_APP_DIR  "${BINARY_DIR}/${OSX_VER}")
   SET(JAR_IN_APP_DIR  ${BINARY_APP_DIR}/${APP_FILE}/Contents/Resources/Java)
   SET(FRAMEWORKS_IN_APP_DIR  ${BINARY_APP_DIR}/${APP_FILE}/Contents/Frameworks)

   FILE(GLOB jnilibs "${BINARY_LIB_DIR}/*.jnilib")
   FILE(GLOB sikuli_script_jnilibs "${BASE_DIR}/../sikuli-script/target/lib/*.jnilib")
   LIST(APPEND jnilibs ${sikuli_script_jnilibs})

   find_dep_libs("${jnilibs}" deplibs)
   find_dep_libs("${deplibs}" deplibs)
   LIST(REMOVE_ITEM deplibs "/usr/lib/libstdc++.6.dylib" "/usr/lib/libSystem.B.dylib" 
                            "/usr/lib/system/libmathCommon.A.dylib" 
                            "/usr/lib/libbz2.1.0.dylib" "/usr/lib/libobjc.A.dylib")
   MESSAGE("Dep: ${deplibs}")

ENDIF(APPLE)

## Packaging App
add_custom_target( ${APP_FILE}
)

add_dependencies( ${APP_FILE} ${JAR_FILE} )

IF(APPLE)
   add_custom_target( ${APP_FILE}.framework 
      COMMAND ${CMAKE_COMMAND} -E make_directory ${FRAMEWORKS_IN_APP_DIR}
      COMMAND cp ${jnilibs} ${FRAMEWORKS_IN_APP_DIR}
      COMMAND cp ${deplibs} ${FRAMEWORKS_IN_APP_DIR}
      #COMMAND ${CMAKE_COMMAND} -E chdir ${FRAMEWORKS_IN_APP_DIR} change-lib-path.sh
      ##FIXME lib-path doesn't have /opt...
      COMMENT "Packaging Frameworks"
   )

   file(READ ${BASE_DIR}/latest-ver VERSION)
   add_custom_target( ${APP_FILE}.update_ver
	COMMAND defaults write ${BINARY_APP_DIR}/${APP_FILE}/Contents/Info CFBundleVersion ${VERSION}
	COMMAND plutil -convert xml1 ${BINARY_APP_DIR}/${APP_FILE}/Contents/Info.plist
   )

   add_dependencies( ${APP_FILE} ${APP_FILE}.framework ${APP_FILE}.update_ver)

ENDIF(APPLE)



FOREACH(JAR ${INCLUDE_JARS})
   get_filename_component(JAR_FILENAME ${JAR} NAME)
   get_filename_component(target ${JAR} NAME_WE)
   add_custom_target( ${target}
      COMMAND ${CMAKE_COMMAND} -E copy ${JAR} ${JAR_IN_APP_DIR}/${JAR_FILENAME}
                  
      COMMENT "Copying ${JAR_FILENAME}"
   )
   add_dependencies( ${APP_FILE} ${target} )
ENDFOREACH(JAR ${INCLUDE_JARS})


add_custom_target( app
)

add_dependencies( app ${APP_FILE} )

# Dependencies

add_dependencies( ${JAR_FILE}.prepare
        ${JAR_FILE}.resources
        ${JAR_FILE}.classes-in-jar 
        ${JAR_FILE}.libs-in-jar
)

add_dependencies( ${JAR_FILE} ${JAR_FILE}.prepare )
add_dependencies( run ${JAR_FILE}.prepare )

SUBDIRS(${JAVA_SRC_DIR} ${NATIVE_SRC_DIR})


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

# Dependencies

add_dependencies( ${JAR_FILE}.prepare
        ${JAR_FILE}.resources
        ${JAR_FILE}.classes-in-jar 
        ${JAR_FILE}.libs-in-jar
)

add_dependencies( ${JAR_FILE} ${JAR_FILE}.prepare )
add_dependencies( run ${JAR_FILE}.prepare )

SUBDIRS(${JAVA_SRC_DIR} ${NATIVE_SRC_DIR})


CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
PROJECT (android-robot Java)
SET(VERSION 0.2)

IF(UNIX)
   SET(SEP ":")
ELSE()
   SET(SEP ";")
ENDIF()


###
SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../../cmake_modules/")
INCLUDE(${CMAKE_MODULE_PATH}/common.cmake)
FIND_PACKAGE(Java)

SET(BASE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
SET(COMMON_LIB_DIR "${BASE_DIR}/../../lib")

SET(SIKULI_JAR ${COMMON_LIB_DIR}/sikuli-script.jar)
SET(ANDROID_SDK /opt/android-sdk-mac_x86)
SET(MONKEY_JAR ${ANDROID_SDK}/tools/lib/monkeyrunner.jar)
SET(CLASSPATH ${SIKULI_JAR}:${MONKEY_JAR})
SET(JAVA_ARGS -Djava.ext.dirs=${ANDROID_SDK}/tools/lib
              -Djava.library.path=${ANDROID_SDK}/tools/lib
              -Dcom.android.monkeyrunner.bindir=${ANDROID_SDK}/tools 
              -Dpython.path=${SIKULI_JAR}/
)
SET(MONKEY_RUNNER_CLASS com.android.monkeyrunner.MonkeyRunnerStarter)

###


SET(PYTHON_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/main/python)
SET(JAVA_SOURCE_FILES 
   ${CMAKE_CURRENT_SOURCE_DIR}/src/main/java/org/sikuli/script/android/*.java
)

SET(JAR_FILE ${PROJECT_NAME}-${VERSION}.jar)
SET(JAR_DIR ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}-${VERSION}-jar)
SET(MANIFESET_SRC ${CMAKE_CURRENT_SOURCE_DIR}/src/MANIFEST.txt)
SET(BINARY_CLASS_DIR ${CMAKE_CURRENT_BINARY_DIR}/classes)
FILE(MAKE_DIRECTORY ${BINARY_CLASS_DIR})

add_custom_target( ${PROJECT_NAME}.classes
    COMMAND ${JAVA_COMPILE} -d ${BINARY_CLASS_DIR} -classpath "${CLASSPATH}" -Djava.ext.dirs=/opt/android-sdk-mac_x86/tools/lib
            ${JAVA_SOURCE_FILES}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})


add_custom_target( ${JAR_FILE}.classes-in-jar
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${BINARY_CLASS_DIR} ${JAR_DIR}
)

add_custom_target( ${JAR_FILE}.py-in-jar
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${PYTHON_SOURCE_DIR} ${JAR_DIR}
)
                                                                                

add_custom_target( ${JAR_FILE} ALL
   COMMAND ${JAVA_ARCHIVE} cfm ${CMAKE_BINARY_DIR}/${JAR_FILE}
           ${MANIFESET_SRC}
           -C ${JAR_DIR} *
   COMMENT "Preparing and packaging ${JAR_FILE}"
   WORKING_DIRECTORY ${JAR_DIR}
)

add_dependencies(${JAR_FILE} ${JAR_FILE}.classes-in-jar ${JAR_FILE}.py-in-jar)
add_dependencies(${JAR_FILE}.classes-in-jar ${PROJECT_NAME}.classes)


### Python test

SET(PY_TEST_DIR "${BASE_DIR}/src/test/python")
SET(JYTHON_CLASS org.python.util.jython)
SET(JACOCOAGENT_JAR ${BASE_DIR}/../../utils/jacoco/lib/jacocoagent.jar)
IF(WIN32)
   STRING(REPLACE "/" "\\" JACOCOAGENT_JAR "${JACOCOAGENT_JAR}")
ENDIF(WIN32)
SET(JACOCO_ARGS
   "-javaagent:${JACOCOAGENT_JAR}=destfile=jacoco.exec,includes=org.sikuli.*"
)

SET(NOSE_ARGS
   ""
   CACHE STRING 
   "The arguments for running Python nose tests")

add_custom_target( python-test
   COMMAND ${JAVA_RUNTIME} ${JAVA_ARGS} -Dfile.encoding=UTF-8 -Dsikuli.testing=yes -cp "${CLASSPATH}${SEP}${CMAKE_BINARY_DIR}/${JAR_FILE}" ${MONKEY_RUNNER_CLASS} ${PY_TEST_DIR}/RunNoseTests.py ${NOSE_ARGS}
   COMMENT "Run Python unit tests"
)

add_dependencies( python-test
   ${JAR_FILE}
   ${PROJECT_NAME}.test.classes
   test-res
)

# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
cmake_minimum_required(VERSION 2.6)
PROJECT (sikuli-script Java)

ENABLE_TESTING()

SET(CMAKE_VERBOSE_MAKEFILE 0)
SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../cmake_modules/")
INCLUDE("${CMAKE_MODULE_PATH}/common.cmake")

FIND_PACKAGE(Java 1.6 REQUIRED)
SET( NON_NATIVE_TARGET TRUE)

IF(UNIX)
   SET(SEP ":")
ELSE()
   SET(SEP ";")
ENDIF()

SET(NATIVE_PACKAGE org.sikuli.script.natives)
STRING(REPLACE . / NATIVE_PACKAGE_DIR ${NATIVE_PACKAGE})

SET(PACKAGE org.sikuli.script)
STRING(REPLACE . / PACKAGE_DIR ${PACKAGE})

SET(BASE_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
SET(DOC_DIR "${BASE_DIR}/doc")
SET(JAVA_SRC_DIR "${BASE_DIR}/src/main/java")
SET(NATIVE_SRC_DIR "${BASE_DIR}/src/main/native")
SET(PYTHON_SRC_DIR "${BASE_DIR}/src/main/python")
SET(JAVA_TEST_DIR "${BASE_DIR}/src/test/java")
SET(TEST_RESOURCE_DIR "test-res")
SET(JAVA_TEST_FOR_HUDSON_XML "../hudson-test.xml")

SET(CMAKE_SWIG_OUTDIR ${CMAKE_BINARY_DIR}/${NATIVE_PACKAGE_DIR})

SET(COMMON_LIB_DIR "${BASE_DIR}/../lib")

# dependent libs
SET(JXGRABKEY_JAR "${COMMON_LIB_DIR}/jxgrabkey/lib/JXGrabKey.jar")
SET(JINTELLITYPE_JAR "${COMMON_LIB_DIR}/jintellitype-1.3.6/jintellitype-1.3.6.jar")

#SET(INCLUDE_LIB_DIR "${BASE_DIR}/lib")
SET(INCLUDE_LIB_DIR "${COMMON_LIB_DIR}/mx-native-loader-1.2/target/classes")

SET(BINARY_DIR "${BASE_DIR}/target")
SET(BINARY_CLASS_DIR "${CMAKE_BINARY_DIR}/classes")
SET(TEST_CLASS_DIR "${CMAKE_BINARY_DIR}/test")
SET(BINARY_LIB_DIR "${BINARY_DIR}/lib")
SET(JAR_DIR "${CMAKE_BINARY_DIR}/jar")
SET(JAR_LIB_DIR  ${JAR_DIR}/META-INF/lib)

SET(JUNIT_JAR "${COMMON_LIB_DIR}/junit-4.8.2.jar")
SET(MOCKITO_JAR "${COMMON_LIB_DIR}/mockito-all-1.8.5.jar")
SET(JYTHON_JAR "${COMMON_LIB_DIR}/jython-2.5.2.jar")
SET(JYTHON_LIB_FILE "${COMMON_LIB_DIR}/jython-lib-2.5.2.tgz")

SET(CLASSPATH 
   ${INCLUDE_LIB_DIR}${SEP}${JYTHON_JAR}${SEP}.
)

IF(LINUX)
   SET(CLASSPATH ${CLASSPATH}${SEP}${JXGRABKEY_JAR})
ELSEIF(WIN32)
   SET(CLASSPATH ${CLASSPATH}${SEP}${JINTELLITYPE_JAR})
ENDIF()


SET(TEST_CLASSPATH
   ${CLASSPATH}${SEP}${JUNIT_JAR}${SEP}${BINARY_CLASS_DIR}${SEP}${MOCKITO_JAR}
)

FILE(MAKE_DIRECTORY ${BINARY_DIR})
FILE(MAKE_DIRECTORY ${BINARY_CLASS_DIR})
FILE(MAKE_DIRECTORY ${TEST_CLASS_DIR})
FILE(MAKE_DIRECTORY ${JAR_DIR})

SET(JAR_FILE sikuli-script.jar)

add_custom_target( ${JAR_FILE} ALL
   COMMAND ${JAVA_ARCHIVE} cfm ${BINARY_DIR}/${JAR_FILE}
           ${BINARY_DIR}/MANIFEST.txt -C ${JAR_DIR} *
   COMMAND ${CMAKE_COMMAND} -E copy ${BINARY_DIR}/${JAR_FILE} ${COMMON_LIB_DIR}/${JAR_FILE}
   COMMENT "Preparing and packaging ${JAR_FILE}"
   WORKING_DIRECTORY ${JAR_DIR}
)

add_custom_target( ${JAR_FILE}.includes-in-jar 
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${INCLUDE_LIB_DIR} ${JAR_DIR}
)

IF(NOT EXISTS ${JAR_DIR}/org/python)
   add_custom_target( ${JAR_FILE}.jython-in-jar
      COMMAND ${CMAKE_COMMAND} -E chdir ${JAR_DIR} ${JAVA_ARCHIVE} xf ${JYTHON_JAR}
      COMMENT "Merging Jython's JAR"
   )
ENDIF()


SET(LIB_IN_JAR_DIR "${JAR_DIR}/Lib")
add_custom_target( ${JAR_FILE}.python-src-in-jar
   COMMAND ${CMAKE_COMMAND} -E make_directory ${LIB_IN_JAR_DIR}
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${PYTHON_SRC_DIR} ${LIB_IN_JAR_DIR}
   COMMENT "Copying Sikuli's Python Source"
)

IF(NOT EXISTS ${LIB_IN_JAR_DIR}/glob.py)
   add_custom_target( ${JAR_FILE}.jython-libs-in-jar
      COMMAND ${CMAKE_COMMAND} -E make_directory ${LIB_IN_JAR_DIR}
      COMMAND ${CMAKE_COMMAND} -E chdir ${LIB_IN_JAR_DIR} 
              ${CMAKE_COMMAND} -E tar xzf ${JYTHON_LIB_FILE}
      COMMENT "Copying Jython Libraries"
   )
ENDIF()

SET(TESSDATA_IN_JAR_DIR "${JAR_DIR}/tessdata")
SET(TESSDATA_LIST_TXT "${BINARY_DIR}/tessdata-list.txt")
IF(NOT EXISTS ${TESSDATA_IN_JAR_DIR}/filelist.txt)
   add_custom_target( ${JAR_FILE}.tessdata-in-jar
      COMMAND ${CMAKE_COMMAND} -E make_directory ${TESSDATA_IN_JAR_DIR}
      COMMAND ${CMAKE_COMMAND} -E copy_directory ${TESSERACT_DATA_DIR} ${TESSDATA_IN_JAR_DIR}
      COMMAND ${CMAKE_COMMAND} -E copy_if_different ${TESSDATA_LIST_TXT} ${TESSDATA_IN_JAR_DIR}/filelist.txt
      #      COMMAND cd ${TESSDATA_IN_JAR_DIR} && (find . -type f > filelist.txt)
      COMMENT "Copying Tesseract Data"
   )
ENDIF()

add_custom_target(doc
   javadoc -public -docencoding utf-8 -windowtitle "Javadoc of Sikuli Script" -sourcepath ${JAVA_SRC_DIR}
    -d ${DOC_DIR} -classpath ${CLASSPATH}  ${PACKAGE}
)

SET(RUN_SCRIPT_ARGS 
   "-i"
   CACHE STRING 
   "The arguments for running Sikuli Script with 'make run-script'")
add_custom_target(run-script
   java -jar ${BINARY_DIR}/${JAR_FILE} ${RUN_SCRIPT_ARGS}
)

file(READ ${BASE_DIR}/../sikuli-ide/latest-ver VERSION)
STRING(STRIP ${VERSION} VERSION)

add_custom_target( update-version
      COMMAND ${BASE_DIR}/../utils/update-version.sh  ${JAVA_SRC_DIR}/org/sikuli/script/Env.java "${VERSION}"
      COMMENT "Update version number to ${VERSION}"
)

add_custom_target(${JAR_FILE}.libs-in-jar
   COMMAND ${CMAKE_COMMAND} -E make_directory ${JAR_DIR}/META-INF
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${BINARY_LIB_DIR} ${JAR_LIB_DIR}
)


# Dependencies

add_dependencies(${JAR_FILE} 
        ${JAR_FILE}.classes-in-jar 
        ${JAR_FILE}.python-src-in-jar
        ${JAR_FILE}.tessdata-in-jar
        ${JAR_FILE}.libs-in-jar
        ${JAR_FILE}.includes-in-jar
        ${JAR_FILE}.jython-in-jar
        ${JAR_FILE}.jython-libs-in-jar
)

IF(WIN32)
   IF(NOT EXISTS ${JAR_DIR}/com/melloware/jintellitype)
      add_custom_target( ${JAR_FILE}.jintellitype-in-jar
         COMMAND ${CMAKE_COMMAND} -E chdir ${JAR_DIR} ${JAVA_ARCHIVE} xf ${JINTELLITYPE_JAR}
         COMMENT "Merging Jintellitype's JAR"
      )
      add_dependencies(${JAR_FILE} 
         ${JAR_FILE}.jintellitype-in-jar
      )

      SET(LIB_GRABKEY "${COMMON_LIB_DIR}/jintellitype-1.3.6/JIntellitype.dll")
      SET(LIB_GRABKEY64 "${COMMON_LIB_DIR}/jintellitype-1.3.6/JIntellitype64.dll")
      add_custom_target( ${JAR_FILE}.hotkey-lib 
         COMMAND ${CMAKE_COMMAND} -E copy ${LIB_GRABKEY} ${JAR_DIR}/com/melloware/jintellitype/
         COMMAND ${CMAKE_COMMAND} -E copy ${LIB_GRABKEY64} ${JAR_DIR}/com/melloware/jintellitype/
         COMMENT "Packaging JIntellitype in Jar"
      )
      add_dependencies( ${JAR_FILE}.hotkey-lib
         ${JAR_FILE}.jintellitype-in-jar 
      )
   ENDIF(NOT EXISTS ${JAR_DIR}/com/melloware/jintellitype)

ENDIF(WIN32)

IF(LINUX)
   IF(NOT EXISTS ${JAR_DIR}/jxgrabkey)
      add_custom_target( ${JAR_FILE}.jxgrabkey-in-jar
         COMMAND ${CMAKE_COMMAND} -E chdir ${JAR_DIR} ${JAVA_ARCHIVE} xf ${JXGRABKEY_JAR}
         COMMENT "Merging JXGrabKey's JAR"
      )
   ENDIF()

   add_dependencies(${JAR_FILE} 
      ${JAR_FILE}.jxgrabkey-in-jar
   )
ENDIF(LINUX)

IF(LINUX)
   EXEC_PROGRAM(uname ARGS -m OUTPUT_VARIABLE SYSTEM_ARCH)
   if(SYSTEM_ARCH MATCHES "x86_64")
      SET(LIB_GRABKEY "${COMMON_LIB_DIR}/jxgrabkey/lib/libJXGrabKey-64.so")
   else()
      SET(LIB_GRABKEY "${COMMON_LIB_DIR}/jxgrabkey/lib/libJXGrabKey-32.so")
   endif()
   add_custom_target( ${JAR_FILE}.hotkey-lib 
      COMMAND cp ${LIB_GRABKEY} ${BINARY_LIB_DIR}/libJXGrabKey.so
      COMMENT "Packaging JXGrabKey in Jar"
   )
ENDIF(LINUX)
add_dependencies( ${JAR_FILE} ${JAR_FILE}.hotkey-lib ) 
add_dependencies( ${JAR_FILE}.libs-in-jar ${JAR_FILE}.hotkey-lib ) 


ADD_SUBDIRECTORY(${JAVA_SRC_DIR})
ADD_SUBDIRECTORY(${NATIVE_SRC_DIR})
ADD_SUBDIRECTORY(${JAVA_TEST_DIR})

# Java level tests

SET(MAIN_TEST_SUITE ${PACKAGE}.SikuliScriptTestSuite)

add_custom_target( test-res
   COMMAND ${CMAKE_COMMAND} -E remove_directory ${CMAKE_BINARY_DIR}/${TEST_RESOURCE_DIR}
   COMMAND ${CMAKE_COMMAND} -E copy_directory ${BASE_DIR}/${TEST_RESOURCE_DIR} ${CMAKE_BINARY_DIR}/${TEST_RESOURCE_DIR}
   COMMENT "Copy test resource"
)

SET(JAVA_TEST_CASE ""
   CACHE STRING
   "The java test case to run with run-java-test"
   )

add_custom_target( run-java-test
   COMMAND ant -f ${JAVA_TEST_FOR_HUDSON_XML} runtest -Dtest=${JAVA_TEST_CASE}
   COMMENT "Run specific Java unit tests"
)

add_dependencies( run-java-test
        ${JAR_FILE}
        ${PROJECT_NAME}.test.classes
        test-res
)

add_custom_target( java-test
   COMMAND ant -f ${JAVA_TEST_FOR_HUDSON_XML}
   COMMENT "Run Java unit tests"
)

add_dependencies( java-test
        ${JAR_FILE}
        ${PROJECT_NAME}.test.classes
        test-res
)


## Python level tests

SET(PY_TEST_DIR "${BASE_DIR}/src/test/python")
SET(JYTHON_CLASS org.python.util.jython)
SET(JACOCOAGENT_JAR ${BASE_DIR}/../utils/jacoco/lib/jacocoagent.jar)
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
   COMMAND ${JAVA_RUNTIME} "${JACOCO_ARGS}"  -Dfile.encoding=UTF-8 -Dsikuli.testing=yes -cp "${BINARY_DIR}/${JAR_FILE}${SEP}${TEST_CLASS_DIR}" ${JYTHON_CLASS} ${PY_TEST_DIR}/RunNoseTests.py ${NOSE_ARGS}
   COMMENT "Run Python unit tests"
)

add_dependencies( python-test
   ${JAR_FILE}
   ${PROJECT_NAME}.test.classes
   test-res
)


# run all test cases
add_custom_target( all-test
)

add_dependencies( all-test
   java-test
   python-test
)

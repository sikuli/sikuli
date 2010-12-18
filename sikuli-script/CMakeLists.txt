PROJECT (sikuli-script Java)

ENABLE_TESTING()

SET(CMAKE_VERBOSE_MAKEFILE 1)
SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../cmake_modules/")
INCLUDE("${CMAKE_MODULE_PATH}/common.cmake")

FIND_PACKAGE(Java)
SET( NON_NATIVE_TARGET TRUE)

IF(UNIX)
   SET(SEP ":")
ELSE()
   SET(SEP ";")
ENDIF()

SET(PACKAGE org.sikuli.script)
STRING(REPLACE . / PACKAGE_DIR ${PACKAGE})

SET(BASE_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
SET(JAVA_SRC_DIR "${BASE_DIR}/src/main/java")
SET(NATIVE_SRC_DIR "${BASE_DIR}/src/main/native")
SET(PYTHON_SRC_DIR "${BASE_DIR}/src/main/python")
SET(CMAKE_SWIG_OUTDIR ${CMAKE_BINARY_DIR}/${PACKAGE_DIR})

SET(COMMON_LIB_DIR "${BASE_DIR}/../lib")
#SET(INCLUDE_LIB_DIR "${BASE_DIR}/lib")
SET(INCLUDE_LIB_DIR "${COMMON_LIB_DIR}/mx-native-loader-1.2/target/classes")
SET(BINARY_DIR "${BASE_DIR}/target")
SET(BINARY_CLASS_DIR "${BINARY_DIR}/classes")
SET(BINARY_LIB_DIR "${BINARY_DIR}/lib")
SET(JAR_DIR "${BINARY_DIR}/jar")

SET(JYTHON_JAR "${COMMON_LIB_DIR}/jython-2.5.1.jar")
SET(JYTHON_LIB_FILE "${COMMON_LIB_DIR}/jython-lib-2.5.1.tgz")

SET(CLASSPATH 
   ${INCLUDE_LIB_DIR}${SEP}${JYTHON_JAR}${SEP}.
)

FILE(MAKE_DIRECTORY ${BINARY_DIR})
FILE(MAKE_DIRECTORY ${BINARY_CLASS_DIR})
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


SUBDIRS(${NATIVE_SRC_DIR} ${JAVA_SRC_DIR})

# Tests

SET(PYTHON_TESTS
   basic bundle capture click dragdrop env findAll 
   keys mouse multiscreen observe region 
   showActions sikuli type vdict wait with
   ocr imageurl wheel
)

SET(PY_TEST_DIR "${BASE_DIR}/src/test/python")
SET(JYTHON_CLASS org.python.util.jython)

FOREACH(TEST_TARGET ${PYTHON_TESTS})
   ADD_TEST(Jar_${TEST_TARGET} ${CMAKE_COMMAND} -E chdir ${BASE_DIR} 
                  java -Dsikuli.Debug=3 -cp ${BINARY_DIR}/${JAR_FILE} 
                  -Dpython.home=${BINARY_DIR}/${JAR_FILE} 
                  ${JYTHON_CLASS} ${PY_TEST_DIR}/test-${TEST_TARGET}.py
   )
ENDFOREACH(TEST_TARGET)

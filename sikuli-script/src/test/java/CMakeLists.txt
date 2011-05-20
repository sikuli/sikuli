# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.

SET(JAVA_TEST_FILES 
   ${CMAKE_SWIG_OUTDIR}/*.java
   org/sikuli/script/*.java
)

add_custom_target( ${PROJECT_NAME}.test.classes
    COMMAND ${JAVA_COMPILE} -d ${TEST_CLASS_DIR} -classpath "${CLASSPATH}${SEP}${JUNIT_JAR}${SEP}${BINARY_CLASS_DIR}"
            ${JAVA_TEST_FILES}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
     

add_dependencies(${PROJECT_NAME}.test.classes ${PROJECT_NAME}.classes)

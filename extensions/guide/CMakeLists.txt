CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
PROJECT (guide Java)
SET(VERSION 1.2)

SET(PYTHON_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/main/python)
SET(JAVA_SOURCE_FILES    src/main/java/org/sikuli/guide/*.java)

SET(JAR_FILE ${PROJECT_NAME}-${VERSION}.jar)
SET(JAR_DIR ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}-${VERSION}-jar)
SET(BINARY_CLASS_DIR ${CMAKE_CURRENT_BINARY_DIR}/classes)
FILE(MAKE_DIRECTORY ${BINARY_CLASS_DIR})

add_custom_target( ${PROJECT_NAME}.classes
    COMMAND ${JAVA_COMPILE} -d ${BINARY_CLASS_DIR} -classpath "${CLASSPATH}"
            ${JAVA_SOURCE_FILES}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})


add_custom_target( ${JAR_FILE}.classes-in-jar
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${BINARY_CLASS_DIR} ${JAR_DIR}
)

add_custom_target( ${JAR_FILE}.py-in-jar
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${PYTHON_SOURCE_DIR} ${JAR_DIR}
)
                                                                                

add_custom_target( ${JAR_FILE} ALL
   COMMAND ${JAVA_ARCHIVE} cf ${CMAKE_BINARY_DIR}/${JAR_FILE}
           -C ${JAR_DIR} *
   COMMENT "Preparing and packaging ${JAR_FILE}"
   WORKING_DIRECTORY ${JAR_DIR}
)

add_dependencies(${JAR_FILE} ${JAR_FILE}.classes-in-jar ${JAR_FILE}.py-in-jar)
add_dependencies(${JAR_FILE}.classes-in-jar ${PROJECT_NAME}.classes)

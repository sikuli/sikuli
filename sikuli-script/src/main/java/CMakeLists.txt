PROJECT (sikuli-script Java)

CMAKE_MINIMUM_REQUIRED(VERSION 2.8)

SET(CMAKE_VERBOSE_MAKEFILE 0)

FIND_PACKAGE(Java)
SET( NON_NATIVE_TARGET TRUE)

IF(UNIX)
   SET(SEP ":")
ELSE()
   SET(SEP ";")
ENDIF()


SET(BASE_DIR "${CMAKE_SOURCE_DIR}/../../..")
SET(COMMON_LIB_DIR "${BASE_DIR}/../lib")
SET(BINARY_DIR "${BASE_DIR}/target")
SET(INCLUDE_LIB_DIR "${BASE_DIR}/lib")
SET(CLASS_DIR "${BINARY_DIR}/classes")
SET(JAR_DIR "${BINARY_DIR}/jar")

SET(JYTHON_JAR "${COMMON_LIB_DIR}/jython-2.5.1.jar")

SET(CLASSPATH 
   ${INCLUDE_LIB_DIR}${SEP}${JYTHON_JAR}${SEP}.
)

FILE(MAKE_DIRECTORY ${CLASS_DIR})
FILE(MAKE_DIRECTORY ${JAR_DIR})

SET(JAR_FILE sikuli-script.jar)
SET(JAVA_SOURCE_FILES 
   edu/mit/csail/uid/AppearEvent.java
   edu/mit/csail/uid/CapturePrompt.java
   edu/mit/csail/uid/ChangeEvent.java
   edu/mit/csail/uid/Clipboard.java
   edu/mit/csail/uid/Debug.java
   edu/mit/csail/uid/DummyOSUtil.java
   edu/mit/csail/uid/Env.java
   edu/mit/csail/uid/EventManager.java
   edu/mit/csail/uid/FindFailed.java
   edu/mit/csail/uid/Finder.java
   edu/mit/csail/uid/KeyCodeConverter.java
   edu/mit/csail/uid/LinuxUtil.java
   edu/mit/csail/uid/Location.java
   edu/mit/csail/uid/MacUtil.java
   edu/mit/csail/uid/Match.java
   edu/mit/csail/uid/OS.java
   edu/mit/csail/uid/OSUtil.java
   edu/mit/csail/uid/Observer.java
   edu/mit/csail/uid/OpenCV.java
   edu/mit/csail/uid/OverlayWindow.java
   edu/mit/csail/uid/Pattern.java
   edu/mit/csail/uid/Region.java
   edu/mit/csail/uid/Screen.java
   edu/mit/csail/uid/ScreenImage.java
   edu/mit/csail/uid/ScriptRunner.java
   edu/mit/csail/uid/Settings.java
   edu/mit/csail/uid/SikuliEvent.java
   edu/mit/csail/uid/SikuliEventAdapter.java
   edu/mit/csail/uid/SikuliEventObserver.java
   edu/mit/csail/uid/SikuliScript.java
   edu/mit/csail/uid/Subject.java
   edu/mit/csail/uid/UnionScreen.java
   edu/mit/csail/uid/VDictProxy.java
   edu/mit/csail/uid/VanishEvent.java
   edu/mit/csail/uid/Win32Util.java
)

add_custom_target( ${PROJECT_NAME}.jar.classes
    COMMAND ${JAVA_COMPILE} -d ${CLASS_DIR} -classpath ${CLASSPATH}
            ${JAVA_SOURCE_FILES}
    WORKING_DIRECTORY ${CMAKE_SOURCE_DIR})
     
add_custom_target( ${JAR_FILE} ALL
   COMMAND ${JAVA_ARCHIVE} cfm ${BINARY_DIR}/${JAR_FILE}
           ${BINARY_DIR}/MANIFEST.txt -C ${JAR_DIR} *
   WORKING_DIRECTORY ${JAR_DIR})

# Dependencies

add_dependencies( ${JAR_FILE}
                  ${PROJECT_NAME}.jar.classes)


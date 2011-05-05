# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
IF(APPLE)
   SET(OS_EXTRA_SOURCE_FILES org/sikuli/script/MacUtil.java)
ELSEIF(LINUX)
   SET(OS_EXTRA_SOURCE_FILES org/sikuli/script/LinuxUtil.java)
ELSEIF(WIN32)
   SET(OS_EXTRA_SOURCE_FILES org/sikuli/script/Win32Util.java)
ENDIF()


SET(JAVA_SOURCE_FILES 
   ${CMAKE_SWIG_OUTDIR}/*.java
   ${OS_EXTRA_SOURCE_FILES}
   org/sikuli/script/AppearEvent.java
   org/sikuli/script/CapturePrompt.java
   org/sikuli/script/ChangeEvent.java
   org/sikuli/script/Clipboard.java
   org/sikuli/script/Debug.java
   org/sikuli/script/DummyOSUtil.java
   org/sikuli/script/Env.java
   org/sikuli/script/EventManager.java
   org/sikuli/script/FindFailed.java
   org/sikuli/script/Finder.java
   org/sikuli/script/KeyCodeConverter.java
   org/sikuli/script/Location.java
   org/sikuli/script/Match.java
   org/sikuli/script/OS.java
   org/sikuli/script/OSUtil.java
   org/sikuli/script/Observer.java
   org/sikuli/script/OpenCV.java
   org/sikuli/script/ScreenHighlighter.java
   org/sikuli/script/Pattern.java
   org/sikuli/script/Region.java
   org/sikuli/script/Screen.java
   org/sikuli/script/ScreenImage.java
   org/sikuli/script/ScriptRunner.java
   org/sikuli/script/Settings.java
   org/sikuli/script/SikuliEvent.java
   org/sikuli/script/SikuliEventAdapter.java
   org/sikuli/script/SikuliEventObserver.java
   org/sikuli/script/SikuliScript.java
   org/sikuli/script/Subject.java
   org/sikuli/script/UnionScreen.java
   org/sikuli/script/VDictProxy.java
   org/sikuli/script/VanishEvent.java
   org/sikuli/script/App.java
   org/sikuli/script/ImageLocator.java
   org/sikuli/script/ResourceExtractor.java
   org/sikuli/script/SikuliException.java
   org/sikuli/script/AppNotFound.java
   org/sikuli/script/Animator.java
   org/sikuli/script/TransparentWindow.java
   org/sikuli/script/ExtensionManager.java
)

add_custom_target( ${PROJECT_NAME}.classes
    COMMAND ${JAVA_COMPILE} -d ${BINARY_CLASS_DIR} -classpath "${CLASSPATH}"
            ${JAVA_SOURCE_FILES}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
     

add_custom_target( ${JAR_FILE}.classes-in-jar
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${BINARY_CLASS_DIR} ${JAR_DIR}
)

add_dependencies(${JAR_FILE}.classes-in-jar ${PROJECT_NAME}.classes)
add_dependencies(${PROJECT_NAME}.classes VisionProxy)

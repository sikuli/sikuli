IF(APPLE)
   SET(OS_EXTRA_SOURCE_FILES edu/mit/csail/uid/NativeLayerForMac.java)
ELSEIF(LINUX)
   SET(OS_EXTRA_SOURCE_FILES edu/mit/csail/uid/NativeLayerForLinux.java)
ELSEIF(WINDOWS)
   SET(OS_EXTRA_SOURCE_FILES edu/mit/csail/uid/NativeLayerForWindows.java)
ENDIF()

SET(JAVA_SOURCE_FILES 
         ${OS_EXTRA_SOURCE_FILES}
         edu/mit/csail/uid/AutoUpdater.java
         edu/mit/csail/uid/ButtonGenCommand.java
         edu/mit/csail/uid/CaptureButton.java
         edu/mit/csail/uid/CloseableTabbedPane.java
         edu/mit/csail/uid/CloseableTabbedPaneListener.java
         edu/mit/csail/uid/CommandArgs.java
         edu/mit/csail/uid/ConsolePane.java
         edu/mit/csail/uid/CurrentLineHighlighter.java
         edu/mit/csail/uid/FileChooser.java
         edu/mit/csail/uid/I18N.java
         edu/mit/csail/uid/IDESettings.java
         edu/mit/csail/uid/ImageButton.java
         edu/mit/csail/uid/LineNumberView.java
         edu/mit/csail/uid/MyDocument.java
         edu/mit/csail/uid/NativeLayer.java
         edu/mit/csail/uid/Observer.java
         edu/mit/csail/uid/PatternWindow.java
         edu/mit/csail/uid/PreferencesWin.java
         edu/mit/csail/uid/RegionButton.java
         edu/mit/csail/uid/ScreenshotPane.java
         edu/mit/csail/uid/sikuli_test/CounterPanel.java
         edu/mit/csail/uid/sikuli_test/DefaultFailureDetailView.java
         edu/mit/csail/uid/sikuli_test/FailureRunView.java
         edu/mit/csail/uid/sikuli_test/ProgressBar.java
         edu/mit/csail/uid/sikuli_test/TestHierarchyRunView.java
         edu/mit/csail/uid/sikuli_test/TestRunContext.java
         edu/mit/csail/uid/sikuli_test/TestRunView.java
         edu/mit/csail/uid/sikuli_test/TestSuitePanel.java
         edu/mit/csail/uid/sikuli_test/TestTreeModel.java
         edu/mit/csail/uid/sikuli_test/TextUnitTestRunner.java
         edu/mit/csail/uid/sikuli_test/UnitTestRunner.java
         edu/mit/csail/uid/SikuliDocument.java
         edu/mit/csail/uid/SikuliEditorKit.java
         edu/mit/csail/uid/SikuliIDE.java
         edu/mit/csail/uid/SikuliPane.java
         edu/mit/csail/uid/SikuliView.java
         edu/mit/csail/uid/SikuliViewFactory.java
         edu/mit/csail/uid/SimilaritySlider.java
         edu/mit/csail/uid/StatusBar.java
         edu/mit/csail/uid/Subject.java
         edu/mit/csail/uid/TargetOffsetPane.java
         edu/mit/csail/uid/UserPreferences.java
         edu/mit/csail/uid/Utils.java
)

add_custom_target( ${PROJECT_NAME}.classes
    COMMAND ${JAVA_COMPILE} -d ${BINARY_CLASS_DIR} -classpath ${CLASSPATH}
            ${JAVA_SOURCE_FILES}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
     

add_custom_target( ${JAR_FILE}.classes-in-jar
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${BINARY_CLASS_DIR} ${JAR_DIR}
)

add_dependencies(${JAR_FILE}.classes-in-jar ${PROJECT_NAME}.classes)

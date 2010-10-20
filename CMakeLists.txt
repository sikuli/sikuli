CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
PROJECT (sikuli)

SET(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake_modules/")
SET(OpenCV_DIR "/opt/opencv/share/opencv") #REMOVE ME

SUBDIRS(sikuli-script sikuli-ide)


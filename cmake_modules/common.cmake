SET(CMAKE_BUILD_TYPE Release) # Release | Debug
SET(CMAKE_VERBOSE_MAKEFILE 1)
SET(CMAKE_OSX_ARCHITECTURES i386 x86_64)

FIND_PATH(OpenCV_DIR OpenCVConfig.cmake
   "/opt/opencv/share/opencv"
   "c:/OpenCV2.1"
)

FIND_PATH(TESSERACT_DATA_DIR confsets 
   "/opt/local/share/tessdata"
   "/usr/local/share/tessdata"
   "/usr/share/tesseract-ocr/tessdata"
   "/usr/share/tessdata"
   "c:/tesseract-2.04/tessdata"
)

IF(WIN32)
   FIND_PATH(TESSERACT_SRC_DIR ccmain
      "c:/tesseract-2.04"
   )

ENDIF()

message("Tesseract-OCR Data Path: ${TESSERACT_DATA_DIR}")
#message("OpenCV Path: ${OpenCV_DIR}")

IF(UNIX AND NOT APPLE)
   SET(LINUX 1)
ENDIF()

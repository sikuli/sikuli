FIND_PATH(OpenCV_DIR OpenCVConfig.cmake
   "/opt/opencv/share/opencv"
)

FIND_PATH(TESSERACT_DATA_DIR confsets 
   "/opt/local/share/tessdata"
   "/usr/share/tesseract-ocr/tessdata"
)
#message("Tesseract-OCR Data Path: ${TESSERACT_DATA_DIR}")

IF(UNIX AND NOT APPLE)
   SET(LINUX 1)
ENDIF()

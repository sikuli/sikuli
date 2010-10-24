SET(OpenCV_DIR "/opt/opencv/share/opencv") #REMOVE ME
#SET(TESSERACT_DATA_DIR "/opt/local/share/tessdata") #REMOVE ME
SET(TESSERACT_DATA_DIR "/usr/share/tesseract-ocr/tessdata") #REMOVE ME

IF(UNIX AND NOT APPLE)
   SET(LINUX 1)
ENDIF()

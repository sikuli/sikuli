# - Try to find Tesseract-OCR 3.00 (not for 2.04)
# Once done, this will define
#
#  Tesseract_FOUND - system has Tesseract
#  Tesseract_INCLUDE_DIRS - the Tesseract include directories
#  Tesseract_LIBRARIES - link these to use Tesseract

find_path(Tesseract_DIR "include/tesseract/baseapi.h" DOC "Root directory of Tesseract-OCR")

include(LibFindMacros)

# Use pkg-config to get hints about paths
libfind_pkg_check_modules(Tesseract_PKGCONF Tesseract)

# Include dir
find_path(Tesseract_INCLUDE_DIR
  NAMES tesseract/baseapi.h
  HINTS "${Tesseract_DIR}/include"
        "/usr/include"
        "/usr/local/include"
        ${Tesseract_PKGCONF_INCLUDE_DIRS}
)

SET(TESSERACT_COMPONENTS 
   api cutil textord ccstruct dict training ccutil 
   image viewer classify main wordrec
)

FOREACH(LIB ${TESSERACT_COMPONENTS})
   find_library(Tesseract_${LIB}_LIBRARY
      NAMES   libtesseract_${LIB}.a
      HINTS "${Tesseract_DIR}/lib"
      "/usr/lib"
      "/usr/local/lib"
      ${Tesseract_PKGCONF_LIBRARY_DIRS}
   )

   set(Tesseract_LIBRARY ${Tesseract_LIBRARY} ${Tesseract_${LIB}_LIBRARY})
ENDFOREACH(LIB ${TESSERACT_COMPONENTS})


# Set the include dir variables and the libraries and let libfind_process do the rest.
# NOTE: Singular variables for this library, plural for libraries this this lib depends on.
set(Tesseract_PROCESS_INCLUDES Tesseract_INCLUDE_DIR Tesseract_INCLUDE_DIRS)
set(Tesseract_PROCESS_LIBS Tesseract_LIBRARY Tesseract_LIBRARIES)
libfind_process(Tesseract)


# - Try to find Tesseract-OCR
# Once done, this will define
#
#  Tesseract_FOUND - system has Tesseract
#  Tesseract_INCLUDE_DIRS - the Tesseract include directories
#  Tesseract_LIBRARIES - link these to use Tesseract

include(LibFindMacros)

# Dependencies
#libfind_package(Tesseract Magick)

# Use pkg-config to get hints about paths
libfind_pkg_check_modules(Tesseract_PKGCONF Tesseract)

# Include dir
find_path(Tesseract_INCLUDE_DIR
  NAMES tesseract/baseapi.h
  PATHS ${Tesseract_PKGCONF_INCLUDE_DIRS}
)

# Finally the library itself
find_library(Tesseract_LIBRARY
  NAMES tesseract_full
  PATHS ${Tesseract_PKGCONF_LIBRARY_DIRS}
)

find_library(Tesseract_LIBRARY
  NAMES tesseract_main
  PATHS ${Tesseract_PKGCONF_LIBRARY_DIRS}
)

# Set the include dir variables and the libraries and let libfind_process do the rest.
# NOTE: Singular variables for this library, plural for libraries this this lib depends on.
set(Tesseract_PROCESS_INCLUDES Tesseract_INCLUDE_DIR Tesseract_INCLUDE_DIRS)
set(Tesseract_PROCESS_LIBS Tesseract_LIBRARY Tesseract_LIBRARIES)
libfind_process(Tesseract)


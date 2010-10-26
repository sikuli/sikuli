#include "sikuli-debug.h"


int sikuli::OCR_DEBUG_LEVEL = 0;
int sikuli::FINDER_DEBUG_LEVEL = 0;

void sikuli::setDebug(DebugCategories cat, int level){
   using namespace sikuli;
   switch(cat){
      case OCR:
         OCR_DEBUG_LEVEL = level;
         return;
      case FINDER:
         FINDER_DEBUG_LEVEL = level;
         return;
   }
}

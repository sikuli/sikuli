#ifndef _SIKULU_DEBUG_H
#define _SIKULI_DEBUG_H

namespace sikuli{
   enum DebugCategories {
      OCR, FINDER
   };
   void setDebug(DebugCategories cat, int level);

   extern int OCR_DEBUG_LEVEL;
   extern int FINDER_DEBUG_LEVEL;
}
#endif

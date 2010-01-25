#ifndef _OCRMATCHER_H_
#define _OCRMATCHER_H_

#include <string>
#include <cxcore.h>

#include "generic-matcher.h"
#include "myocr.h"

using namespace std;

class OCRMatcher : public GenericMatcher {
public:

  OCRMatcher(OCRResult& _result, const string& _target);
  ~OCRMatcher();

  virtual Match next();

private:

  CvMat* scores;  
  
  string target;
  string ocr;

  OCRResult& ocr_result;

};


Matches match_by_ocr(const char* screen_image_filename, 
                     const char* target_string, 
                     int max_num_matches=10, 
                     double min_similarity_threshold=0.7,
                     int x=0, int y=0, int w=0, int h=0,
                     bool write_images=false, bool display_images=false);

#endif // _OCRMATCHER_H_

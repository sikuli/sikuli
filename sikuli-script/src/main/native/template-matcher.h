#ifndef _TEMPLATEMATCHER_H_
#define _TEMPLATEMATCHER_H_

#include <string>
#include <cxcore.h>

#include "generic-matcher.h"
#include "myocr.h"

using namespace std;

class TemplateMatcher : public GenericMatcher{

public:

  TemplateMatcher(){};
  TemplateMatcher(IplImage *img, IplImage *tpl);
  ~TemplateMatcher();

  virtual Match next();
  bool more();

protected:

  IplImage  *img_;
  IplImage  *tpl_;
  IplImage  *res_;
  float threshold_;

  void init(IplImage *img, IplImage *tpl);

};



class DownsampleTemplateMatcher : public TemplateMatcher {

public:

  DownsampleTemplateMatcher(IplImage *img, IplImage *tpl, float downsample_ratio=1.0);
  ~DownsampleTemplateMatcher();

  virtual Match next();

private:

  IplImage *original_img_;
  IplImage *original_tpl_;
  float downsample_ratio_;

};

class LookaheadTemplateMatcher : public DownsampleTemplateMatcher{

public:
  LookaheadTemplateMatcher(IplImage *img, IplImage *tpl, float downsample_ratio=1.0);
  ~LookaheadTemplateMatcher();
  virtual Match next();

private:

  vector<Match> top_matches_;

};



Matches match_by_template(const char* screen_image_filename, 
                          const char *template_image_filename,
                          int max_num_matches, 
                          double min_similarity_threshold,
                          bool search_multiscale,
                          int x, int y, int w, int h,
                          bool write_images, bool display_images);



#endif // _TEMPLATEMATCHER_H_
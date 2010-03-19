#ifndef _FINDER_H_
#define _FINDER_H_

#include <string>
#include <cxcore.h>
#include <cv.h>

#include "template-matcher.h"

class BaseFinder{

public:

  BaseFinder(const IplImage* screen_image);
  BaseFinder(const char* screen_image_filename);
  ~BaseFinder();

  void setROI(int x, int y, int w, int h);

  int get_screen_height() const { return img->height;};
  int get_screen_width()  const {return img->width;};

  // public debug functions
  void debug(float debug_or_not) {is_debug = debug_or_not;};
  void debug_show_image();
  void debug_save_image(const char* output_image_filename);

  void find();
 
protected:

  // applicable to the entire life-span of the Finder object
  IplImage  *img;
  IplImage  *roi_img;
  CvRect roi;

  // debug related data/functions
  bool is_debug;
  IplImage *debug_img;

  void debug_draw_match(Match match, int rank);
  void debug_init_image();

};

class Finder : public BaseFinder{

public:

  Finder(const IplImage* screen_image);
  Finder(const char* screen_image_filename);
  ~Finder();
  

  void find(const IplImage* tpl, double min_similarity = 0.0);
  void find(const char *template_image_filename, double min_similarity = 0.0);  

  bool hasNext();
  Match next();

private:

  IplImage  *tpl;

  void find_helper();

  double min_similarity_threshold;
  TemplateMatcher* matcher;

  Match current_match;
  int current_rank;

  double min_similarity;

  // debug related data/functions
  void debug_init_image();

};

class FaceFinder : public BaseFinder {

public:

  FaceFinder(const char* screen_image_filename);
  ~FaceFinder();

  void find();
  bool hasNext();
  Match next();

private:

  CvMemStorage* storage;
  
  static CvHaarClassifierCascade* cascade;
  
  CvSeq* faces;
  int face_i;

};


class ChangeFinder : public BaseFinder {

public:

  ChangeFinder(const char* screen_image_filename);
  ~ChangeFinder();

  void find(IplImage* new_img);
  void find(const char* new_screen_image_filename);
  
  bool hasNext();
  Match next(); 

private:
  
  bool is_identical;

  IplImage *prev_img;
  CvSeq* c;
  CvMemStorage* storage;

};
#endif // _FINDER_H_

#ifndef _OCR_H_
#define _OCR_H_

#include "cv.h"
using namespace cv;


#ifndef _MATCH_
#define _MATCH_

struct Match {
   int x, y;
   int w, h;
   double score;
   Match(){
      x=0;y=0;w=0;h=0;score=-1;
   }
   Match(int _x, int _y, int _w, int _h, double _score){
      x = _x; y = _y;
      w = _w; h = _h;
      score = _score;
   }
};
#endif

struct WordRect : public Rect{	
	WordRect(Rect& r) : Rect(r) {};
	vector<Rect> charRects;
};

void train_by_image(const Mat& trainingImage);
vector<Match> find_word_by_image(const Mat& inputImage, const char word[]);
void test_segment(const Mat& inputImage, const char word[]);


#define DISPLAY_SEGMENT_IMAGE 0
#define DISPLAY_SEGMENT_LINEIMAGE_STEP 0
#define DISPLAY_SEGMENT_LINEIMAGE_RESULT 0
#define DISPLAY_RECOGNIZE 0
#define DISPLAY_WORD_FIND_RESULT 0

#define DISPLAY_TEST_SEGMENT 1

#define DISPLAY_MATCH_WORD 0
#define DISPLAY_MATCH_CHAR 0

#define DISPLAY_FIND_WORD_STEP 0

#define DISPLAY_NUM_TOP_MATCHES 1

#define MIN_CHAR_MATCH_THRESHOLD 0.4

#endif
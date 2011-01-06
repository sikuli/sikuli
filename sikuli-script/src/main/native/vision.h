/*
 *  vision.h
 *  sikuli
 *
 *  Created by Tom Yeh on 8/1/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef _VISION_H_
#define _VISION_H_

#include "find-result.h"
#include "opencv.hpp"
#include "tessocr.h"

using namespace cv;

namespace sikuli {
   
enum TARGET_TYPE{
   TARGET_TYPE_IMAGE,
   TARGET_TYPE_TEXT,
   TARGET_TYPE_BUTTON
};
   
class FindInput{
      
public:
   
   FindInput();
   FindInput(Mat source, Mat target);
   FindInput(Mat source, int target_type, const char* target);
   
   FindInput(const char* source_filename, int target_type, const char* target);
  
   FindInput(Mat source, int target_type);
   FindInput(const char* source_filename, int target_type);
   
   // copy everything in 'other' except for the source image
   FindInput(Mat source, const FindInput other);

   void setSource(const char* source_filename);
   void setTarget(int target_type, const char* target_string);

   void setSource(Mat source);
   void setTarget(Mat target);
  
   Mat getSourceMat();
   Mat getTargetMat();
      
   void setFindAll(bool all);
   bool isFindingAll();

   void setLimit(int limit);
   int getLimit();
   
   void setSimilarity(double similarity);
   double getSimilarity();
     
   int getTargetType();
   
   std::string getTargetText();
   
private:
   
   void init();
      
   Mat source;
   Mat target;
   std::string target_text;
   
   int limit;
   double similarity;
   int target_type;
   
   int ordering;
   int position;
   
   bool bFindingAll;
};

class Vision{
public:
      
   static vector<FindResult> find(FindInput q);
   
   static double compare(cv::Mat m1, cv::Mat m2);
   
   static void initOCR(const char* ocrDataPath);
   
   
   static string query(const char* index_filename, cv::Mat image);
   
   static OCRText recognize_as_ocrtext(cv::Mat image);
      
   static std::string recognize(cv::Mat image);

   //helper functions
   static cv::Mat createMat(int _rows, int _cols, unsigned char* _data);

private:   
      
};

}

#endif // _VISION_H_

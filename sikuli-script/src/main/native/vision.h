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

using namespace cv;

namespace sikuli {
   
class FindInput{
      
public:
   
   FindInput(Mat source, Mat target);
   FindInput(Mat source, const char* target, bool text = false);
   FindInput(const char* source_filename, const char* target, bool text = false);

   Mat getSourceMat();
   Mat getTargetMat();
      
   void setFindAll(bool all);
      
   bool isFindingAll();
   bool isFindingText();

   void setLimit(int limit);
   int getLimit();
   
   void setSimilarity(double similarity);
   double getSimilarity();
     
   std::string getTargetText();
   
private:
   
   void init(Mat source_, const char* target_string, bool text);
   void init();

      
   Mat source;
   Mat target;
   std::string targetText;
   
   int limit;
   double similarity;
   
   int ordering;
   int position;
   
   bool bFindingAll;
   bool bFindingText;
};

class Vision{
public:
      
   static vector<FindResult> find(FindInput q);
   
   static double compare(cv::Mat m1, cv::Mat m2);
   
   static void initOCR(const char* ocrDataPath);
      
   static std::string recognize(cv::Mat image);
   
};

}

#endif // _VISION_H_

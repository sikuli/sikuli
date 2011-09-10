/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#ifndef _PYRAMID_TEMPLATE_MATCHER_
#define _PYRAMID_TEMPLATE_MATCHER_

#include <stdio.h>
#include <iostream>


#include "opencv.hpp"
#include "find-result.h"
#include "vision.h"
#ifdef ENABLE_GPU
#include <opencv2/gpu/gpu.hpp>
#endif

using namespace cv;
using namespace std;


struct MatchingData {
   Mat source, target;
   Mat source_gray, target_gray;
   Scalar mean, stddev;
   bool use_gray;

   MatchingData(const Mat& source_, const Mat& target_) : source(source_), target(target_){
      use_gray = false;
      meanStdDev( target, mean, stddev );
   }

   bool useGray(){
      return use_gray;
   }

   bool useGray(bool flag){
      use_gray = flag;
      if(use_gray){
         cvtColor(source, source_gray, CV_RGB2GRAY);
         cvtColor(target, target_gray, CV_RGB2GRAY);
      }
      return flag;
   }
};

class PyramidTemplateMatcher{
private:
   bool _use_gpu;
   void init();
public:

   PyramidTemplateMatcher(){
      init();
   }
   PyramidTemplateMatcher(const MatchingData& data, int levels, float factor);
   ~PyramidTemplateMatcher();

   virtual FindResult next();

protected:

   PyramidTemplateMatcher* createSmallMatcher(int level);
   double findBest(const Mat& source, const Mat& target, Mat& out_result, Point& out_location);
   void eraseResult(int x, int y, int xmargin, int ymargin);
   FindResult nextFromLowerPyramid();

   Mat source, target;
   float factor;
   bool _hasMatchedResult;
   double _detectedScore;
   Point  _detectedLoc;

   PyramidTemplateMatcher* lowerPyramid;

   Mat result;

#ifdef ENABLE_GPU
   gpu::GpuMat gResult;
#endif
};

#endif

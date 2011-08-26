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

#define MIN_PIXELS_TO_USE_GPU 90000

class PyramidTemplateMatcher{
private:
   bool _use_gpu;
   void init(){
      _use_gpu = false;
      _hasMatchedResult = false;
      if(sikuli::Vision::getParameter("GPU"))
         _use_gpu = true;
   }
public:

   PyramidTemplateMatcher(){
      init();
   }
   PyramidTemplateMatcher(Mat source, Mat target, int levels, float factor);
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

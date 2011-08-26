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
public:

   PyramidTemplateMatcher() : _use_gpu(false){
      if(sikuli::Vision::getParameter("GPU")){
         _use_gpu = true;
      } 
   };
   PyramidTemplateMatcher(Mat source, Mat target, int levels, float factor);
   ~PyramidTemplateMatcher();

   virtual FindResult next();

protected:

   Mat source;
   Mat target;

   // create copies of the images to modify
   Mat copyOfSource;
   Mat copyOfTarget;	

   int alg;
   float factor;

   PyramidTemplateMatcher* lowerPyramid;
   Mat result;
#ifdef ENABLE_GPU
   gpu::GpuMat gResult;
#endif
};

#endif

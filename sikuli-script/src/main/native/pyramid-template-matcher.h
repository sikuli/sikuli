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

using namespace cv;
using namespace std;

class PyramidTemplateMatcher{
private:
   bool _use_gpu = false;
public:

   PyramidTemplateMatcher(){
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
};

#endif

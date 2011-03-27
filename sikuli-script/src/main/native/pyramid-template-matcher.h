/*
 *  pyramid-template-matcher.h
 *  vision
 *
 *  Created by Tom Yeh on 5/1/10.
 *  Copyright 2010 sikuli.org. All rights reserved.
 *
 */
#ifndef _PYRAMID_TEMPLATE_MATCHER_
#define _PYRAMID_TEMPLATE_MATCHER_

#include <stdio.h>
#include <iostream>


#include "opencv2/opencv.hpp"
#include "find-result.h"

using namespace cv;
using namespace std;

class PyramidTemplateMatcher{

public:

   PyramidTemplateMatcher(){};
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

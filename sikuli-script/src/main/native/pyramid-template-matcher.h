/*
 *  pyramid-template-matcher.h
 *  vision
 *
 *  Created by Tom Yeh on 5/1/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */
#ifndef _PYRAMID_TEMPLATE_MATCHER_
#define _PYRAMID_TEMPLATE_MATCHER_

#include <stdio.h>
#include <iostream>
#include "cv.h"
#include "highgui.h"

using namespace cv;
using namespace std;

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


class PyramidTemplateMatcher{
	
public:
	
	PyramidTemplateMatcher(){};
	PyramidTemplateMatcher(Mat source, Mat target, int levels);
	~PyramidTemplateMatcher();
	
	virtual Match next();
	
protected:
	
	Mat source;
	Mat target;
	
	// create copies of the images to modify
    Mat copyOfSource;
    Mat copyOfTarget;	
	
	PyramidTemplateMatcher* lowerPyramid;
	Mat result;
};

#endif
#ifndef _CV_UTIL_H_
#define _CV_UTIL_H_

/*
 *  cv-util.h
 *  block
 *
 *  Created by Tom Yeh on 5/19/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */
#include "cv.h"
#include "highgui.h"


using namespace cv;

void paste(const Mat& src, Mat& dest, int x, int y);
void add_margin(Rect& r, int margin, Size bound);
void merge(Rect& r1, const Rect& r2);
void print_rect(Rect& r);
void print_matrix(Mat& m);


void 
putTextWithBackground(Scalar fillColor, Mat& img, const string text, Point org, int fontFace, 
                      double fontScale, Scalar color, int thickness=1, 
                      int linetype=8, bool bottomLeftOrigin=false);

void draw_rectangle(Mat& img, Rect& rect, Scalar color = Scalar(0,0,255));

void imshowCompare(const Mat& m1, const Mat& m2, bool pause = true);
void imshowCompareHorizontal(const Mat& m1, const Mat& m2, bool pause = true);
void imshowCompareZoom(const Mat& m1, const Mat& m2, bool pause = true);
void imshowDebug(const char* name, const Mat& m, bool pause = true);
void imshowDebugZoom(const char* name, Mat& m, bool  pause = true);

#endif
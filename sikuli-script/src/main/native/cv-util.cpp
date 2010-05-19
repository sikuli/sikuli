/*
 *  cv-util.cpp
 *  block
 *
 *  Created by Tom Yeh on 5/19/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "cv-util.h"
#include <iostream>

using namespace std;


void imshowCompare(const Mat& m1, const Mat& m2, bool pause){
	Size canvasSize(max(m1.cols, m2.cols), m1.rows + m2.rows);
	Mat canvas(canvasSize, m1.type(),Scalar(0));
	
	Mat d1(canvas,Rect(0,0,m1.cols,m1.rows));
	Mat d2(canvas,Rect(0,m1.rows,m2.cols,m2.rows));
	m1.copyTo(d1);
	m2.copyTo(d2);
	
	namedWindow("compare",CV_WINDOW_AUTOSIZE);
	imshow("compare",canvas);
	if (pause)
		waitKey();
}

void imshowCompareHorizontal(const Mat& m1, const Mat& m2, bool pause){
	Mat canvas(max(m1.rows, m2.rows),m1.cols + m2.cols, m1.type());
	
	Mat d1(canvas,Rect(0,0,m1.cols,m1.rows));
	Mat d2(canvas,Rect(m1.cols,0,m2.cols,m2.rows));
	m1.copyTo(d1);
	m2.copyTo(d2);
	
	namedWindow("compare",CV_WINDOW_AUTOSIZE);
	imshow("compare",canvas);
	if (pause)
		waitKey();
}


void imshowCompareZoom(const Mat& m1, const Mat& m2, bool pause){
	
	Mat large1;
	Mat large2;	
	resize(m1,large1, Size(m1.cols*3,m1.rows*3));
	resize(m2,large2, Size(m2.cols*3,m2.rows*3));
	imshowCompare(large1, large2, pause);
}

void imshowDebug(const char* name, const Mat& m, bool pause){
   imshow(name,m);
	if (pause)
		waitKey();	
}

void imshowDebugZoom(const char* name, Mat& m, bool  pause){
	
	Mat large;
	resize(m,large, Size(m.cols*3,m.rows*3));
   imshow(name,large);
	if (pause)
		waitKey();	
}




void draw_rectangle(Mat& img, Rect& rect, Scalar color){
	Rect& c = rect;
	rectangle(img, 
             Point( c.x, c.y), 
             Point( c.x + c.width, c.y + c.height),
             color, 0.5, 0, 0 );	
}

void paste(const Mat& src, Mat& dest, int x, int y){
	
	int x1 = x;
	int y1 = y;
	int x2 = min(x+src.cols-1, dest.cols-1);
	int y2 = min(y+src.rows-1, dest.rows-1);
	int h = y2-y1+1;
	int w = x2-x1+1;
	Mat destRectImage(dest, Rect(x1,y1,w,h));
	Mat srcRectImage(src, Rect(0,0,w,h));
	
	srcRectImage.copyTo(destRectImage);
}

void add_margin(Rect& r, int margin, Size bound){;
	
	int x1 = r.x;
	int y1 = r.y;
	int x2 = r.x + r.width;
	int y2 = r.y + r.height;
	
	x1 = max(x1-margin,0);
	y1 = max(y1-margin,0);
	x2 = min(x2+margin,bound.width-margin);
	y2 = min(y2+margin,bound.height-margin);
	
	r.x = x1;
	r.y = y1;
	r.width = x2 - x1 + 1;
	r.height = y2 - y1 + 1;
}


void merge(Rect& r1, Rect& r2){
	int x1 = min(r1.x, r2.x);
	int y1 = min(r1.y, r2.y);
	int x2 = max(r1.x+r1.width-1, r2.x+r2.width-1);
	int y2 = max(r1.y+r1.height-1, r2.y+r2.height-1);	
	r1.x = x1;
	r1.y = y1;
	r1.width = x2 - x1 + 1;
	r1.height = y2 - y1 + 1;
   //	return Rect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
}

void print_rect(Rect& r){
	
	cout << "(" << r.x << "," << r.y << ") " << r.width << "x" << r.height << endl; 
}

void print_matrix(Mat& m){
	typedef uchar T;	
	for( int i = 0; i < m.rows; i +=1 )
   {
		T* ptr = m.ptr<T>(i);		
      cout << i << "\t:";
		for( int j = 0; j < m.cols; j += 1 )
      {			
			cout << (int) ptr[j] << '\t';
		}
		cout << endl;
	}
}


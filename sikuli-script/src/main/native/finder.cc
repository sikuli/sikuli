#include <stdio.h>
#include <iostream>
#include "cv.h"
#include "highgui.h"

#include "finder.h"

using namespace cv;
using namespace std;

#define PYRAMID_MIM_TARGET_DIMENSION 12
#define PYRAMIDUP_THRESHOLD 0.9

BaseFinder::BaseFinder(Mat _source) : source(_source){
	roi = Rect(0,0,source.cols,source.rows);
}


BaseFinder::BaseFinder(const char* source_image_filename){
	source = imread(source_image_filename,1);
	roi = Rect(0,0,source.cols,source.rows);
}

BaseFinder::BaseFinder(IplImage*  _source) : source(Mat(_source)){
	roi = Rect(0,0,source.cols,source.rows);
}

BaseFinder::~BaseFinder(){
}

void 
BaseFinder::setROI(int x, int y, int w, int h){
	roi = Rect(x,y,w,h);
}

void
BaseFinder::find(){
	
	// create an ROI image to work on
	roiSource.create(roi.size(),source.type());
	Mat(source,roi).copyTo(roiSource);	
}

//=======================================================================================

Finder::Finder(Mat _source) : BaseFinder(_source){
	matcher = NULL;
}

Finder::Finder(IplImage* _source) : BaseFinder(_source){
	matcher = NULL;
}

Finder::Finder(const char* source_image_filename)
: BaseFinder(source_image_filename){
	matcher = NULL;
}

Finder::~Finder(){
	delete matcher;
}

void 
Finder::find(const char *target_image_filename, double min_similarity){
	Mat target = imread(target_image_filename, 1);
	find(target, min_similarity);
}	

void
Finder::find(Mat target, double min_similarity){
	
	this->min_similarity = min_similarity;
	
	BaseFinder::find();  
	
	current_rank = 1;	
	if (roiSource.cols >= target.cols && roiSource.rows >= target.rows){
		
		// create a template matcher (released in the destructor)
		if (matcher){
			delete matcher;
			matcher = 0;
		}
		
		int levels=-1;
		int w = target.rows;
		int h = target.cols;
		while (w >= PYRAMID_MIM_TARGET_DIMENSION && h >= PYRAMID_MIM_TARGET_DIMENSION){
			w = w / 2;
			h = h / 2;
			levels++;
		}
		
		matcher = new PyramidTemplateMatcher(roiSource, target, levels);
		
		current_match = matcher->next();
		
		if (current_match.score < PYRAMIDUP_THRESHOLD){
			delete matcher;
			matcher = new PyramidTemplateMatcher(roiSource, target, levels - 1);
			current_match = matcher->next();
		}
		
		current_match.x = current_match.x + roi.x;
		current_match.y = current_match.y + roi.y;
		
	}
	else{
		
		current_match.score = -1;
		
	}	
}

bool
Finder::hasNext(){  
	return current_match.score >= min_similarity;
}

Match
Finder::next(){
	Match temp = current_match;
	
	if (hasNext()){
		
		current_match = matcher->next();
		current_match.x = current_match.x + roi.x;
		current_match.y = current_match.y + roi.y;
		current_rank++;
		return temp;
	}else{
		Match match;
		match.score = -1;
		return match;
	}
}

//=========================================================================================

static const char* cascade_name = "haarcascade_frontalface_alt.xml";

CvHaarClassifierCascade* FaceFinder::cascade = 0;

FaceFinder::FaceFinder(const char* screen_image_name)
: BaseFinder(screen_image_name){
	
	//  cascade = 0;
	storage = 0;
	if (!cascade){
		cascade = (CvHaarClassifierCascade*)cvLoad(cascade_name, 0, 0, 0);
	}
	if (!cascade)
	{
		cerr << "can't load the face cascade";
		return;
	}
}

FaceFinder::~FaceFinder(){
	//cvReleaseImage(&img);
	if (cascade)
		cvReleaseHaarClassifierCascade(&cascade);
	if (storage)
		cvReleaseMemStorage(&storage);
}

void 
FaceFinder::find(){
	
	BaseFinder::find();
	
	storage = cvCreateMemStorage(0);
	//faces = cvHaarDetectObjects(roi_img, cascade, storage, 1.1, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(40,40));
	face_i = 0;  
	
	// [DEBUG] reset the debug image to the content of the input image
}


bool
FaceFinder::hasNext(){
	return faces && face_i < faces->total;
}

Match
FaceFinder::next(){
	
	
	
	CvRect* r = (CvRect*)cvGetSeqElem(faces ,face_i);  
	face_i++;
	
	Match match;
	match.x = r->x + roi.x;
	match.y = r->y + roi.y;
	match.w = r->width;
	match.h = r->height;
	
	
	return match;
}

//=====================================================================================
#define PIXEL_DIFF_THRESHOLD 50
#define IMAGE_DIFF_THRESHOLD 20

ChangeFinder::ChangeFinder(const char* screen_image_filename)
: BaseFinder(screen_image_filename){
	is_identical = false;
	storage = 0;
}

ChangeFinder::ChangeFinder(const IplImage* screen_image)
: BaseFinder(screen_image){
	is_identical = false;
	storage = 0;
}


ChangeFinder::ChangeFinder(const Mat screen_image)
: BaseFinder(screen_image){
	is_identical = false;
	storage = 0;
}

ChangeFinder::~ChangeFinder(){
	if (storage)
		cvReleaseMemStorage(&storage);
}


void
ChangeFinder::find(const char* new_screen_image_filename){
	find(imread(new_screen_image_filename,1));
}

void
ChangeFinder::find(IplImage* new_screen_image){
	find(Mat(new_screen_image));
}

void
ChangeFinder::find(Mat new_screen_image){

	BaseFinder::find(); // set ROI
	
	Mat im1 = roiSource;
	Mat im2 = Mat(new_screen_image,roi);
	
	Mat gray1;
	Mat gray2;

	// convert image from RGB to grayscale
	cvtColor(im1, gray1, CV_RGB2GRAY);
	cvtColor(im2, gray2, CV_RGB2GRAY);
	
	Mat diff1;
	absdiff(gray1,gray2,diff1);
	
	typedef float T;
	
	Size size = diff1.size();

	int diff_cnt = 0;
	for( int i = 0; i < size.height; i++ )
    {
        const T* ptr1 = diff1.ptr<T>(i);
        for( int j = 0; j < size.width; j += 4 )
        {			
			if (ptr1[j] > PIXEL_DIFF_THRESHOLD)
				diff_cnt++;
        }
    }
	
	// quickly check if two images are nearly identical
	if (diff_cnt < IMAGE_DIFF_THRESHOLD){
		is_identical = true;
		return;
	}	
	
	
	threshold(diff1,diff1,PIXEL_DIFF_THRESHOLD,255,CV_THRESH_BINARY);
	dilate(diff1,diff1,Mat());

	// close operation
	Mat se = getStructuringElement(MORPH_ELLIPSE, Size(5,5));
	morphologyEx(diff1, diff1, MORPH_CLOSE, se);
	
/*	
	namedWindow("matches", CV_WINDOW_AUTOSIZE);
    imshow("matches", diff1);
	waitKey();	
*/
	
	vector< vector<Point> > contours; 
	vector< Vec4i> hierarchy;
	//findContours(diff1, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point());
	
	storage = cvCreateMemStorage();
	CvSeq* first_contour = NULL;
	
	CvMat mat = (CvMat) diff1;
	
	cvFindContours(
							&mat,
							storage,
							&first_contour,
							sizeof(CvContour),
							CV_RETR_EXTERNAL);
	
	c = first_contour;
}

bool      
ChangeFinder::hasNext(){
	return !is_identical  && c !=NULL;
}

Match
ChangeFinder::next(){ 
	
	// find bounding boxes
    int x1=source.cols;
    int x2=0;
    int y1=source.rows;
    int y2=0;
	
    for( int i=0; i < c->total; ++i ){
		CvPoint* p = CV_GET_SEQ_ELEM( CvPoint, c, i );
		if (p->x > x2)
			x2 = p->x;
		if (p->x < x1)
			x1 = p->x;
		if (p->y > y2)
			y2 = p->y;
		if (p->y < y1)
			y1 = p->y;			
    }
	
    Match m;
    m.x = x1 + roi.x;
    m.y = y1 + roi.y;
    m.w = x2 - x1 + 1;
    m.h = y2 - y1 + 1;
	
    c = c->h_next;
	return m;
}
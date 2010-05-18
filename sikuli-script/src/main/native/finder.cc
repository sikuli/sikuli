#include <stdio.h>
#include <iostream>
#include "cv.h"
#include "highgui.h"

#include "finder.h"
//#include "template-matcher.h"
#include "pyramid-template-matcher.h"
#include "TimingBlock.h"

using namespace cv;
using namespace std;

#define PYRAMID_MIM_TARGET_DIMENSION 6
#define PYRAMID_MIM_TARGET_DIMENSION_ALL 24
#define REMATCH_THRESHOLD 0.9
#define CENTER_REMATCH_THRESHOLD 0.99
#define BORDER_MARGIN 0.2


#ifdef DEBUG
#define dout std::cerr
#else
#define dout if(0) std::cerr
#endif

BaseFinder::BaseFinder(Mat _source) : source(_source){
   roi = Rect(0,0,source.cols,source.rows);
}


BaseFinder::BaseFinder(const char* source_image_filename){
   source = imread(source_image_filename,1);
   roi = Rect(0,0,source.cols,source.rows);
}

BaseFinder::BaseFinder(IplImage*  _source) : source(Mat(_source, true)){
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
Finder::find(IplImage* target, double min_similarity){
   find(Mat(target, false), min_similarity);
}   

void 
Finder::find_all(const char *target_image_filename, double min_similarity){
   Mat target = imread(target_image_filename, 1);
   find_all(target, min_similarity);
}   


void 
Finder::find_all(IplImage* target, double min_similarity){
   find_all(Mat(target, true), min_similarity);
}   

void
Finder::find_all(Mat target, double min_similarity){   
   this->min_similarity = min_similarity;
   BaseFinder::find();  

   current_rank = 1;   
   if (roiSource.cols >= target.cols && roiSource.rows >= target.rows){
      
      // create a template matcher (released in the destructor)
      if (matcher){
         delete matcher;
         matcher = 0;
      }
      
      float factor = 2.0;      
      int levels=-1;
      int w = target.rows;
      int h = target.cols;
      while (w >= PYRAMID_MIM_TARGET_DIMENSION_ALL && h >= PYRAMID_MIM_TARGET_DIMENSION_ALL){
         w = w / factor;
         h = h / factor;
         levels++;
      }
      
      Mat roiSourceGray;
      Mat targetGray;
      
      // convert image from RGB to grayscale
      cvtColor(roiSource, roiSourceGray, CV_RGB2GRAY);
      cvtColor(target, targetGray, CV_RGB2GRAY);
            
      matcher = new PyramidTemplateMatcher(roiSource, target, levels, factor);
      current_match = matcher->next();      
   
      if (current_match.score < max(min_similarity,REMATCH_THRESHOLD)){
         dout << "matching (original resolution: color) ... " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSource, target, 0, 1);
         current_match = matcher->next();
      }
      
      
      current_match.x = current_match.x + roi.x;
      current_match.y = current_match.y + roi.y;
      
   }
   else{
      current_match.score = -1;      
   }   
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

      float ratio;
      ratio = min(target.rows * 1.0 / PYRAMID_MIM_TARGET_DIMENSION, 
               target.cols * 1.0 / PYRAMID_MIM_TARGET_DIMENSION);

      Mat roiSourceGray;
      Mat targetGray;
      
      // convert image from RGB to grayscale
      cvtColor(roiSource, roiSourceGray, CV_RGB2GRAY);
      cvtColor(target, targetGray, CV_RGB2GRAY);

      
      TimingBlock *t;
/*      
      t = new TimingBlock("BASELINE");
      float baseline_ratio;
      baseline_ratio = min(target.rows * 1.0 / 12, 
               target.cols * 1.0 / 12);
      dout << "baseline (old template matcher ) " << endl;
      LookaheadTemplateMatcher lt(&IplImage(roiSource), &IplImage(target), baseline_ratio);
      delete t;
*/
      t = new TimingBlock("NEW METHOD");
      dout << "matching (center) ... " << endl;            
      Mat roiSourceGrayCenter = Mat(roiSourceGray, 
                             Range(roiSourceGray.rows*BORDER_MARGIN,
                                 roiSourceGray.rows*(1-BORDER_MARGIN)),
                             Range(roiSourceGray.cols*BORDER_MARGIN,
                                 roiSourceGray.cols*(1-BORDER_MARGIN)));
      matcher = new PyramidTemplateMatcher(roiSourceGrayCenter, targetGray, 1, ratio);
      
      current_match = matcher->next();

      if (current_match.score < max(min_similarity,CENTER_REMATCH_THRESHOLD)){
         
         dout << "matching (whole) ... " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSourceGray, targetGray, 1, ratio);
         current_match = matcher->next();
         
                                    
      }else{
         current_match.x = current_match.x + roiSourceGray.cols*BORDER_MARGIN;
         current_match.y = current_match.y + roiSourceGray.rows*BORDER_MARGIN;
      }
      if (current_match.score < max(min_similarity,REMATCH_THRESHOLD)){
         dout << "matching (0.75) " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSourceGray, targetGray, 1, ratio*0.75);
         current_match = matcher->next();
      }
      if (current_match.score < max(min_similarity,REMATCH_THRESHOLD) && ratio > 2){
         dout << "matching (0.5) " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSourceGray, targetGray, 1, ratio*0.5);
         current_match = matcher->next();
      }
      if (current_match.score < max(min_similarity,REMATCH_THRESHOLD) && ratio > 4){
         dout << "matching (0.25) " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSourceGray, targetGray, 1, ratio*0.25);
         current_match = matcher->next();
      }      
      if (current_match.score < max(min_similarity,REMATCH_THRESHOLD)){
         dout << "matching (original resolution) ... " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSourceGray, targetGray, 0, 1);
         current_match = matcher->next();
      }
      if (current_match.score < max(min_similarity,REMATCH_THRESHOLD)){
         dout << "matching (original resolution: color) ... " << endl;
         delete matcher;
         matcher = new PyramidTemplateMatcher(roiSource, target, 0, 1);
         current_match = matcher->next();
      }
      
      delete t;
      
      current_match.x = current_match.x + roi.x;
      current_match.y = current_match.y + roi.y;
      
   }
   else{
      
      current_match.score = -1;
      
   }   
}

bool
Finder::hasNext(){  
   return current_match.score >= (min_similarity-0.0000001);
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
   find(Mat(new_screen_image, false));
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

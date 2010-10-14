#include <stdio.h>
#include <iostream>
#include <fstream>
#include "cv.h"
#include "highgui.h"

#include "finder.h"
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


// somwhow after changing it to false works!!
BaseFinder::BaseFinder(IplImage*  _source) : source(Mat(_source, true)){
   roi = Rect(0,0,source.cols,source.rows);
}

BaseFinder::~BaseFinder(){
   dout << "~BaseFinder" << endl;
   /*
   if (img) cvReleaseImage(&img);
   if (roi_img) cvReleaseImage(&roi_img);
   if (debug_img) cvReleaseImage(&debug_img);
   */
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

int Finder::num_matchers = 0;
Finder::Finder(Mat _source) : BaseFinder(_source){
   matcher = NULL;
   wf = NULL;
}

Finder::Finder(IplImage* _source) : BaseFinder(_source){
   matcher = NULL;
   wf = NULL;
}

Finder::Finder(const char* source_image_filename)
: BaseFinder(source_image_filename){
   matcher = NULL;
   wf = NULL; 
}

Finder::~Finder(){
   if (matcher){
      delete matcher;
      num_matchers--;
   }
   
   if (wf)
      delete wf;
}

void 
Finder::find(const char *target_image_filename, double min_similarity){   
   
   const char* p = target_image_filename;
   const char* ext = p + strlen(p) - 3;
   dout << ext;
   if (strncmp(ext,"png",3) != 0){
      // get name after bundle path, which is
      // assumed to be the query word
      int j;
      for (j = (strlen(p)-1); j >=0; j--){      
         if (p[j]=='/')
            break;
      }
      
      Mat im = imread("/arial.png",1);
      WordFinder::train(im);
      wf = new WordFinder(source);
      wf->find(p+j+1,0.6);
      return;
   }
   
   
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
   
   if (roiSource.cols < target.cols || roiSource.rows < target.rows){	   
	   current_match.score = -1;
	   return;
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
         
   create_matcher(roiSourceGray, targetGray, levels, factor);
   add_matches_to_buffer(5);     

   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD))
      return;
   
   
   dout << "[find_all] matching (original resolution: color) ... " << endl;
   create_matcher(roiSource, target, 0, 1);
   add_matches_to_buffer(5);

}


bool sort_by_score(Match m1, Match m2){
   return m1.score > m2.score;
}

float
Finder::top_score_in_buffer(){
   if (buffered_matches.empty())
      return -1;
   else
      return buffered_matches[0].score;
}

void
Finder::create_matcher(Mat& source, Mat& target, int level, float ratio){
   if (matcher){
      num_matchers--;
      delete matcher;
   } 
   matcher = new PyramidTemplateMatcher(source,target,level,ratio);
   num_matchers++;
   dout << "[Memleak debug] # of matchers: " << num_matchers << endl;
}

void 
Finder::add_matches_to_buffer(int num_matches_to_add){ 
   buffered_matches.clear();
   for (int i=0;i<num_matches_to_add;++i){
      Match next_match = matcher->next();
      buffered_matches.push_back(next_match);
   }
   sort(buffered_matches,sort_by_score);
} 

void
Finder::find(Mat target, double min_similarity){
      
   this->min_similarity = min_similarity;   
   BaseFinder::find();  
   
   if (roiSource.cols < target.cols || roiSource.rows < target.rows){	   
	   current_match.score = -1;
	   return;
   }


   float ratio;
   ratio = min(target.rows * 1.0 / PYRAMID_MIM_TARGET_DIMENSION, 
            target.cols * 1.0 / PYRAMID_MIM_TARGET_DIMENSION);

   Mat roiSourceGray;
   Mat targetGray;
   
   // convert image from RGB to grayscale
   cvtColor(roiSource, roiSourceGray, CV_RGB2GRAY);
   cvtColor(target, targetGray, CV_RGB2GRAY);

   TimingBlock tb("NEW METHOD");

   dout << "matching (center) ... " << endl;            
   Mat roiSourceGrayCenter = Mat(roiSourceGray, 
                             Range(roiSourceGray.rows*BORDER_MARGIN,
                                 roiSourceGray.rows*(1-BORDER_MARGIN)),
                             Range(roiSourceGray.cols*BORDER_MARGIN,
                                 roiSourceGray.cols*(1-BORDER_MARGIN)));
   create_matcher(roiSourceGrayCenter, targetGray, 1, ratio);
   add_matches_to_buffer(5); 
   if (top_score_in_buffer() >= max(min_similarity,CENTER_REMATCH_THRESHOLD)){
      roi.x += roiSourceGray.cols*BORDER_MARGIN;
      roi.y += roiSourceGray.rows*BORDER_MARGIN;   
      return;
   }
   
   dout << "matching (whole) ... " << endl;
   create_matcher(roiSourceGray, targetGray, 1, ratio);
   add_matches_to_buffer(5);
   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD)){
      return;
   }  
   
   dout << "matching (0.75) ..." << endl;
   create_matcher(roiSourceGray, targetGray, 1, ratio*0.75);
   add_matches_to_buffer(5);
   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD))
      return;
   
   if (ratio > 2){
      dout << "matching (0.5) ..." << endl;
      create_matcher(roiSourceGray, targetGray, 1, ratio*0.5);
      add_matches_to_buffer(5);
      if (top_score_in_buffer()  >= max(min_similarity,REMATCH_THRESHOLD))
         return;
   }
   
   if (ratio > 4){      
      dout << "matching (0.25) ..." << endl;
      create_matcher(roiSourceGray, targetGray, 1, ratio*0.25);
      add_matches_to_buffer(5);
      if (top_score_in_buffer()  >= max(min_similarity,REMATCH_THRESHOLD))
         return;
   }

   dout << "matching (original resolution) ... " << endl;
   create_matcher(roiSourceGray, targetGray, 0, 1);
   add_matches_to_buffer(5);
   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD))
      return;

   dout << "matching (original resolution: color) ... " << endl;
   create_matcher(roiSource, target, 0, 1);
   add_matches_to_buffer(5);
   
}

bool
Finder::hasNext(){  
   
   if (wf)
      return true;//wf->hasNext();
  
   return top_score_in_buffer() >= (min_similarity-0.0000001);
}

Match
Finder::next(){
   
   if (wf)
      return wf->next();   

   if (!hasNext())
      return Match(0,0,0,0,-1);
   
   Match top_match = buffered_matches.front();
   top_match.x += roi.x;
   top_match.y += roi.y;
   
   Match next_match = matcher->next();
   buffered_matches[0] = next_match;
   sort(buffered_matches,sort_by_score);
   return top_match;
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


//=====================================================================================
#include "ocr.h"

WordFinder::WordFinder(Mat inputImage)
: BaseFinder(inputImage){
};

void
WordFinder::train(Mat& trainingImage){	
	train_by_image(trainingImage);
}

void
WordFinder::find(const char* word, double _min_similarity){
   this->min_similarity = _min_similarity;
   BaseFinder::find();
	TimingBlock tb("WordFinder::find");
	matches = find_word_by_image(roiSource, word);
   matches_iterator = matches.begin();
}

void
WordFinder::find(vector<string> words, double _min_similarity){
   this->min_similarity = _min_similarity;
   BaseFinder::find();
	TimingBlock tb("WordFinder::find");
	matches = find_phrase(roiSource, words);
   matches_iterator = matches.begin();   
}

bool      
WordFinder::hasNext(){
   
//   dout << "[WordFinder] " << matches_iterator->score  << endl;
   return (matches_iterator != matches.end()) &&
       (matches_iterator->score >= min_similarity);
}

Match
WordFinder::next(){ 
   
   Match ret;
   if (hasNext()){
      ret = *matches_iterator;
      ++matches_iterator;
      return ret;
   }else {
      return Match(0,0,0,0,-1);
   }

}
   

void
WordFinder::recognize(const Mat& inputImage){	
//	recognize_helper(inputImage);
}

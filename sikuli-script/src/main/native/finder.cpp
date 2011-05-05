/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#include <stdio.h>
#include <iostream>
#include <fstream>

#include "finder.h"
#include "pyramid-template-matcher.h"
#include "TimingBlock.h"

using namespace cv;
using namespace std;

#define PYRAMID_MIM_TARGET_DIMENSION 6
#define PYRAMID_MIM_TARGET_DIMENSION_ALL 50
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
BaseFinder::BaseFinder(IplImage*  _source) : source(Mat(_source, false)){
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

bool sort_by_score(FindResult m1, FindResult m2){
   return m1.score > m2.score;
}


TemplateFinder::TemplateFinder(Mat _source) : BaseFinder(_source){
   matcher = NULL;
}

TemplateFinder::TemplateFinder(IplImage* _source) : BaseFinder(_source){
   matcher = NULL;
}

TemplateFinder::TemplateFinder(const char* source_image_filename)
: BaseFinder(source_image_filename){
   matcher = NULL;
}

TemplateFinder::~TemplateFinder(){
   if (matcher)
      delete matcher;
}

void 
TemplateFinder::find(const char *target_image_filename, double min_similarity){     
   Mat target = imread(target_image_filename, 1);
   if (target.data == NULL)
      throw cv::Exception();
   find(target, min_similarity);
}   

void 
TemplateFinder::find(IplImage* target, double min_similarity){
   find(Mat(target, false), min_similarity);
}   

void 
TemplateFinder::find_all(const char *target_image_filename, double min_similarity){
   Mat target = imread(target_image_filename, 1);
   if (target.data == NULL)
      throw cv::Exception();

   find_all(target, min_similarity);
}   


void 
TemplateFinder::find_all(IplImage* target, double min_similarity){
   find_all(Mat(target, true), min_similarity);
}   

void
TemplateFinder::find_all(Mat target, double min_similarity){   
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
   
   
   Mat sourceMat;
   Mat targetMat;
   
   if (min_similarity < 0.99){
      // if fuzzy matching, we use gray-scale image to boost speed
      
      Mat roiSourceGray;
      Mat targetGray;
      
      // convert image from RGB to grayscale
      cvtColor(roiSource, roiSourceGray, CV_RGB2GRAY);
      cvtColor(target, targetGray, CV_RGB2GRAY);
      
      sourceMat = roiSourceGray;
      targetMat = targetGray;      
      
   } else{
      // otherwise, we use color image to boost precision
      
      sourceMat = roiSource;
      targetMat = target;   
   }
   
   
   
   create_matcher(sourceMat, targetMat, levels, factor);
   add_matches_to_buffer(5);     
   
   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD))
      return;
   
   dout << "[find_all] matching (original resolution: color) ... " << endl;
   create_matcher(roiSource, target, 0, 1);
   add_matches_to_buffer(5);
   
}


float
TemplateFinder::top_score_in_buffer(){
   if (buffered_matches.empty())
      return -1;
   else
      return buffered_matches[0].score;
}

void
TemplateFinder::create_matcher(Mat& source, Mat& target, int level, float ratio){
   if (matcher)
      delete matcher;
   matcher = new PyramidTemplateMatcher(source,target,level,ratio);
}

void 
TemplateFinder::add_matches_to_buffer(int num_matches_to_add){ 
   buffered_matches.clear();
   for (int i=0;i<num_matches_to_add;++i){
      FindResult next_match = matcher->next();
      buffered_matches.push_back(next_match);
   }
   sort(buffered_matches,sort_by_score);
} 

void
TemplateFinder::find(Mat target, double min_similarity){
   
   this->min_similarity = min_similarity;   
   BaseFinder::find();  
   
   if (roiSource.cols < target.cols || roiSource.rows < target.rows){	   
	   current_match.score = -1;
	   return;
   }
   
   
   float ratio;
   ratio = min(target.rows * 1.0 / PYRAMID_MIM_TARGET_DIMENSION, 
               target.cols * 1.0 / PYRAMID_MIM_TARGET_DIMENSION);
   
   
   Mat sourceMat;
   Mat targetMat;
   
   if (min_similarity < 0.99){
      // if fuzzy matching, we use gray-scale image to boost speed
      
      Mat roiSourceGray;
      Mat targetGray;
      
      // convert image from RGB to grayscale
      cvtColor(roiSource, roiSourceGray, CV_RGB2GRAY);
      cvtColor(target, targetGray, CV_RGB2GRAY);
      
      
      sourceMat = roiSourceGray;
      targetMat = targetGray;
      
   } else{
      // otherwise, we use color image to boost precision
      
      sourceMat = roiSource;
      targetMat = target;   
   }
   
   
   TimingBlock tb("NEW METHOD");
   
   dout << "matching (center) ... " << endl;            
   Mat sourceMat_center = Mat(sourceMat, 
                                 Range(sourceMat.rows*BORDER_MARGIN,
                                       sourceMat.rows*(1-BORDER_MARGIN)),
                                 Range(sourceMat.cols*BORDER_MARGIN,
                                       sourceMat.cols*(1-BORDER_MARGIN)));
   create_matcher(sourceMat_center, targetMat, 1, ratio);
   add_matches_to_buffer(5); 
   if (top_score_in_buffer() >= max(min_similarity,CENTER_REMATCH_THRESHOLD)){
      roi.x += sourceMat.cols*BORDER_MARGIN;
      roi.y += sourceMat.rows*BORDER_MARGIN;   
      return;
   }
   
   dout << "matching (whole) ... " << endl;
   create_matcher(sourceMat, targetMat, 1, ratio);
   add_matches_to_buffer(5);
   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD)){
      return;
   }  
   
   dout << "matching (0.75) ..." << endl;
   create_matcher(sourceMat, targetMat, 1, ratio*0.75);
   add_matches_to_buffer(5);
   if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD))
      return;
   
   if (ratio > 2){
      dout << "matching (0.5) ..." << endl;
      create_matcher(sourceMat, targetMat, 1, ratio*0.5);
      add_matches_to_buffer(5);
      if (top_score_in_buffer()  >= max(min_similarity,REMATCH_THRESHOLD))
         return;
   }
   
   if (ratio > 4){      
      dout << "matching (0.25) ..." << endl;
      create_matcher(sourceMat, targetMat, 1, ratio*0.25);
      add_matches_to_buffer(5);
      if (top_score_in_buffer()  >= max(min_similarity,REMATCH_THRESHOLD))
         return;
   }
   
   if (min_similarity < 0.99){
      // only do this for fuzzy matching when the input images were
      // converted to gray-scale
      
      dout << "matching (original resolution: gray) ... " << endl;
      create_matcher(sourceMat, targetMat, 0, 1);
      add_matches_to_buffer(5);
      if (top_score_in_buffer() >= max(min_similarity,REMATCH_THRESHOLD))
         return;
   }
   

   dout << "matching (original resolution: color) ... " << endl;
   create_matcher(roiSource, target, 0, 1);
   add_matches_to_buffer(5);
 
   
}

bool
TemplateFinder::hasNext(){  
   return top_score_in_buffer() >= (min_similarity-0.0000001);
}

FindResult
TemplateFinder::next(){
   
   if (!hasNext())
      return FindResult(0,0,0,0,-1);
   
   FindResult top_match = buffered_matches.front();
   top_match.x += roi.x;
   top_match.y += roi.y;
   
   FindResult next_match = matcher->next();
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

FindResult
FaceFinder::next(){
   
   
   
   CvRect* r = (CvRect*)cvGetSeqElem(faces ,face_i);  
   face_i++;
   
   FindResult match;
   match.x = r->x + roi.x;
   match.y = r->y + roi.y;
   match.w = r->width;
   match.h = r->height;
   
   
   return match;
}

//=====================================================================================
#define PIXEL_DIFF_THRESHOLD 5
#define IMAGE_DIFF_THRESHOLD 5

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
   
   
   Size size = diff1.size();
   int ch = diff1.channels();
   typedef unsigned char T;

   int diff_cnt = 0;
   for( int i = 0; i < size.height; i++ )
    {
        const T* ptr1 = diff1.ptr<T>(i);
        for( int j = 0; j < size.width; j += ch )
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

FindResult
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
   
    FindResult m;
    m.x = x1 + roi.x;
    m.y = y1 + roi.y;
    m.w = x2 - x1 + 1;
    m.h = y2 - y1 + 1;
   
    c = c->h_next;
   return m;
}



//=====================================================================================
#include "tessocr.h"

TextFinder::TextFinder(Mat inputImage)
: BaseFinder(inputImage){
};

void
TextFinder::train(Mat& trainingImage){	
//	train_by_image(trainingImage);
}


// Code copied from 
// http://oopweb.com/CPP/Documents/CPPHOWTO/Volume/C++Programming-HOWTO-7.html
static void Tokenize(const string& str,
              vector<string>& tokens,
              const string& delimiters = " ")
{
   // Skip delimiters at beginning.
   string::size_type lastPos = str.find_first_not_of(delimiters, 0);
   // Find first "non-delimiter".
   string::size_type pos     = str.find_first_of(delimiters, lastPos);
   
   while (string::npos != pos || string::npos != lastPos)
   {
      // Found a token, add it to the vector.
      tokens.push_back(str.substr(lastPos, pos - lastPos));
      // Skip delimiters.  Note the "not_of"
      lastPos = str.find_first_not_of(delimiters, pos);
      // Find next "non-delimiter"
      pos = str.find_first_of(delimiters, lastPos);
    }
}


void
TextFinder::find(const char* text, double _min_similarity){
   vector<string> words;
   Tokenize(text, words, " ");
   return find(words, _min_similarity);
}

void
TextFinder::find_all(const char* text, double _min_similarity){
   vector<string> words;
   Tokenize(text, words, " ");
   return find_all(words, _min_similarity);
}


void
TextFinder::find(vector<string> words, double _min_similarity){
   this->min_similarity = _min_similarity;
   BaseFinder::find();
	TimingBlock tb("TextFinder::find");
	matches = OCR::find_phrase(roiSource, words);
   matches_iterator = matches.begin();   
}

void
TextFinder::find_all(vector<string> words, double _min_similarity){
   this->min_similarity = _min_similarity;
   BaseFinder::find();
	TimingBlock tb("TextFinder::find_all");
	matches = OCR::find_phrase(roiSource, words, false);
   matches_iterator = matches.begin();   
}


bool      
TextFinder::hasNext(){
   
//   dout << "[TextFinder] " << matches_iterator->score  << endl;
   return (matches_iterator != matches.end()) &&
   (matches_iterator->score >= min_similarity);
}

FindResult
TextFinder::next(){ 
   
   FindResult ret;
   if (hasNext()){
      ret = *matches_iterator;
      ++matches_iterator;
      return ret;
   }else {
      return FindResult(0,0,0,0,-1);
   }

}
   

vector<string>
TextFinder::recognize(const Mat& inputImage){	
	return vector<string>();//recognize_words(inputImage);
}


//=====================================================================================
Finder::Finder(Mat source)
: _source(source){
   _finder = NULL;
   _roi = Rect(-1,-1,-1,-1);
}

Finder::Finder(IplImage* source)
: _source(Mat(source)){
   _finder = NULL;
   _roi = Rect(-1,-1,-1,-1);
}

Finder::Finder(const char* source){
   _source = imread(source,1);
   _finder = NULL;
   _roi = Rect(-1,-1,-1,-1);
}

Finder::~Finder(){
   if (_finder)
      delete _finder;
}

void 
Finder::find(IplImage* target, double min_similarity){
   dout << "[Finder::find]" << endl;
   
   
   if (abs(min_similarity - 100)< 0.00001){
      cout << "training.." << endl;
      Mat im(target);
      TextFinder::train(im);
      
   }else{
   
      TemplateFinder* tf = new TemplateFinder(_source);
      if(_roi.width>0) tf->setROI(_roi.x, _roi.y, _roi.width, _roi.height);
      tf->find(target, min_similarity);
      if(_finder) delete _finder;
      _finder = tf;
      
   }
}

void 
Finder::find(const char *target, double min_similarity){
   dout << "[Finder::find]" << endl;

   const char* p = target;
   const char* ext = p + strlen(p) - 3;
   
   if (abs(min_similarity - 100)< 0.00001){
      
      Mat im = imread(target,1);
      TextFinder::train(im);
      
   }else if (strncmp(ext,"png",3) != 0){
      TextFinder* wf = new TextFinder(_source);
      if(_roi.width>0) wf->setROI(_roi.x, _roi.y, _roi.width, _roi.height);
      
         // get name after bundle path, which is
         // assumed to be the query word
      int j;
      for (j = (strlen(p)-1); j >=0; j--){      
         if (p[j]=='/')
         break;
      }
     
      const char* q = p + j + 1;
         
      wf->find(q,0.6);
      if(_finder) delete _finder;
      _finder = wf;
      
   }else {
      
      TemplateFinder* tf = new TemplateFinder(_source);
      if(_roi.width>0) tf->setROI(_roi.x, _roi.y, _roi.width, _roi.height);
      tf->find(target, min_similarity);
      if(_finder) delete _finder;
      _finder = tf;
   }                    
}

void 
Finder::find_all(IplImage*  target, double min_similarity){
   TemplateFinder* tf  = new TemplateFinder(_source);
   if(_roi.width>0) tf->setROI(_roi.x, _roi.y, _roi.width, _roi.height);
   tf->find_all(target, min_similarity);
   if(_finder) delete _finder;
   _finder = tf;
}

void 
Finder::find_all(const char *target, double min_similarity){
   
   const char* p = target;
   const char* ext = p + strlen(p) - 3;
   
   if (strncmp(ext,"png",3) != 0){
      TextFinder* wf = new TextFinder(_source);
      if(_roi.width>0) wf->setROI(_roi.x, _roi.y, _roi.width, _roi.height);
      
      // get name after bundle path, which is
      // assumed to be the query word
      int j;
      for (j = (strlen(p)-1); j >=0; j--){      
         if (p[j]=='/')
            break;
      }
      
      const char* q = p + j + 1;
      
      wf->find(q,0.6);
      if(_finder) delete _finder;
      _finder = wf;
   }else {
      
      TemplateFinder* tf = new TemplateFinder(_source);
      if(_roi.width>0) tf->setROI(_roi.x, _roi.y, _roi.width, _roi.height);
      tf->find_all(target, min_similarity);
      if(_finder) delete _finder;
      _finder = tf;
   }     
}

bool 
Finder::hasNext(){
   return _finder->hasNext();
}

FindResult 
Finder::next(){
   return _finder->next();
}


void Finder::setROI(int x, int y, int w, int h){
   _roi = Rect(x, y, w, h);
}

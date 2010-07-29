#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <iostream>
#include <sstream>
#include <vector>
#include <list>
#include <time.h>
#include <algorithm>
#include <cstdio>

#include "template-matcher.h"
#include "TimingBlock.h"

//#define CV_RESIZE_INTERPOLATION_OPTION CV_INTER_NN
#define CV_RESIZE_INTERPOLATION_OPTION CV_INTER_LINEAR
#define NUM_LOOKAHEAD 20

#ifdef DEBUG
#define dout std::cerr
#else
#define dout if(0) std::cerr
#endif

vector<double> distances_to(CvPoint point, vector<CvPoint>& points){
  vector<double> distances;
  for (int i=0; (int) i < points.size(); ++i){
    CvPoint& p1 = point;
    CvPoint& p2 = points[i];

    double distance = sqrt(double ((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y)));
    distances.push_back(distance);
  }
  return distances;
}

bool is_too_close_to_existing_points(CvPoint point, vector<CvPoint>& points, double min_distance = 10.0){
  vector<double> distances = distances_to(point, points);
  for (int i=0; (int) i < points.size(); ++i){
    if (distances[i] < min_distance)    
      return true;
  }
  return false;
}


vector<double> distances_to_matches(Match match, vector<Match>& matches){
  vector<double> distances;
  for (int i=0; (int) i < matches.size(); ++i){
    Match& p1 = match;
    Match& p2 = matches[i];

    double distance = sqrt(double ((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y)));
    distances.push_back(distance);
  }
  return distances;
}



bool is_too_close_to_existing_matches(Match match, vector<Match>& matches, double min_distance = 10.0){
  vector<double> distances = distances_to_matches(match, matches);
  for (int i=0; (int) i < matches.size(); ++i){
    if (distances[i] < min_distance)    
      return true;
  }
  return false;
}






bool descend_sort_function(Match m1, Match m2){
  return m1.score > m2.score;
}

void sort_matches(vector<Match>& matches){    
  sort(matches.begin(), matches.end(), descend_sort_function);    
}

bool
overlap(const Match &r1, const Match &r2)
{
  // The rectangles don't overlap if
  // one rectangle's minimum in some dimension 
  // is greater than the other's maximum in
  // that dimension.

  bool noOverlap = r1.x > r2.x + r2.w ||
    r2.x > r1.x + r1.w ||
    r1.y > r2.y + r2.h ||
    r2.y > r1.y + r1.h;

  return !noOverlap;
}

bool
overlap_existing_matches(Match match, vector<Match>& matches){
  for (int i=0; (int) i < matches.size(); ++i){
    if (overlap(match, matches[i]))
      return true;
  }
  return false;
}


// return best score
Match verify_match(IplImage *img, IplImage *tpl, CvRect roi){

  cvSetImageROI(img, roi);


  int res_width  = roi.width  - tpl->width + 1;
  int res_height = roi.height - tpl->height + 1;

  // create new image for template matching computation
  IplImage* res = cvCreateImage( cvSize( res_width, res_height ), IPL_DEPTH_32F, 1 );
  // released in this function

  cvMatchTemplate(img, tpl, res, CV_TM_CCOEFF_NORMED);

  // find best detection scores
  double maxval;
  CvPoint maxloc;
  cvMinMaxLoc( res, 0, &maxval, 0, &maxloc);

  Match match(roi.x+maxloc.x,roi.y+maxloc.y,tpl->width,tpl->height,maxval);
  
  cvResetImageROI(img);
  cvReleaseImage(&res);

  return match;
} 




LookaheadTemplateMatcher::LookaheadTemplateMatcher(IplImage *img, IplImage *tpl, float downsample_ratio)
: DownsampleTemplateMatcher(img,tpl,downsample_ratio){};

LookaheadTemplateMatcher::~LookaheadTemplateMatcher(){
   dout << "~LookaheadTemplateMatcher" << endl;
};

Match LookaheadTemplateMatcher::next(){
  TimingBlock tb("LookaheadTemplateMatcher::next()");
  if (top_matches_.size() == 0){
    for (int i=0;i<NUM_LOOKAHEAD;++i){
      
      Match match = DownsampleTemplateMatcher::next();
      top_matches_.push_back(match);

      // if a match is found to have high score, no need
      // to look ahead for better matches, otheriwse
      // continue to look for other better matches 
      // that may be the lower ranked in the low scale
      // space
      if (match.score > 0.9){
        break;
      }
    }

    sort_matches(top_matches_);
  }

  Match top_match = top_matches_[0];

  // replace the top match with a new match
  top_matches_[0] = DownsampleTemplateMatcher::next();

  sort_matches(top_matches_);

  return top_match; 
}

DownsampleTemplateMatcher::DownsampleTemplateMatcher(IplImage *img, IplImage *tpl, float downsample_ratio) 
: original_img_(img), original_tpl_(tpl), downsample_ratio_(downsample_ratio){

  if (downsample_ratio < 1.0)
    downsample_ratio = 1.0;

  downsampled_img_ = cvCreateImage(cvSize(img->width/downsample_ratio, img->height/downsample_ratio), IPL_DEPTH_8U, 3 );
  // released in DownsampleTempalteMatcher's desctrutor
  cvResize(img, downsampled_img_, CV_RESIZE_INTERPOLATION_OPTION);

  downsampled_tpl_ = cvCreateImage(cvSize(tpl->width/downsample_ratio, tpl->height/downsample_ratio), IPL_DEPTH_8U, 3 );
  // released in DownsampleTempalteMatcher's desctrutor
  cvResize(tpl, downsampled_tpl_, CV_RESIZE_INTERPOLATION_OPTION);

  init(downsampled_img_,downsampled_tpl_);
};

DownsampleTemplateMatcher::~DownsampleTemplateMatcher(){
   dout << "~DownsampleTemplateMatcher" << endl;
   if(downsampled_img_) cvReleaseImage(&downsampled_img_);
   if(downsampled_tpl_) cvReleaseImage(&downsampled_tpl_);
};

Match DownsampleTemplateMatcher::next(){
  TimingBlock tb("DownsampleTemplateMatcher::next()");

  Match match = TemplateMatcher::next();

  if (match.score == 0.0)
    return match;

  int x = match.x*downsample_ratio_;
  int y = match.y*downsample_ratio_;
  int w = original_tpl_->width;
  int h = original_tpl_->height;

  // compute the parameter to define the neighborhood rectangle
  int margin = 10*downsample_ratio_;
  int x0 = max(x-margin,0);
  int y0 = max(y-margin,0);
  int x1 = min(x+w+margin,original_img_->width);
  int y1 = min(y+h+margin,original_img_->height);
  CvRect roi = cvRect(x0,y0,x1-x0,y1-y0);

  // compute the actual template matching score within the neighborhood
  Match verified_match = verify_match(original_img_, original_tpl_, roi);

  return verified_match;
}

TemplateMatcher::TemplateMatcher(IplImage *img, IplImage *tpl){
  init(img,tpl);
}

TemplateMatcher::~TemplateMatcher(){
   dout << "~TemplateMatcher" << endl;
   //if(img_) cvReleaseImage(&img_); img_ should be released by its creator
   //if(tpl_) cvReleaseImage(&tpl_); tpl_ should be released by its creator
   if(res_) cvReleaseImage(&res_);
}

void TemplateMatcher::init(IplImage *img, IplImage *tpl){
  TimingBlock tb("TemplateMatcher::init()");
  // create new image for template matching computation
  img_ = img;
  tpl_ = tpl;
  int res_width  = img->width  - tpl->width + 1;
  int res_height = img->height - tpl->height + 1;
  res_ = cvCreateImage( cvSize( res_width, res_height ), IPL_DEPTH_32F, 1 );
  cvMatchTemplate(img, tpl, res_, CV_TM_CCOEFF_NORMED );
  // res_ should be released by descructor
}

Match TemplateMatcher::next(){
  TimingBlock tb("TemplateMatcher::next()");

  CvPoint detection_loc;
  double detection_score=1.0;

  // block out a padded window around the match by setting their correlation scores to zeros
  // this removes overlapping duplicates (for now, we added 30% padding to each dimension)
  cvMinMaxLoc( res_, 0, &detection_score, 0, &detection_loc, 0 );

  int xmargin = tpl_->width/3;
  int ymargin = tpl_->height/3;

  int& x = detection_loc.x;
  int& y = detection_loc.y;
  int& w = tpl_->width;
  int& h = tpl_->height;

  int x0 = max(x-xmargin,0);
  int y0 = max(y-ymargin,0);
  int x1 = min(x+w,res_->width);  // no need to blank right and bottom
  int y1 = min(y+h,res_->height);


  {
     //TimingBlock tb("TemplateMatcher::next():remove-overlaps");
     cvRectangle(res_, cvPoint(x0, y0), cvPoint(x1-1, y1-1), 
                       cvScalar(0), CV_FILLED);
  }

  Match match(detection_loc.x,detection_loc.y,tpl_->width,tpl_->height,detection_score);          
  return match;    
}





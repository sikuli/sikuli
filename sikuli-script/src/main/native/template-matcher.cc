#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <iostream>
#include <sstream>
#include <vector>
#include <list>
#include <time.h>
#include <algorithm>

#include "template-matcher.h"

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
  //Match match(maxloc.x,maxloc.y,tpl->width,tpl->height,maxval);

  cvResetImageROI(img);
  cvReleaseImage(&res);

  return match;
} 




LookaheadTemplateMatcher::LookaheadTemplateMatcher(IplImage *img, IplImage *tpl, float downsample_ratio)
: DownsampleTemplateMatcher(img,tpl,downsample_ratio){};


Match 
LookaheadTemplateMatcher::next(){
  if (top_matches_.size() == 0){
    for (int i=0;i<5;++i){
      top_matches_.push_back(DownsampleTemplateMatcher::next());
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

  cout << "screen/template: (" << img->width << "," << img->height << ")";
  cout << "/(" << tpl->width << "," << tpl->height << ")" << endl;

  IplImage* downsampled_img = cvCreateImage(cvSize(img->width/downsample_ratio, img->height/downsample_ratio), IPL_DEPTH_8U, 3 );
  // released in DownsampleTempalteMatcher's desctrutor
  cvResize(img, downsampled_img);

  IplImage* downsampled_tpl = cvCreateImage(cvSize(tpl->width/downsample_ratio, tpl->height/downsample_ratio), IPL_DEPTH_8U, 3 );
  // released in DownsampleTempalteMatcher's desctrutor
  cvResize(tpl, downsampled_tpl);

  cout << "downsampled to: (" << downsampled_img->width << "," << downsampled_img->height << ")";
  cout << "/(" << downsampled_tpl->width << "," << downsampled_tpl->height << ")" << " ratio = " << downsample_ratio << endl;

  //cout << "[time] after downsampling:\t" << (clock() - starttime)/CLOCKS_PER_SEC << " sec." << endl;

  init(downsampled_img,downsampled_tpl);  
};

DownsampleTemplateMatcher::~DownsampleTemplateMatcher(){
  cvReleaseImage(&img_);
  cvReleaseImage(&tpl_);  
  cvReleaseImage(&res_);
};

Match DownsampleTemplateMatcher::next(){

  Match match = TemplateMatcher::next();

  if (match.score == 0.0)
    return match;

  int x = match.x*downsample_ratio_;
  int y = match.y*downsample_ratio_;
  int w = original_tpl_->width;
  int h = original_tpl_->height;

  // compute the parameter to define the neighborhood rectangle
  int margin = 5;
  int x0 = max(x-margin,0);
  int y0 = max(y-margin,0);
  int x1 = min(x+w+margin,original_img_->width);
  int y1 = min(y+h+margin,original_img_->height);
  CvRect roi = cvRect(x0,y0,x1-x0,y1-y0);

  //cout << match.score << endl;
  // compute the actual template matching score within the neighborhood
  Match verified_match = verify_match(original_img_, original_tpl_, roi);

  return verified_match;
}

TemplateMatcher::TemplateMatcher(IplImage *img, IplImage *tpl){
  init(img,tpl);
}

TemplateMatcher::~TemplateMatcher(){
  cvReleaseImage(&img_);
  cvReleaseImage(&tpl_);
  cvReleaseImage(&res_);
}

void
TemplateMatcher::init(IplImage *img, IplImage *tpl){
  // create new image for template matching computation
  img_ = img;
  tpl_ = tpl;
  int res_width  = img->width  - tpl->width + 1;
  int res_height = img->height - tpl->height + 1;
  res_ = cvCreateImage( cvSize( res_width, res_height ), IPL_DEPTH_32F, 1 );
  cvMatchTemplate(img, tpl, res_, CV_TM_CCOEFF_NORMED );
  // released by descructor
}

Match TemplateMatcher::next(){

  CvPoint detection_loc;
  double detection_score=1.0;

  cvMinMaxLoc( res_, 0, &detection_score, 0, &detection_loc, 0 );

  for(int i=detection_loc.y; i < min(detection_loc.y+tpl_->height, res_->height); i++) 
    for(int j=detection_loc.x; j < min(detection_loc.x+tpl_->width, res_->width); j++) 
      cvSet2D(res_,i,j, cvScalar(0));

  Match match(detection_loc.x,detection_loc.y,tpl_->width,tpl_->height,detection_score);          
  return match;    
}



Matches match_by_template(const char* screen_image_filename, 
                          const char *template_image_filename,
                          int max_num_matches, 
                          double min_similarity_threshold,
                          bool search_multiscale,
                          int x, int y, int w, int h,
                          bool write_images, bool display_images)
{


  IplImage  *img;
  IplImage  *tpl;
  Matches matches;

  CvRect roi;



  float downsample_ratio = 1.0;
  int min_tpl_dimension = 12;
  

  float starttime, stoptime, timeused;
  starttime = clock();


  cout << endl << "screen_match is called with n=" << max_num_matches << " and t=" << min_similarity_threshold << endl;

  // load input image
  img = cvLoadImage( screen_image_filename, CV_LOAD_IMAGE_COLOR );
  // released in this function
 
  // obtain region of interest rectangle
   if (w>0&&h>0){

    roi = cvRect(x,y,w,h);

  }else{

    roi = cvRect(0,0,img->width,img->height);
  }

  // obtain a subimage for the roi rectangle
  cvSetImageROI(img, roi);

  IplImage *roi_img;
  roi_img = cvCreateImage(cvGetSize(img),img->depth,img->nChannels);
  // released in this function
  cvCopy(img, roi_img);
 
  cvResetImageROI(img);


  /* always check */
  if( img == 0 ) {
    fprintf( stderr, "Cannot load file %s!\n", screen_image_filename );
    return matches; 
  }

  /* load template image */
  tpl = cvLoadImage( template_image_filename, CV_LOAD_IMAGE_COLOR );
  // released in this function

  /* always check */
  if( tpl == 0 ) {
    fprintf( stderr, "Cannot load file %s!\n", template_image_filename );
    return matches;
  }

  if (write_images){
    cvSaveImage("input.jpg", img);
  }  
  cout << "[time] after loading images:\t" << (clock() - starttime)/CLOCKS_PER_SEC << " sec." << endl;


  vector<float> scales;
  if (search_multiscale){


    //scales.push_back(0.25);
    scales.push_back(0.5);
    scales.push_back(0.75);
    scales.push_back(1.0);
    scales.push_back(1.25);
    scales.push_back(1.5);
    scales.push_back(1.75);
    scales.push_back(2.0);
  }else{
    scales.push_back(1.0);
  }


  vector<TemplateMatcher*> matchers;
  vector<IplImage*> scaled_tpls;


  for (int t=0; t< scales.size(); ++t){

    float scale = scales[t];

    int scaled_tpl_width  = tpl->width * scale;
    int scaled_tpl_height = tpl->height * scale;
    IplImage* scaled_tpl = cvCreateImage(cvSize(scaled_tpl_width, scaled_tpl_height), IPL_DEPTH_8U, 3 );
    // released at the end of match_by_template()
    
    cvResize(tpl, scaled_tpl);
    scaled_tpls.push_back(scaled_tpl);
    
    downsample_ratio = 1.0*min(scaled_tpl->width,scaled_tpl->height) / min_tpl_dimension;
    
    

    //DownsampleTemplateMatcher* matcher = new DownsampleTemplateMatcher(img, scaled_tpl, downsample_ratio);
    TemplateMatcher* matcher = new LookaheadTemplateMatcher(roi_img, scaled_tpl, downsample_ratio);

    matchers.push_back(matcher);

  }

  // the best match at each scale
  vector<Match> best_matches;
  for (int i=0; i<(int) matchers.size(); ++i){
    best_matches.push_back(matchers[i]->next());
  }

  bool detect_more = true;
  while (detect_more && matches.size() < max_num_matches){


    double best_score = 0.0;    
    int best_i=0;
    for (int i=0; i<(int) matchers.size(); ++i){
      if (best_matches[i].score > best_score){
        best_i     = i;
        best_score = best_matches[i].score;        
      }      
    }

    Match best_match = best_matches[best_i];
    best_matches[best_i] = matchers[best_i]->next();


//    cout << best_match.score << endl;

    if (best_match.score > min_similarity_threshold/2){

      if (!overlap_existing_matches(best_match, matches)){
        matches.push_back(best_match);
      }

    }else{

      detect_more = false;

    }
  }

  //cout << "[time] after finding all:\t" << (clock() - starttime)/CLOCKS_PER_SEC << " sec." << endl;

  sort_matches(matches);

  for (int i=0;i<(int)matches.size();++i){
      Match& match = matches[i];
      match.x = match.x + roi.x;
      match.y = match.y + roi.y;
  }

  if (write_images){

    for (int i=0;i<(int)matches.size();++i){
      Match match = matches[i];

      cvRectangle(img, 
        cvPoint( match.x, match.y), 
        cvPoint( match.x + match.w, match.y + match.h),
        cvScalar( 0, 0, (int)255*match.score, 0 ), 2, 0, 0 );  


      cvRectangle(img,
        cvPoint(roi.x, roi.y),
        cvPoint(roi.x + roi.width, roi.y + roi.height),
        cvScalar( 255, 0, 255), 2, 0, 0 );  
      

      CvPoint center;
      center.x = match.x + match.w/2;
      center.y = match.y + match.h/2;
      //cvCircle(imgdownsampled_img, cvPoint(center.x, center.y), 10, cvScalar(0,0,255,0));

      CvFont font;
      {
        stringstream ss;
        ss << match.score;  
        CvPoint loc = center;
        loc.y = loc.y + 20;
        cvInitFont(&font,CV_FONT_HERSHEY_SIMPLEX, 0.5,0.5,0,1);                  
        cvPutText(img,ss.str().c_str(),loc, &font, cvScalar(255,0,0));
      }

      {
        stringstream ss;
        ss << i+1;
        cvInitFont(&font,CV_FONT_HERSHEY_SIMPLEX|CV_FONT_ITALIC, 1.0,1.0,0,3);                  
        cvPutText(img,ss.str().c_str(),center, &font, cvScalar(255,0,0));
      }


    }



    cvSaveImage("output.jpg", img);
    cvSaveImage("template.jpg", tpl);
    cout << "[time] after writing images:\t" << (clock() - starttime)/CLOCKS_PER_SEC << " sec." << endl;
  }

  if (display_images){
    cvNamedWindow("Result", CV_WINDOW_AUTOSIZE);
    cvShowImage("Result",img);

    cvNamedWindow("Template", CV_WINDOW_AUTOSIZE);
    cvShowImage("Template",tpl);

    /* wait until user press a key to exit */
    cvWaitKey( 0 );
  }

  cvReleaseImage( &img );
  cvReleaseImage( &tpl );
  cvReleaseImage( &roi_img);
  for (int i=0;i<scaled_tpls.size();i++){
    cvReleaseImage(&scaled_tpls[i]);
  }

  cout << "Results: (" << matches.size() << " matches)" << endl;
  cout << "no.\tx\ty\tscore\n";
  for(int i=0;i<(int)matches.size();++i){
    Match& m = matches[i];
    cout << i+1 << "\t" << m.x << "\t" << m.y << "\t" << m.score << endl;
  }

  return matches;
}



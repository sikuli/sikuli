#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <iostream>
#include <sstream>
#include <vector>
#include <list>
#include <time.h>
#include <algorithm>

#include "finder.h"
#include "TimingBlock.h"


#define MIN_TPL_DIMENSION 12

BaseFinder::BaseFinder(const char* screen_image_filename){
  // load the screen image (released in the desctrutor)
  img = cvLoadImage(screen_image_filename);
  if( img == 0 ) {  
    cerr << "Cannot load screen image " << screen_image_filename << endl;
    return;
  }

  // roi default to the entire image
  roi = cvRect(0,0,img->width,img->height);
  roi_img = 0;

  is_debug = false;
  debug_img = 0;
}

BaseFinder::BaseFinder(const IplImage *screen_image){
  img = cvCreateImage(cvGetSize(screen_image),
                      screen_image->depth,screen_image->nChannels);
  cvCopy(screen_image, img);  

  // roi default to the entire image
  roi = cvRect(0,0,img->width,img->height);
  roi_img = 0;

  is_debug = false;
  debug_img = 0;
}


BaseFinder::~BaseFinder(){
  if (img)
    cvReleaseImage(&img);
  if (roi_img)
    cvReleaseImage(&roi_img);
  if (debug_img)
    cvReleaseImage(&debug_img);
}

void 
BaseFinder::setROI(int x, int y, int w, int h){
  roi = cvRect(x,y,w,h);
}

void
BaseFinder::find(){
  // reset find specific data
  if (roi_img){
    cvReleaseImage(&roi_img);
    roi_img = 0;
  }

  if (img){
    // create an ROI image to work on (released in the destructor)
    cvSetImageROI(img, roi);
    roi_img = cvCreateImage(cvGetSize(img),img->depth,img->nChannels);
    cvCopy(img, roi_img);
    cvResetImageROI(img);
  }

}


void
BaseFinder::debug_save_image(const char* output_image_filename){
  if (!is_debug) 
    return;

  cvSaveImage(output_image_filename, debug_img);
}


void
BaseFinder::debug_show_image(){
  if (is_debug && debug_img){
    cvNamedWindow("Result", CV_WINDOW_AUTOSIZE);
    cvShowImage("Result",debug_img);
    cvWaitKey( 0 );
  }
}

void
BaseFinder::debug_draw_match(Match match, int rank){
  if (!is_debug) 
    return;

  cvRectangle(debug_img, 
    cvPoint( match.x, match.y), 
    cvPoint( match.x + match.w, match.y + match.h),
    cvScalar( 0, 0, (int)255*match.score, 0 ), 2, 0, 0 );  


  // draw the ROI rectangle
  cvRectangle(debug_img,
    cvPoint(roi.x, roi.y),
    cvPoint(roi.x + roi.width, roi.y + roi.height),
    cvScalar( 255, 0, 255), 2, 0, 0 );  


  CvPoint center;
  center.x = match.x + match.w/2;
  center.y = match.y + match.h/2;
  //cvCircle(imgdownsampled_img, cvPoint(center.x, center.y), 10, cvScalar(0,0,255,0));

  CvFont font;
  if (match.score > 0)  
  {
    stringstream ss;
    ss << match.score;  
    CvPoint loc = center;
    loc.y = loc.y + 20;
    cvInitFont(&font,CV_FONT_HERSHEY_SIMPLEX, 0.5,0.5,0,1);                  
    cvPutText(debug_img,ss.str().c_str(),loc, &font, cvScalar(255,0,0));
  }


  if (rank > 0)
  {
    stringstream ss;
    ss << rank;
    cvInitFont(&font,CV_FONT_HERSHEY_SIMPLEX|CV_FONT_ITALIC, 1.0,1.0,0,3);                  
    cvPutText(debug_img,ss.str().c_str(),center, &font, cvScalar(255,0,0));
  }
}

void 
BaseFinder::debug_init_image(){
  if (!is_debug) 
    return;

  if (img){
    
    if (debug_img)
      cvReleaseImage(&debug_img);

    debug_img = cvCreateImage(cvGetSize(img),img->depth,img->nChannels);
    cvCopy(img, debug_img);
  }
}


//=======================================================================================
Finder::Finder(const char* screen_image_filename)
: BaseFinder(screen_image_filename){
  tpl = 0;
  matcher = 0;
}

Finder::Finder(const IplImage* screen_image)
: BaseFinder(screen_image){
  tpl = 0;
  matcher = 0;
}

void
Finder::find(const IplImage* tpl_in, double min_similarity){
  TimingBlock tb("Finder::find()");
  this->min_similarity = min_similarity;

  if (tpl){    
    cvReleaseImage(&tpl);
    tpl = 0;
  }

  tpl = cvCreateImage(cvGetSize(tpl_in),tpl_in->depth,tpl_in->nChannels);
  cvCopy(tpl_in, tpl);  

  find_helper();
}

void 
Finder::find(const char *template_image_filename, double min_similarity){
  TimingBlock tb("Finder::find()");
  this->min_similarity = min_similarity;

  if (tpl){
    cvReleaseImage(&tpl);
    tpl = 0;
  }

  {
     TimingBlock tb("Finder::cvLoadImage()");
     // load the target template image (released in the destrutor)
     tpl = cvLoadImage( template_image_filename, CV_LOAD_IMAGE_COLOR );
     if( tpl == 0 ) {  
       cerr << "Cannot load target image " << template_image_filename << endl;
       return;
     }
  }

  find_helper();
}


void
Finder::find_helper(){
  TimingBlock tb("Finder::find_helper()");

  // prepare the roi image
  BaseFinder::find();

  // compute downsample ratio
  //float downsample_ratio = 1.0*min(tpl->width,tpl->height) / MIN_TPL_DIMENSION;  

  current_rank = 1;

  if (roi_img->width >= tpl->width && roi_img->height >= tpl->height){

    // create a template matcher (released in the destructor)
    if (matcher){
      delete matcher;
      matcher = 0;
    }
    float downsample_ratio = 1.0*min(tpl->width,tpl->height) / MIN_TPL_DIMENSION;
    matcher = new LookaheadTemplateMatcher(roi_img, tpl, downsample_ratio);

    
    current_match = matcher->next();
    if (current_match.score < 0.8){
      TimingBlock tb("find_helper():recreate a TemplateMatcher");
      delete matcher;
      matcher = new TemplateMatcher(roi_img, tpl);
      current_match = matcher->next();
    }


    current_match.x = current_match.x + roi.x;
    current_match.y = current_match.y + roi.y;

  }
  else{

    current_match.score = -1;

  }
  
  // [DEBUG] reset the debug image to the content of the input image
  debug_init_image();

  //current_match = next();
}

void 
Finder::debug_init_image(){
  if (!is_debug) 
    return;

  if (img){

    // create a canvas so we can concatante the target image to the screen
    CvSize s = cvSize(img->width + tpl->width + 1, max(img->height, tpl->height));
   
    if (debug_img)
      cvReleaseImage(&debug_img);

    debug_img = cvCreateImage(cvGetSize(img),img->depth,img->nChannels);
   

    cvCopy(img, debug_img);

    cvSetImageROI(debug_img, cvRect(5,5,tpl->width,tpl->height));
    cvCopy(tpl, debug_img);
    cvResetImageROI(debug_img);

    // draw a thick border around the template image
    cvRectangle(debug_img,
      cvPoint(5, 5),
      cvPoint(5+tpl->width, 5+tpl->height),
      cvScalar(0, 0, 0), 5, 0, 0 );  

    cvRectangle(debug_img,
      cvPoint(5, 5),
      cvPoint(5+tpl->width, 5+tpl->height),
      cvScalar(180, 180, 180), 2, 0, 0 );  


/*

    debug_img = cvCreateImage(s,img->depth,img->nChannels);

    cvSetImageROI(debug_img, cvRect(0,0,img->width,img->height));  
    cvCopy(img, debug_img);
    cvResetImageROI(debug_img);

    cvSetImageROI(debug_img, cvRect(img->width+1,0,tpl->width,tpl->height));
    cvCopy(tpl, debug_img);
    cvResetImageROI(debug_img);
*/
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
    debug_draw_match(current_match, current_rank);

    // Todo: fetch next only when actually called

    current_match = matcher->next();
    // convert from roi coordinate to the main coordinate
    current_match.x = current_match.x + roi.x;
    current_match.y = current_match.y + roi.y;
    //current_match.rank = current_rank;
    current_rank++;

    return temp;
  }else{
     Match match;
     match.score = -1;
     return match;
  }
}


Finder::~Finder(){

  if (matcher)
    delete matcher;
  if (img)
    cvReleaseImage( &img );  
  if (tpl)
    cvReleaseImage( &tpl );
  if (roi_img)
    cvReleaseImage( &roi_img);
  if (debug_img)
    cvReleaseImage( &debug_img);
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
  cvReleaseImage(&img);
  if (cascade)
    cvReleaseHaarClassifierCascade(&cascade);
  if (storage)
    cvReleaseMemStorage(&storage);
}

void 
FaceFinder::find(){

  BaseFinder::find();

  storage = cvCreateMemStorage(0);
  faces = cvHaarDetectObjects(roi_img, cascade, storage, 1.1, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(40,40));
  face_i = 0;  

  // [DEBUG] reset the debug image to the content of the input image
  debug_init_image();
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

  debug_draw_match(match,face_i);

  return match;
}

//=====================================================================================
template<class T> class Image
{
private:
  IplImage* imgp;
public:
  Image(IplImage* img=0) {imgp=img;}
  ~Image(){imgp=0;}
  void operator=(IplImage* img) {imgp=img;}
  inline T* operator[](const int rowIndx) {
    return ((T *)(imgp->imageData + rowIndx*imgp->widthStep));}
};

typedef struct{
  unsigned char b,g,r;
} RgbPixel;

typedef struct{
  float b,g,r;
} RgbPixelFloat;

typedef Image<RgbPixel>       RgbImage;
typedef Image<RgbPixelFloat>  RgbImageFloat;
typedef Image<unsigned char>  BwImage;
typedef Image<float>          BwImageFloat;

#define PIXEL_DIFF_THRESHOLD 50

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

ChangeFinder::~ChangeFinder(){
  if (storage)
    cvReleaseMemStorage(&storage);
}


void
ChangeFinder::find(const char* new_screen_image_filename){
  IplImage* im = cvLoadImage( new_screen_image_filename, CV_LOAD_IMAGE_COLOR );
  find(im);
  cvReleaseImage(&im);
}

void
ChangeFinder::find(IplImage* new_screen_image){

  BaseFinder::find(); // set ROI
  debug_init_image();


  IplImage* roi_img2 = cvCreateImage(cvGetSize(roi_img), IPL_DEPTH_8U, 3);
  cvSetImageROI(new_screen_image, roi);
  cvCopy(new_screen_image,roi_img2);
  cvResetImageROI(new_screen_image);

  IplImage* im1 = roi_img;
  IplImage* im2 = roi_img2;
  
  //im2 = cvCreateImage(cvSize(im1->width, im1->height), IPL_DEPTH_8U, 3);
  //cvCopy(new_screen_image,im2);
  
  //im2 = cvLoadImage( new_screen_image_filename, CV_LOAD_IMAGE_COLOR );

  IplImage* diff3 = cvCreateImage(cvSize(im1->width, im1->height), IPL_DEPTH_8U, 3);
  cvAbsDiff(im1,im2,diff3);

  IplImage* diff1 = cvCreateImage(cvSize(im1->width, im1->height), IPL_DEPTH_8U, 1); 

  RgbImage  Idiff3(diff3);
  BwImage   Idiff1(diff1);

  int diff_cnt = 0;
  for (int i=0;i<diff3->height;i++){
    for (int j=0;j<diff3->width;j++){

      Idiff1[i][j] = Idiff3[i][j].r + Idiff3[i][j].g + Idiff3[i][j].b;

      if (Idiff1[i][j] > PIXEL_DIFF_THRESHOLD) 
        diff_cnt++;
    }
  }
  cvReleaseImage(&diff3);

  // quickly check if two images are nearly identical
  if (diff_cnt < 20){
    is_identical = true;
    cvReleaseImage(&diff1);    
    cvReleaseImage(&roi_img2);
    return;
  }

  //RgbImage  Iim2(im2);

  cvThreshold(diff1,diff1,PIXEL_DIFF_THRESHOLD,255,CV_THRESH_BINARY);
  cvDilate(diff1,diff1);

  //if (bDrawImage){
  //  for (int i=0;i<diff3->height;i++){
  //    for (int j=0;j<diff3->width;j++){
  //      if (Idiff1[i][j] > PIXEL_DIFF_THRESHOLD){        
  //        Iim2[i][j].r = 0;
  //      }
  //    }
  //  }
  //}

  // close operation
  IplConvKernel* se = cvCreateStructuringElementEx(5,5,1,1,CV_SHAPE_ELLIPSE,0);
  IplImage* temp = cvCreateImage(cvSize(im1->width, im1->height), IPL_DEPTH_8U, 1);
  cvMorphologyEx(diff1,diff1,temp,se, CV_MOP_CLOSE, 1);
  cvReleaseImage(&temp);
  cvReleaseStructuringElement(&se);


  storage = cvCreateMemStorage();
  CvSeq* first_contour = NULL;

  int Nc = cvFindContours(
    diff1,
    storage,
    &first_contour,
    sizeof(CvContour),
    CV_RETR_EXTERNAL);

  c = first_contour;

  cvReleaseImage(&diff1);
  cvReleaseImage(&diff3);
  cvReleaseImage(&roi_img2);


}
  //for( CvSeq* c=first_contour; c!=NULL; c=c->h_next ){
bool      
ChangeFinder::hasNext(){
  return !is_identical  && c !=NULL;
}

Match
ChangeFinder::next(){ 

 // find bounding boxes
    int x1=img->width;
    int x2=0;
    int y1=img->height;
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

  debug_draw_match(m,0);

    return m;
}

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
#include "screendiff.h"

#define DISPLAY_IMAGES false
#define PIXEL_DIFF_THRESHOLD 50

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



vector<DiffRegion> screendiff(const char* before_image_filename, const char *after_image_filename,
                              const char* output_image_filename = NULL)
{


  IplImage  *im1;
  IplImage  *im2;
  vector<DiffRegion> regions;  

  bool bDrawImage = output_image_filename || DISPLAY_IMAGES;

  // load input image
  im1 = cvLoadImage( before_image_filename, CV_LOAD_IMAGE_COLOR );
  im2 = cvLoadImage( after_image_filename, CV_LOAD_IMAGE_COLOR );


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

  // quickly check if two images are nearly identical
  if (diff_cnt < 20){
    printf("Found %d diff regions.\n", regions.size());
    return regions;
  }

  RgbImage  Iim2(im2);

  cvThreshold(diff1,diff1,PIXEL_DIFF_THRESHOLD,255,CV_THRESH_BINARY);
  cvDilate(diff1,diff1);

  if (bDrawImage){
    for (int i=0;i<diff3->height;i++){
      for (int j=0;j<diff3->width;j++){
        if (Idiff1[i][j] > PIXEL_DIFF_THRESHOLD){        
          Iim2[i][j].r = 0;
        }
      }
    }
  }

  IplConvKernel* se = cvCreateStructuringElementEx(5,5,1,1,CV_SHAPE_ELLIPSE,0);
  IplImage* temp = cvCreateImage(cvSize(im1->width, im1->height), IPL_DEPTH_8U, 1);
  cvMorphologyEx(diff1,diff1,temp,se, CV_MOP_CLOSE, 1);



  CvMemStorage* storage = cvCreateMemStorage();
  CvSeq* first_contour = NULL;

  int Nc = cvFindContours(
    diff1,
    storage,
    &first_contour,
    sizeof(CvContour),
    CV_RETR_EXTERNAL);


  if (bDrawImage){
    CvScalar red = CV_RGB(250,0,0);
    CvScalar blue = CV_RGB(0,0,250);    
    cvDrawContours(
      im2,
      first_contour,
      red,		// Red
      blue,		// Blue
      1,			// Vary max_level and compare results
      1,      // Thinkness
      8 );
  }


  for( CvSeq* c=first_contour; c!=NULL; c=c->h_next ){

    // find bounding boxes
    int x1=diff1->width;
    int x2=0;
    int y1=diff1->height;
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

    // detect lower-left shadow
    double cnt = 0;
    for( int i=0; i < c->total; ++i ){
      CvPoint* p = CV_GET_SEQ_ELEM( CvPoint, c, i );
      if (p->x > (x2 - (x2-x1)/5) || p->y > (y2 - (y2-y1)/5))
        cnt++;
    }

    bool select = true;
    /*
    if (cnt/c->total > 0.90){
      select = false;
    }
    */

    int w = (x2-x1);
    int h = (y2-y1);
    int area = h*w;    
    if (area<20){
      select = false;
    }

    if (h<3 || w<3){
      select = false;
    }

    if (select){

      DiffRegion r;
      r.x = x1;
      r.y = y1;
      r.h = h;
      r.w = w;
      regions.push_back(r);
    }


    if (select && bDrawImage){

      printf("(%d,%d)-(%d,%d)\n", x1,y1,x2,y2);
      cvRectangle(im2,
        cvPoint(x1,y1),
        cvPoint(x2,y2),
        cvScalar(0, 255, 0), 2, 0, 0 );  

    }

  }

  printf("Found %d diff regions.\n", regions.size());

  if (DISPLAY_IMAGES){
    cvNamedWindow("Contours 2", CV_WINDOW_AUTOSIZE);
    cvShowImage( "Contours 2", im2 );
    cvWaitKey();
  }

  if (output_image_filename){
    cvSaveImage(output_image_filename, im2);
  }


  cvReleaseImage(&diff3);
  cvReleaseImage(&diff1);
  cvReleaseImage(&im1);
  cvReleaseImage(&im2);

  return regions;
}

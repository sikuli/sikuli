#include "cv.h"
#include "highgui.h"

#include "assert.h"

#include <fstream>
#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

#define DEBUG 1

//===================================================================
// some opencv helper functions

namespace VDict{

   struct Match{
      int value;
      float score;
   };

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

}


   void doPyrDown(IplImage** pin, int filter = IPL_GAUSSIAN_5x5)
   {
      // Best to make sure input image is divisible by two.
      //
      //assert( in->width%2 == 0 && in->height%2 == 0 );
      IplImage* in = *pin;

      IplImage* out = cvCreateImage(
        cvSize( in->width/2, in->height/2 ),
        in->depth,
        in->nChannels
        );

      cvPyrDown( in, out, filter );
      cvReleaseImage(&in);

      *pin = out;

      //return out;
   };

   //===================================================================
   // VizDict class definition and implementation

   struct VizRecord{
      int value;
      IplImage* image;
      int original_height;
      int original_width;
   };

   class VizDict{

   public:

      // insert an (key,value) entry using an image key
      void insert(string imagekey_filename, int value);

      // lookup the entry using an image key (exact match)
      int  lookup(string imagekey_filename);

      // lookup the first entry with a similar image key
      int lookup_similar(string imagekey_filename, float similarity_threshold);

      // lookup at most n entries with keys similar to the given image (n = 0 : all)
      vector<int>  lookup_similar_n(string imagekey_filename, float similarity_threshold, int n=0);

      // erase the entry associated with the image
      void erase(string imagekey_filename);


      int  size();  // return the number of image keys stored
      bool empty(); // test whether it is empty

   private:

      vector<VizRecord> records;

      vector<VizRecord>::iterator lookup_record(VizRecord& rec, float similarity_threshold);

      // helper functions
      void     preprocess(IplImage** img);
      VizRecord load_image(string filename);
   };


void 
VizDict::preprocess(IplImage** img){
  if ((*img)->height > 100){
    doPyrDown(img);
  }  
}

VizRecord 
VizDict::load_image(string filename){
  cerr << "VizDict::load_image: " + filename << endl;

  IplImage* img = cvLoadImage(filename.c_str());  


  VizRecord rec;  
  rec.original_height = img->height;
  rec.original_width  = img->width;


  preprocess(&img);

  rec.image = img;

  return rec;
}

int
VizDict::size(){
  return records.size();
}

bool
VizDict::empty(){
  return records.empty();
}

void
VizDict::insert(string filename, int value){

  VizRecord new_rec = load_image(filename);


  vector<VizRecord>::iterator existing_rec = lookup_record(new_rec,1.0);

  // if a record associated with the exact image already exists
  if (existing_rec != records.end() ){

    // update the value
    existing_rec->value = value;

  }else{

    // otherwise, add a new record for this image with the value    
    new_rec.value = value;
    records.push_back(new_rec);
  }
}


static 
bool same(VizRecord& rec1, VizRecord& rec2){

  IplImage* im1 = rec1.image;
  IplImage* im2 = rec2.image;

  bool same_height = rec1.original_height == rec2.original_height;
  bool same_width  = rec1.original_width  == rec2.original_width;

  if (!(same_height && same_width)){
    return false;
  }

  IplImage* diff3 = cvCreateImage(cvSize(im1->width, im1->height), IPL_DEPTH_8U, 3);
  cvAbsDiff(im1,im2,diff3);

  VDict::RgbImage  Idiff3(diff3);

  for (int i=0;i<diff3->height;i++){
    for (int j=0;j<diff3->width;j++){
      if (Idiff3[i][j].r || Idiff3[i][j].g || Idiff3[i][j].b)
        return false;
    }
  }

  return true;
}

static 
double similar(VizRecord& rec1, VizRecord& rec2){

  IplImage* im1 = rec1.image;
  IplImage* im2 = rec2.image;

  bool similar_height = abs(rec1.original_height - rec2.original_height) < 50;
  bool similar_width  = abs(rec1.original_width  - rec2.original_width) < 50;

  if (!(similar_height  && similar_width)){
    return false;
  }

  IplImage* img;
  IplImage* tpl;

  IplImage* smaller;
  IplImage* bigger;
  if (im1->imageSize > im2->imageSize){
    smaller = im2;    
    bigger  = im1;
  }else{
    smaller = im1;    
    bigger  = im2;
  }


  cvSetImageROI(smaller, cvRect(0, 0, min(im1->width,im2->width), min(im1->height,im2->height)));

  tpl = cvCreateImage(cvGetSize(smaller),smaller->depth,smaller->nChannels);  
  cvCopy(smaller,tpl,NULL);

  cvResetImageROI(smaller);

  img = bigger;

  int res_width  = img->width  - tpl->width + 1;
  int res_height = img->height - tpl->height + 1;
  IplImage* res = cvCreateImage( cvSize( res_width, res_height ), IPL_DEPTH_32F, 1 );
  cvMatchTemplate(img, tpl, res, CV_TM_CCOEFF_NORMED );

  CvPoint max_loc;
  double max_score=1.0;

  cvMinMaxLoc( res, 0, &max_score, 0, &max_loc, 0 );

  cvReleaseImage(&tpl);

  return max_score;

}

void 
VizDict::erase(string filename){

  VizRecord key = load_image(filename);

  vector<VizRecord>::iterator it = lookup_record(key, 1.0);

  if (it != records.end()){
    records.erase(it);    
  }
}

vector<VizRecord>::iterator
VizDict::lookup_record(VizRecord& rec1, float similarity_threshold){

  for (vector<VizRecord>::iterator it = records.begin(); it != records.end(); it++){

    VizRecord& rec2 = *it;

    if (similarity_threshold == 1.0){
      if (same(rec1, rec2))
        return it;
    }
    else {
      if (similar(rec1, rec2) >= similarity_threshold)
        return it;
    }
  }

  return records.end();
}

int
VizDict::lookup(string filename){
  return lookup_similar(filename, 1.0);
}


int
VizDict::lookup_similar(string filename, float similarity_threshold){

  VizRecord query = load_image(filename);

  vector<VizRecord>::iterator match = lookup_record(query, similarity_threshold);

  if (match != records.end())
    return match->value;
  else
    return -1;

}



static 
bool descend_sort_function(VDict::Match m1, VDict::Match m2){
  return m1.score > m2.score;
}    

static
void sort_matches(vector<VDict::Match>& matches){    
  sort(matches.begin(), matches.end(), descend_sort_function);    
}


vector<int>
VizDict::lookup_similar_n(string filename, float similarity_threshold, int n){

  vector<int> values;
  vector<VDict::Match> matches;

  VizRecord rec1 = load_image(filename);

  for (vector<VizRecord>::iterator it = records.begin(); it != records.end(); it++){

    VizRecord& rec2 = *it;      
    
    float score = similar(rec1, rec2);

    VDict::Match match;
    match.score = score;
    match.value = it->value;

    if (score >= similarity_threshold)
      matches.push_back(match);

    if (n != 0 && matches.size() == n)
      break;
  }

  sort_matches(matches);
  for (int i=0;i<matches.size();++i){
    values.push_back(matches[i].value);
  }

  return values;
}

//===================================================================
// VizDict test codes
void test_vizdict( int argc, char** argv ) {

  int ret;
  {
    // create an empty vict dict
    VizDict vd;
    assert(vd.size() == 0);
    assert(vd.empty());

    // add the first image key with a value
    vd.insert("1.png",100);
    assert(vd.size() == 1);
    assert(!vd.empty());

    // lookup using an image key
    ret = vd.lookup("1.png");
    assert(ret == 100);

    // lookup using a non-existing image key
    ret = vd.lookup("2.png");
    assert(ret == -1);  // should return an invalid value (-1)

    // add another image key with a value
    vd.insert("2.png",200);

    // add a big image key with a value
    vd.insert("big.png",300);

    // lookup the values for each key
    ret = vd.lookup("1.png");
    assert(ret == 100);

    ret = vd.lookup("2.png");
    assert(ret == 200);

    ret = vd.lookup("big.png");
    assert(ret == 300);

    // lookup using a similar image key  
    ret = vd.lookup_similar("2.png",0.8);
    assert(ret == 200);

    // update the value for an existing image key
    vd.insert("2.png",400);
    ret = vd.lookup("2.png");
    assert(ret == 400);  // should lookup the new value
    assert(vd.size() == 3); // should keep the same size

    // erase a key
    vd.erase("1.png");
    assert(vd.size() == 2); // should decrease the size by one
    assert(vd.lookup("1.png") == -1); // should return an invalid value

    // erase a non-exising key
    vd.erase("1.png");  
    assert(vd.size() == 2); // should not change the size

  }

  // test lookup similar n
  {
    VizDict vd;
    vd.insert("2b.png",1);
    vd.insert("2a.png",2);
    vd.insert("1.png",100);


    vector<int> values;
    values = vd.lookup_similar_n("big.png",0.8,1);
    assert(values.size() == 0);

    values = vd.lookup_similar_n("2.png",0.8,1);
    assert(values.size() == 1);

    // n = 2, lookup two similar image keys
    values = vd.lookup_similar_n("2.png",0.8,2);
    assert(values.size() == 2);
    assert(values[0] == 2);  // check sorted
    assert(values[1] == 1);  
    
    // n = 0, lookup all similar image keys (equivalent to above)
    values = vd.lookup_similar_n("2.png",0.8,0);
    assert(values.size() == 2);
    assert(values[0] == 2);  // check sorted
    assert(values[1] == 1);  


    values = vd.lookup_similar_n("2.png",0,2);
    assert(values.size() == 2);


    // t = 0, n = 0, should return all images
    values = vd.lookup_similar_n("2.png",0,0);
    assert(values.size() == vd.size());


  }

};

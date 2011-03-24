/*
 *  vision.cpp
 *  sikuli
 *
 *  Created by Tom Yeh on 8/1/10.
 *  Copyright 2010 sikuli.org. All rights reserved.
 *
 */

#include "vision.h"
#include "finder.h"
#include "tessocr.h"
#include "imgdb.h"
#include <sys/stat.h> 

#ifdef ENABLE_VISUAL_LOG
   const bool enable_visual_log = true;
#else
   const bool enable_visual_log = false;
#endif 
using namespace sikuli;

bool fileExists(const char* strFilename){
   struct stat stFileInfo;
   return !stat(strFilename,&stFileInfo);
}

FindInput::FindInput(){
   init();
}

FindInput::FindInput(Mat source_, int target_type_){
   init();
   source = source_;
   target_type = target_type_;
}

FindInput::FindInput(const char* source_filename_, int target_type_){
   init();
   setSource(source_filename_);
   target_type = target_type_;
}

FindInput::FindInput(Mat source_, Mat target_){
   init();
   source = source_;
   target = target_;
}

FindInput::FindInput(Mat source_, int target_type_, const char* target_string_){
   init();
   setSource(source_);
   setTarget(target_type_, target_string_);
}

FindInput::FindInput(const char* source_filename_,  int target_type_, const char* target_string_){
   init();
   setSource(source_filename_);
   setTarget(target_type_, target_string_);
}

FindInput::FindInput(Mat source_, const FindInput other){
   source = source_;
   target = other.target;
   target_type = other.target_type;
   target_text = other.target_text;
   bFindingAll = other.bFindingAll;
   similarity = other.similarity;
   limit = other.limit;
}

void 
FindInput::init(){
   target_type = TARGET_TYPE_IMAGE;
   target_text = "";
   similarity = 0.8;
   limit = 100;
   bFindingAll = false;
}

void FindInput::setSource(const char* source_filename){
   if(fileExists(source_filename))
      source = cv::imread(source_filename,1);
}

void FindInput::setTarget(int target_type_, const char* target_string){
   target_type = target_type_;
   
   if (target_type == TARGET_TYPE_TEXT  
       || target_type == TARGET_TYPE_BUTTON){
      target_text = target_string;
   }else if (target_type == TARGET_TYPE_IMAGE){
      
      if(fileExists(target_string))
         target = cv::imread(target_string,1);
   }
}

void FindInput::setSource(Mat source_){
   source = source_;
}

void FindInput::setTarget(Mat target_){
   target = target_;
}

Mat 
FindInput::getSourceMat(){
   return source;
}

Mat 
FindInput::getTargetMat(){
   return target;
}

int 
FindInput::getTargetType(){
   return target_type;
}

void 
FindInput::setFindAll(bool all){
   bFindingAll = all;
}

bool 
FindInput::isFindingAll(){
   return bFindingAll;
}


void 
FindInput::setLimit(int limit_){
   limit = limit_;
}

int 
FindInput::getLimit(){
   return limit;
}

void 
FindInput::setSimilarity(double similarity_){
   similarity = similarity_;
}

double 
FindInput::getSimilarity(){
   return similarity;
}

std::string 
FindInput::getTargetText(){
   return target_text;
}


void
Vision::initOCR(const char* ocrDataPath) {
   OCR::init(ocrDataPath);
}

#include "cvgui.h"
#include "tessocr.h"
static vector<FindResult> 
find_text(FindInput& input){
  
   vector<FindResult> results;
   
   Mat source = input.getSourceMat();
   if(!source.rows || !source.cols) 
      return results;
   
   TextFinder f(source);
   if (input.isFindingAll()){
      
      f.find_all(input.getTargetText().c_str(), input.getSimilarity());
      while (f.hasNext()){
         results.push_back(f.next());
      }
   }
   else{
      f.find(input.getTargetText().c_str(), input.getSimilarity());
      if (f.hasNext())
         results.push_back(f.next());
   }
   
   
   
   Mat result_image = source * 0.5;
   for (vector<FindResult>::iterator it = results.begin();
        it != results.end(); ++it){
      
      FindResult& r = *it;
      
      Point pt(r.x,r.y);
      putText(result_image, input.getTargetText(), pt,  
              FONT_HERSHEY_SIMPLEX, 0.3, Color::RED);
      
   }
   VisualLogger::setEnabled(enable_visual_log);
   VisualLogger::log("FindText-Result", result_image);

   
   return results;
}

static vector<FindResult> 
find_image(FindInput& input){
   
   vector<FindResult> results;
   Mat source = input.getSourceMat();
   Mat image = input.getTargetMat();
   if(!source.rows || !source.cols || !image.rows || !image.cols)
      return results;
      
   TemplateFinder f(source);
   
   if (input.isFindingAll()){
      f.find_all(image, input.getSimilarity());
      while (f.hasNext()){
         results.push_back(f.next());
      }
   }
   else{
      f.find(image, input.getSimilarity());
      if (f.hasNext())
         results.push_back(f.next());
      
   }
   return results;
}


static vector<FindResult> 
find_button(FindInput& input){
   
   vector<FindResult> results;
   
   Mat screen = input.getSourceMat();
   
   vector<Blob> blobs;
   cvgui::findBoxes(screen, blobs);
   
   
   VisualLogger::setEnabled(false);
   
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      Blob& blob = *it;
      
      
      if (blob.width < 10)
         continue;
      
      Util::growRect(blob, -3, 0, screen);
      
      Mat blob_image(screen, blob);
      
      FindResult result(blob.x,blob.y,blob.width,blob.height,1);      
      result.text = Vision::recognize(blob_image);
      
      if (result.text.empty())
         continue;
      
      
      string target_text = input.getTargetText();
      if (!target_text.empty()){
         
         int d;
         d = OCR::findEditDistance(target_text.c_str(),
                                            result.text.c_str(),
                                            3);
         
         if (d < 2){
            results.push_back(result);  
         }
      }else{
         results.push_back(result);
      }
      
      // if we only need to find one result, and we already have one result
      if (!input.isFindingAll() && !results.empty()){
         break;
      }
      
   }
   
   
   Mat result_image = screen * 0.5;
   for (vector<FindResult>::iterator it = results.begin();
        it != results.end(); ++it){
      
      FindResult& r = *it;
      
      Point pt(r.x,r.y);
      putText(result_image, r.text, pt,  
              FONT_HERSHEY_SIMPLEX, 0.3, Color::RED);
      
      
      
   }
   
   VisualLogger::setEnabled(enable_visual_log);
   VisualLogger::log("Buttons-OCR", result_image);
   
   return results;
}


#define PIXEL_DIFF_THRESHOLD 20
#define IMAGE_DIFF_THRESHOLD 20

double
Vision::compare(Mat im1, Mat im2){
   
   Mat gray1;
   Mat gray2;
   
   // convert image from RGB to grayscale
   cvtColor(im1, gray1, CV_RGB2GRAY);
   cvtColor(im2, gray2, CV_RGB2GRAY);
   
   Mat diff1;
   absdiff(gray1,gray2,diff1);
   
   typedef uchar T;
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
   
   // ratio of pixels that are different
   double score = 1.0 * diff_cnt / (im1.rows * im1.cols);
   return score;
}

vector<FindResult> 
Vision::find(FindInput input){
   VisualLogger::next();

   vector<FindResult> results;

   if (input.getTargetType() == TARGET_TYPE_IMAGE){
      
      results = find_image(input);
      
   }
   else if (input.getTargetType() == TARGET_TYPE_TEXT){
    
      results = find_text(input);
      
   }else if (input.getTargetType() == TARGET_TYPE_BUTTON){

      results = find_button(input);
   
   }
   
   vector<FindResult> final_results;
   int n = min((int)results.size(), (int)input.getLimit());
   for (int i=0; i< n; ++i){
      final_results.push_back(results[i]);
   }

   return final_results;
}

vector<FindResult> 
Vision::findChanges(FindInput input){

   vector<FindResult> results;
   Mat source = input.getSourceMat();
   Mat image = input.getTargetMat();
   ChangeFinder f(source);
   
   //f.find(image, input.getSimilarity());
   f.find(image);
   while (f.hasNext() && (results.size() < input.getLimit())){
      results.push_back(f.next());
   }
   
   vector<FindResult> final_results;
   int n = input.getLimit();
   if(n < results.size()){
      copy(results.begin(), results.begin()+n, final_results.begin());
      return final_results;
   }
   else
      return results;
}





string
Vision::recognize(Mat image){
   OCRText text = OCR::recognize(image);
   return text.getString();
}

string
Vision::query(const char* index_filename, cv::Mat image){
   
   Database db;   
   ifstream in(index_filename, ios::binary);
   db.read(in);
   in.close();
   
   
   string ret = "";
   
   vector<ImageRecord> results = db.find(image);   
   for (vector<ImageRecord>::iterator r = results.begin(); 
        r != results.end(); ++r){
      
      ImageRecord& record = *r;
      
      //cout << "ui" << record.id << " ";
      char buf[50];
      sprintf(buf,"ui%d",record.id);
      ret = ret + string(buf) + " ";
   }
   
   return ret;
}


OCRText
Vision::recognize_as_ocrtext(Mat image){
   return OCR::recognize(image);
}

void Vision::findBlobs(const cv::Mat& image, 
                  vector<Blob>& out_text_blobs, vector<Blob>& out_img_blobs){
   cvgui::segmentScreenshot(image, out_text_blobs, out_img_blobs);
}

cv::Mat Vision::createMat(int _rows, int _cols, unsigned char* _data){
   Mat mat_ref = Mat(_rows, _cols, CV_8UC4, _data);
   Mat mat;
   cvtColor(mat_ref, mat, CV_RGBA2BGR, 3);
   //cout << "createMat: " << mat.rows << " " << mat.cols << endl;
   //imwrite("createMat.png", mat);
   return mat;
}

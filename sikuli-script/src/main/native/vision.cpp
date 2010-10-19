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

using namespace sikuli;

FindInput::FindInput(Mat source_, Mat target_){
   source = source_;
   target = target_;
   init();
}

FindInput::FindInput(Mat source_, const char* target_string, bool text){
   init(source_, target_string, text);
}

FindInput::FindInput(const char* source_filename, const char* target_string, bool text){
   source = cv::imread(source_filename,1);
   init(source, target_string, text);
}

void 
FindInput::init(){
   bFindingText = false;
   bFindingAll = false;
}

void
FindInput::init(Mat source_, const char* target_string, bool text){
   init();
   
   source = source_;
   
   if (text){
      targetText = target_string;
   }else{
      target = cv::imread(target_string,1);
   }
   
   bFindingText = text;
}


Mat 
FindInput::getSourceMat(){
   return source;
}

Mat 
FindInput::getTargetMat(){
   return target;
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

bool 
FindInput::isFindingText(){
   return bFindingText;
}

std::string 
FindInput::getTargetText(){
   return targetText;
}


void
Vision::initOCR(const char* ocrDataPath) {
   OCR::init(ocrDataPath);
}

vector<FindResult> 
find_helper(FindInput& input){
   
   vector<FindResult> results;
   
   if (input.isFindingText()){
      
      
      TextFinder f(input.getSourceMat());
      f.find(input.getTargetText().c_str(), input.getSimilarity());
      
      if (input.isFindingAll()){
         while (f.hasNext()){
            results.push_back(f.next());
         }
      }
      else{
         if (f.hasNext())
            results.push_back(f.next());
      }
      
      
      
   }else{
      
      TemplateFinder f(input.getSourceMat());
	   Mat image = input.getTargetMat();
      
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
   }
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

   vector<FindResult> results;
   results = find_helper(input);
   
   vector<FindResult> final_results;
   int n = min((int)results.size(), (int)input.getLimit());
   for (int i=0; i< n; ++i){
      final_results.push_back(results[i]);
   }

   return final_results;
}


string
Vision::recognize(Mat image){

   OCRText text = OCR::recognize(image);
   return text.getString();
}

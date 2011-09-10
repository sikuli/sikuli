/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */

#include "pyramid-template-matcher.h"
#include "TimingBlock.h"
#include "vision.h"

// select how images are template matched
#define USE_SQRDIFF_NORMED 0
#define USE_CCORR_NORMED 1

// select how images are downsampled
#define USE_RESIZE 1
#define USE_PYRDOWN 0

#ifdef DEBUG
#define dout std::cerr
#else
#define dout if(0) std::cerr
#endif

#define MIN_PIXELS_TO_USE_GPU 90000
#define WORTH_GPU(mat) ( ((mat).rows * (mat).cols) > MIN_PIXELS_TO_USE_GPU)


void PyramidTemplateMatcher::init() {
   _use_gpu = false;
   _hasMatchedResult = false;
}


PyramidTemplateMatcher::PyramidTemplateMatcher(const MatchingData& data, int levels, float _factor)
: factor(_factor), source(data.source), target(data.target), lowerPyramid(NULL)
{ 
   if (source.rows < target.rows || source.cols < target.cols)
      return;

   init();
#ifdef ENABLE_GPU
   if(sikuli::Vision::getParameter("GPU") && WORTH_GPU(source)){
      if(gpu::getCudaEnabledDeviceCount()>0) 
         _use_gpu = true;
      //cout << source.rows << "x" << source.cols << endl; 
   }
#endif
   if (levels > 0)
      lowerPyramid = createSmallMatcher(levels-1);
}



PyramidTemplateMatcher::~PyramidTemplateMatcher(){
   if (lowerPyramid != NULL)
      delete lowerPyramid;   
};


PyramidTemplateMatcher* PyramidTemplateMatcher::createSmallMatcher(int level){
      TimingBlock t("PyramidTemplateMatcher::createSmallMatcher");
      Mat smallSource, smallTarget;
      
#if USE_PYRDOWN
         // Faster
      pyrDown(source, smallSource);
      pyrDown(target, smallTarget);
#endif
#if USE_RESIZE
      resize(source, smallSource, Size(source.cols/factor,source.rows/factor),INTER_NEAREST);
      resize(target, smallTarget, Size(target.cols/factor,target.rows/factor),INTER_NEAREST);      
#endif
      MatchingData data(smallSource, smallTarget);
      return new PyramidTemplateMatcher(data, level, factor);
}

double PyramidTemplateMatcher::findBest(const Mat& source, const Mat& target, Mat& out_result, Point& out_location){
      TimingBlock t("PyramidTemplateMatcher::findBest");
      double out_score;
#ifdef ENABLE_GPU
      if(_use_gpu){
         gpu::GpuMat gSource, gTarget;
         gSource.upload(source);
         gTarget.upload(target);
         gpu::matchTemplate(gSource,gTarget,gResult,CV_TM_CCOEFF_NORMED);
         gpu::minMaxLoc(gResult, NULL, &out_score, NULL, &out_location);
         return out_score;
      }
#endif

#if USE_SQRDIFF_NORMED
      matchTemplate(source,target,out_result,CV_TM_SQDIFF_NORMED);   
      result = Mat::ones(out_result.size(), CV_32F) - result;
#else
      Scalar mean, stddev;
      meanStdDev( target, mean, stddev );
      if(stddev[0]+stddev[1]+stddev[2]+stddev[3] < DBL_EPSILON){ // pure color target
         if(mean[0]+mean[1]+mean[2]+mean[3] < DBL_EPSILON){ // black target
            Mat inv_source, inv_target;
            bitwise_not(source, inv_source);
            bitwise_not(target, inv_target);
            matchTemplate(inv_source,inv_target,out_result,CV_TM_SQDIFF_NORMED);   
         } 
         else
            matchTemplate(source,target,out_result,CV_TM_SQDIFF_NORMED);   
         result = Mat::ones(out_result.size(), CV_32F) - result;
      }
      else
         matchTemplate(source,target,out_result,CV_TM_CCOEFF_NORMED);
#endif
      minMaxLoc(result, NULL, &out_score, NULL, &out_location);
      return out_score;
}

void PyramidTemplateMatcher::eraseResult(int x, int y, int xmargin, int ymargin){
   int x0 = max(x-xmargin,0);
   int y0 = max(y-ymargin,0);
#ifdef ENABLE_GPU
   int rows = _use_gpu? gResult.rows : result.rows;
   int cols = _use_gpu? gResult.cols : result.cols;
#else
   int rows = result.rows;
   int cols = result.cols;
#endif
   int x1 = min(x+xmargin,cols);  // no need to blank right and bottom
   int y1 = min(y+ymargin,rows);

#ifdef ENABLE_GPU
   if(_use_gpu)
      gResult(Range(y0, y1), Range(x0, x1)) = 0.f;
   else
#endif
   {
      result(Range(y0, y1), Range(x0, x1)) = 0.f;
   }
}

FindResult PyramidTemplateMatcher::next(){
   TimingBlock tb("PyramidTemplateMatcher::next()");
   if (source.rows < target.rows || source.cols < target.cols){
      //std:cerr << "PyramidTemplateMatcher: source is smaller than the target" << endl;
      return FindResult(0,0,0,0,-1);
   }
   if (lowerPyramid != NULL)
      return nextFromLowerPyramid();

   double detectionScore;
   Point detectionLoc;
   if(!_hasMatchedResult){
      detectionScore = findBest(source, target, result, detectionLoc);
      _hasMatchedResult = true;
   }
   else{
#ifdef ENABLE_GPU
      if(_use_gpu)
         gpu::minMaxLoc(gResult, NULL, &detectionScore, NULL, &detectionLoc);
      else
#endif
      {
         minMaxLoc(result, NULL, &detectionScore, NULL, &detectionLoc);
      }

   }

   int xmargin = target.cols/3;
   int ymargin = target.rows/3;
   eraseResult(detectionLoc.x, detectionLoc.y, xmargin, ymargin);
   
   return FindResult(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);
}

FindResult PyramidTemplateMatcher::nextFromLowerPyramid(){
   FindResult match = lowerPyramid->next();

   int x = match.x*factor;
   int y = match.y*factor;
   
   // compute the parameter to define the neighborhood rectangle
   int x0 = max(x-(int)factor,0);
   int y0 = max(y-(int)factor,0);
   int x1 = min(x+target.cols+(int)factor,source.cols);
   int y1 = min(y+target.rows+(int)factor,source.rows);
   Rect roi(x0,y0,x1-x0,y1-y0);
   Mat roiOfSource(source, roi);

   Point detectionLoc;
   double detectionScore = findBest(roiOfSource, target, result, detectionLoc);

   detectionLoc.x += roi.x;
   detectionLoc.y += roi.y;

   return FindResult(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);
}

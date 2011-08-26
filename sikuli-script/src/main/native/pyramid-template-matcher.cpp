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
#define RESIZE_STEP 3
#define USE_RESIZE 1
#define USE_PYRDOWN 0

#ifdef DEBUG
#define dout std::cerr
#else
#define dout if(0) std::cerr
#endif

PyramidTemplateMatcher* PyramidTemplateMatcher::createSmallMatcher(int level){
      Mat smallSource, smallTarget;
      TimingBlock* t = new TimingBlock("downsampling");
      
#if USE_PYRDOWN
         // Faster
      pyrDown(source, smallSource);
      pyrDown(target, smallTarget);
#endif
#if USE_RESIZE
      resize(source, smallSource, Size(1.0*source.cols/factor,source.rows*1.0/factor),INTER_NEAREST);
      resize(target, smallTarget, Size(1.0*target.cols/factor,target.rows*1.0/factor),INTER_NEAREST);      
#endif
      delete t;
      return new PyramidTemplateMatcher(smallSource, smallTarget, level, factor);
}

PyramidTemplateMatcher::PyramidTemplateMatcher(Mat _source, Mat _target, int levels, float _factor)
: factor(_factor), source(_source), target(_target), lowerPyramid(NULL)
{ 

   TimingBlock tb("PyramidTemplateMatcher()");
   if (source.rows < target.rows || source.cols < target.cols){
      //std:cerr << "PyramidTemplateMatcher: source is smaller than the target" << endl;
      return;
   }
   
   if (levels > 0){
      lowerPyramid = createSmallMatcher(levels-1);
   }else{
      TimingBlock t("matchTemplate");
#if USE_SQRDIFF_NORMED
      matchTemplate(source,target,result,CV_TM_SQDIFF_NORMED);
      result = Mat::ones(result.size(), CV_32F) - result;
#else
      matchTemplate(source,target,result,CV_TM_CCOEFF_NORMED);
#endif
   }
};



PyramidTemplateMatcher::~PyramidTemplateMatcher(){
   if (lowerPyramid != NULL)
      delete lowerPyramid;   
};

double PyramidTemplateMatcher::findBest(const Mat& source, const Mat& target, Mat& out_result, Point& out_location){
      TimingBlock t("PyramidTemplateMatcher::findBest");
      double out_score;
#if USE_SQRDIFF_NORMED
      matchTemplate(source,target,out_result,CV_TM_SQDIFF_NORMED);   
      result = Mat::ones(out_result.size(), CV_32F) - result;
#else
      matchTemplate(source,target,out_result,CV_TM_CCOEFF_NORMED);
#endif
      minMaxLoc(result, NULL, &out_score, NULL, &out_location);
      return out_score;
}

FindResult PyramidTemplateMatcher::next(){
   TimingBlock tb("PyramidTemplateMatcher::next()");
   
   if (source.rows < target.rows || source.cols < target.cols){
      return FindResult(0,0,0,0,-1);
   }
   
   int x, y;
   int xmargin, ymargin;
   if (lowerPyramid == NULL){
      double detectionScore;
      Point detectionLoc;
      minMaxLoc(result, NULL, &detectionScore, NULL, &detectionLoc);

      xmargin = target.cols/3;
      ymargin = target.rows/3;
      
      x = detectionLoc.x;
      y = detectionLoc.y;

      int x0 = max(x-xmargin,0);
      int y0 = max(y-ymargin,0);
      int x1 = min(x+xmargin,result.cols);  // no need to blank right and bottom
      int y1 = min(y+ymargin,result.rows);
      
      result(Range(y0, y1), Range(x0, x1)) = 0.f;
      
      return FindResult(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);;
   }
   else{
      FindResult match = lowerPyramid->next();
      
      x = match.x*factor;
      y = match.y*factor;
      //
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
   
   
};

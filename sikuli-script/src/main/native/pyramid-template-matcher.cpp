/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */

#include "pyramid-template-matcher.h"
#include "TimingBlock.h"

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

PyramidTemplateMatcher::PyramidTemplateMatcher(Mat _source, Mat _target, int levels, float _factor)
: factor(_factor), source(_source), target(_target), lowerPyramid(NULL)
{ 

   TimingBlock tb("PyramidTemplateMatcher()");
   if (source.rows < target.rows || source.cols < target.cols){
      //std:cerr << "PyramidTemplateMatcher: source is smaller than the target" << endl;
      return;
   }
   
   Size sourceSize = source.size();
   Size targetSize = target.size();
   
   
   if (levels > 0){
      
      copyOfSource = source.clone();
      copyOfTarget = target.clone();
      
      
      Mat smallSource;
      Mat smallTarget;

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
      
      /*
      Mat halfTarget;
      if (smallTarget.cols > smallTarget.rows){
         halfTarget = Mat(smallTarget, Range(1,smallTarget.rows/2), Range(1,smallTarget.cols/2));
      }else{
         halfTarget = Mat(smallTarget, Range(1,smallTarget.rows/2),  Range(1,smallTarget.cols/2));
      }
      */
      
      lowerPyramid = new PyramidTemplateMatcher(smallSource, smallTarget, levels - 1, factor);
      
   }else{
      

      lowerPyramid = NULL;
      
      Size resultSize;
      resultSize.width = sourceSize.width - targetSize.width + 1;
      resultSize.height = sourceSize.height - targetSize.height + 1;      
      
      result.create(resultSize,CV_32FC1);
      dout << "[" << sourceSize.width << " x " << sourceSize.height << "] ";
      dout << "[" << targetSize.width << " x " << targetSize.height << "]" << endl;


      TimingBlock* t = new TimingBlock("matching");

#if USE_SQRDIFF_NORMED
      matchTemplate(source,target,result,CV_TM_SQDIFF_NORMED);
      result = Mat::ones(result.size(), CV_32F) - result;
#else
      matchTemplate(source,target,result,CV_TM_CCOEFF_NORMED);
#endif
      delete t;

   
      
   }
};



PyramidTemplateMatcher::~PyramidTemplateMatcher(){
   if (lowerPyramid != NULL)
      delete lowerPyramid;   
};

FindResult PyramidTemplateMatcher::next(){
   TimingBlock tb("PyramidTemplateMatcher::next()");
   
   if (source.rows < target.rows || source.cols < target.cols){
      return FindResult(0,0,0,0,-1);
   }
   
   if (lowerPyramid == NULL){
      
      // find the best match location
      double minValue, maxValue;
      Point minLoc, maxLoc;
      minMaxLoc(result, &minValue, &maxValue, &minLoc, &maxLoc);
      
      double detectionScore = maxValue;
      Point detectionLoc = maxLoc;
      
      int xmargin = target.cols/3;
      int ymargin = target.rows/3;
      
      int& x = detectionLoc.x;
      int& y = detectionLoc.y;
      
      int x0 = max(x-xmargin,0);
      int y0 = max(y-ymargin,0);
      int x1 = min(x+xmargin,result.cols);  // no need to blank right and bottom
      int y1 = min(y+ymargin,result.rows);
      
      rectangle(result, Point(x0, y0), Point(x1-1, y1-1), 
              Scalar(0), CV_FILLED);
      
      return FindResult(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);;
      
      
   }else{
      
      FindResult match = lowerPyramid->next();
      
      int x = match.x*factor;
      int y = match.y*factor;
      
      // compute the parameter to define the neighborhood rectangle
      int x0 = max(x-(int)factor,0);
      int y0 = max(y-(int)factor,0);
      int x1 = min(x+target.cols+(int)factor,source.cols);
      int y1 = min(y+target.rows+(int)factor,source.rows);
      Rect roi(x0,y0,x1-x0,y1-y0);
      
      
      Mat roiOfSource(copyOfSource, roi);
      Size resultSize;
      resultSize.width = roiOfSource.size().width - target.size().width + 1;
      resultSize.height = roiOfSource.size().height - target.size().height + 1;      
      
      result.create(resultSize, CV_32FC1);
   
      TimingBlock* t = new TimingBlock("matching");
#if USE_SQRDIFF_NORMED
      matchTemplate(roiOfSource,target,result,CV_TM_SQDIFF_NORMED);   
      result = Mat::ones(result.size(), CV_32F) - result;
#else
      matchTemplate(roiOfSource,target,result,CV_TM_CCOEFF_NORMED);
#endif
      delete t;
      
      double minValue, maxValue;
      Point minLoc, maxLoc;
      minMaxLoc(result, &minValue, &maxValue, &minLoc, &maxLoc);
      
      double detectionScore = maxValue;
      Point detectionLoc = maxLoc;

      detectionLoc.x += roi.x;
      detectionLoc.y += roi.y;
      
      
      return FindResult(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);
      
   }
   
   
};

/*
 *  pyramid-template-matcher.cpp
 *  vision
 *
 *  Created by Tom Yeh on 5/1/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "pyramid-template-matcher.h"

PyramidTemplateMatcher::PyramidTemplateMatcher(Mat _source, Mat _target, int levels){
	
	source = _source;
	target = _target;
	
	Size sourceSize = source.size();
    Size targetSize = target.size();
	
	
	if (levels > 0){
		
		copyOfSource = source.clone();
		copyOfTarget = target.clone();
		
		
		Mat smallSource(sourceSize, source.type());
		pyrDown(source, smallSource);
		
		Mat smallTarget(targetSize, source.type());
		pyrDown(target, smallTarget);	
		
		lowerPyramid = new PyramidTemplateMatcher(smallSource, smallTarget, levels - 1);
		
	}else{
		
		lowerPyramid = NULL;
		
		Size resultSize;
		resultSize.width = sourceSize.width - targetSize.width + 1;
		resultSize.height = sourceSize.height - targetSize.height + 1;		
		
		result.create(resultSize,CV_32FC1);
		cout << "[" << sourceSize.width << " x " << sourceSize.height << "]" << endl;
		matchTemplate(source,target,result,CV_TM_CCOEFF_NORMED);
	}
};



PyramidTemplateMatcher::~PyramidTemplateMatcher(){
	if (lowerPyramid != NULL)
		delete lowerPyramid;	
};

Match PyramidTemplateMatcher::next(){
	
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
		
		return Match(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);;
		
		
	}else{
		
		Match match = lowerPyramid->next();
		
		int x = match.x*2;
		int y = match.y*2;
		
		// compute the parameter to define the neighborhood rectangle
		int x0 = max(x-2,0);
		int y0 = max(y-2,0);
		int x1 = min(x+target.cols+2,source.cols);
		int y1 = min(y+target.rows+2,source.rows);
		Rect roi(x0,y0,x1-x0,y1-y0);
		
		
		Mat roiOfSource(copyOfSource, roi);
		Size resultSize;
		resultSize.width = roiOfSource.size().width - target.size().width + 1;
		resultSize.height = roiOfSource.size().height - target.size().height + 1;		
		
		result.create(resultSize, CV_32FC1);
		matchTemplate(roiOfSource,target,result,CV_TM_CCOEFF_NORMED);
		
		double minValue, maxValue;
        Point minLoc, maxLoc;
        minMaxLoc(result, &minValue, &maxValue, &minLoc, &maxLoc);
		
		double detectionScore = maxValue;
		Point detectionLoc = maxLoc;
		detectionLoc.x += roi.x;
		detectionLoc.y += roi.y;
		
		return Match(detectionLoc.x,detectionLoc.y,target.cols,target.rows,detectionScore);
		
	}
	
	
};
/*
 *  screen-imgproc.cpp
 *  sikuli
 *
 *  Created by Tom Yeh on 9/8/10.
 *  Copyright 2010 sikuli.org. All rights reserved.
 *
 */

#include "cvgui.h"

#include <iostream>
using namespace std;


Scalar Color::RED(0,0,255);
Scalar Color::WHITE(255,255,255);

Scalar Color::RANDOM() { 
   return Scalar(rand()&255, rand()&255, rand()&255);
};

static bool sort_by_x (Rect a, Rect b){ 
	return (a.x < b.x); 
}

static bool sort_blob_by_x(Blob a, Blob b){ 
	return (a.x < b.x); 
}

static bool sort_blob_by_y(Blob a, Blob b){ 
	return (a.y < b.y); 
}

#define SHOW(x) namedWindow(#x,1); imshow(#x,x);
#define SHOW0(x) namedWindow(#x,1); imshow(#x,x); waitKey(0);

//class VisualLogger{
//   
//   
//   int image_i;
//   int step_i;
//   char* prefix;
//   
//public:
//   
//   VisualLogger(){
//   }
//   
//   static void newImage(){
//      image_i++;
//      step_i = 0;
//   }
//   
//   static void log(const char* name, const Mat& image){
//      char buf[200];
//      
//      if (prefix){
//         
//         sprintf(buf, "%s-%02d-%s.vlog.png", prefix, step_i, name);
//         
//      }else{
//         sprintf(buf, "%03d-%02d-%s.vlog.png",image_i,step_i,name);           
//      }
//      
//      imwrite(buf, image);
//      
//      step_i++;  
//   }
//};
//
//static VisualLogger vlog;

int VisualLogger::image_i = 0;
int VisualLogger::step_i = 0;
bool VisualLogger::enabled = true;
char* VisualLogger::prefix = 0;

//#define VLOG(x,y) 

void
Util::rgb2grayC3(const Mat& input, Mat& output){
   Mat g;
   cvtColor(input, g, CV_RGB2GRAY);   
   Mat ss[3] = {g,g,g};
   merge(ss,3,output);   
}

void 
Util::growRect(Rect& rect, int xd, int yd, Rect bounds){
   
   
   Rect in = rect;
   in.x -= xd;
   in.width += 2*xd;
   in.y -= yd;
   in.height += 2*yd;
   
   int x1 = max(bounds.x, in.x);
   int y1 = max(bounds.y, in.y);
   int x2 = min(in.x + in.width, bounds.width);
   int y2 = min(in.y + in.height, bounds.height);
	
   if (x2 < x1 || y2 < y1)
      rect = Rect(0,0,0,0);
   else
      rect = Rect(x1,y1,x2-x1+1,y2-y1+1);
   
}

void 
Util::growRect(Rect& rect, int xd, int yd, cv::Mat image){
   growRect(rect, xd, yd, Rect(0,0, image.cols-1, image.rows-1));
}



void 
Painter::drawRect(Mat& image, Rect r, Scalar color){
   rectangle(image, 
             Point(r.x, r.y),
             Point(r.x + r.width, r.y + r.height),
             color);   
}

void 
Painter::drawRects(Mat& image, vector<Rect>& rects, Scalar color){
   for (vector<Rect>::iterator r = rects.begin();
        r != rects.end(); ++r){
      
      rectangle(image, 
                Point(r->x, r->y),
                Point(r->x + r->width, r->y + r->height),
                color);
   }
}

void 
Painter::drawRects(Mat& image, vector<Rect>& rects){
   drawRects(image, rects, Scalar(0,0,255));
}

void 
Painter::drawBlobs(Mat& image, vector<Blob>& blobs){
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& blob = *it;
      
      Scalar color(blob.mr,blob.mg,blob.mb);
      vector<Rect> rs;
      rs.push_back(blob);
      drawRects(image, rs, color);
   }
}

void 
Painter::drawBlobs(Mat& image, vector<Blob>& blobs, Scalar color){
   vector<Rect> rs;

   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& blob = *it;
      rs.push_back(blob);
   }
   drawRects(image, rs, color);
}

void 
Painter::drawLineBlobs(Mat& image, vector<LineBlob>& lineblobs, Scalar color){
   for (vector<LineBlob>::iterator it1 = lineblobs.begin();
        it1 != lineblobs.end(); ++it1){
      
      LineBlob& lineblob = *it1;
      
      
      if (lineblob.blobs.size()>=2){
         
         for (vector<Blob>::iterator r = lineblob.blobs.begin() + 1;
              r != lineblob.blobs.end(); ++r){
            
            Rect previous = *(r-1);
            Rect current = *r;
            
            Point from(previous.x+previous.width,previous.y);
            Point to(current.x,current.y);            
            cv::line(image, from, to, Scalar(255,255,255));
         }
      }
      
      drawRect(image, lineblob, color);
   }
}

void 
Painter::drawParagraphBlobs(Mat& image, vector<ParagraphBlob> blobs, Scalar color){
   for (vector<ParagraphBlob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      ParagraphBlob& parablob = *it;      
      for (vector<LineBlob>::iterator it1 = parablob.lineblobs.begin();
           it1 != parablob.lineblobs.end(); ++it1){
         
         LineBlob& lineblob = *it1;
         drawRect(image, lineblob, Scalar(255,255,0));
      }
      
      drawRect(image, parablob, Scalar(0,0,255));
      
   }
}

void 
Painter::drawOCRWord(Mat& ocr_result_image, OCRWord ocrword){
   
//   cout << ocrword.x << " " << ocrword.y <<  " " << ocrword.width <<  " " << ocrword.height << ":";
   
   vector<OCRChar> chars = ocrword.getChars();
   for (vector<OCRChar>::iterator it = chars.begin(); 
        it != chars.end(); ++it){
      
      OCRChar& ocrchar = *it;
      char ch = ocrchar.ch;
      
      char buf[2];
      buf[0] = ch;
      buf[1] = 0;
      
//      cout << buf;
      
     
      Point pt(ocrchar.x, ocrword.y + ocrword.height - 10);      
      
      putText(ocr_result_image, buf, pt,  
                        FONT_HERSHEY_SIMPLEX, 0.4, Color::RED);
   //           FONT_HERSHEY_PLAIN, 0.8, Color::RED);
      
   }
//   cout << "|" << ocrword.getString() << endl;

}

void 
Painter::drawOCRLine(Mat& ocr_result_image, OCRLine ocrline){
   vector<OCRWord> ocrwords = ocrline.getWords();
   for (vector<OCRWord>::iterator it = ocrwords.begin(); it != ocrwords.end(); ++it){
      OCRWord& ocrword = *it;
      drawOCRWord(ocr_result_image, ocrword);
   }
}

void 
Painter::drawOCRParagraph(Mat& ocr_result_image, OCRParagraph ocrpara){
   vector<OCRLine> ocrlines = ocrpara.getLines();
   for (vector<OCRLine>::iterator it = ocrlines.begin(); it != ocrlines.end(); ++it){
      OCRLine& ocrline = *it;
      drawOCRLine(ocr_result_image, ocrline);
   }
}

void 
Painter::drawOCRText(Mat& ocr_result_image, OCRText ocrtext){
   vector<OCRParagraph> ocrparas = ocrtext.getParagraphs();
   for (vector<OCRParagraph>::iterator it = ocrparas.begin(); it != ocrparas.end(); ++it){
      OCRParagraph& ocrpara = *it;
      drawOCRParagraph(ocr_result_image, ocrpara);
   }
}


void extractFeatures(Mat src){
   
   
   // dimensions
   cout << src.rows << endl;
   cout << src.cols << endl;
   
   
   Mat srcgray;
   cvtColor(src,srcgray, CV_RGB2GRAY);
   
   adaptiveThreshold(srcgray, srcgray, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 3, 1);
   
   SHOW(srcgray);
   
   cout << countNonZero(srcgray) << endl;
   
   Moments mnts = moments(srcgray);
   
   cout << mnts.m00 << "," << mnts.m11 << endl;
   
   Mat hsv;
   cvtColor(src, hsv, CV_BGR2HSV);
   
   // let's quantize the hue to 30 levels
   // and the saturation to 32 levels
   int hbins = 30, sbins = 32;
   int histSize[] = {hbins, sbins};
   // hue varies from 0 to 179, see cvtColor
   float hranges[] = { 0, 180 };
   // saturation varies from 0 (black-gray-white) to
   // 255 (pure spectrum color)
   float sranges[] = { 0, 256 };
   const float* ranges[] = { hranges, sranges };
   MatND hist;
   // we compute the histogram from the 0-th and 1-st channels
   int channels[] = {0, 1};
   
   calcHist( &hsv, 1, channels, Mat(), // do not use mask
            hist, 2, histSize, ranges,
            true, // the histogram is uniform
            false );
   
   double maxVal=0;
   minMaxLoc(hist, 0, &maxVal, 0, 0);
   
   int scale = 10;
   Mat histImg = Mat::zeros(sbins*scale, hbins*10, CV_8UC3);
   
   for( int h = 0; h < hbins; h++ )
      for( int s = 0; s < sbins; s++ )
      {
         float binVal = hist.at<float>(h, s);
         int intensity = cvRound(binVal*255/maxVal);
         rectangle( histImg, Point(h*scale, s*scale),
                   Point( (h+1)*scale - 1, (s+1)*scale - 1),
                   Scalar::all(intensity),
                   CV_FILLED );
      }
   
   namedWindow( "Source", 1 );
   imshow( "Source", src );
   
   namedWindow( "H-S Histogram", 1 );
   imshow( "H-S Histogram", histImg );
   
   //flann::IndexParams );
   
   cv::flann::Index idx(src, cv::flann::KDTreeIndexParams(4));
   
   //cvflann::Index idx;
   
   waitKey(0);
   
}


void
LineBlob::merge(LineBlob& blob){
   
   for (vector<Blob>::iterator it = blob.blobs.begin();
        it != blob.blobs.end(); ++it){
      add(*it);
   }
   
}

void
LineBlob::updateBoundingRect(Blob& blob){
   if (blobs.size() == 0){
      x = blob.x;
      y = blob.y;
      height = blob.height;
      width = blob.width;
      
   }else{
      
      int x1,y1,x2,y2;
      x1 = min(x, blob.x);
      y1 = min(y, blob.y);
      x2 = max(x+width, blob.x + blob.width);
      y2 = max(y+height, blob.y + blob.height);
      
      x = x1;
      y = y1;
      height = y2 - y1;
      width = x2 - x1;
   }
}

void
LineBlob::add(Blob& blob){
   updateBoundingRect(blob);
   blobs.push_back(blob);
}

void
LineBlob::calculateBoundingRectangle(){
   int x1,y1,x2,y2;
   Blob& first_blob = blobs.front();
   x1 = first_blob.x;
   x2 = first_blob.x + first_blob.width;
   y1 = first_blob.y;
   y2 = first_blob.y + first_blob.height;
   
   for (vector<Blob>::iterator it_line = blobs.begin()+1; 
        it_line != blobs.end(); ++it_line){
      Blob& b = *it_line;
      x1 = min(x1, b.x);
      y1 = min(y1, b.y);
      x2 = max(x2, b.x + b.width);
      y2 = max(y2, b.y + b.height);
   }
   
   x = x1;
   y = y1;
   height = y2 - y1;
   width = x2 - x1;
}

void
ParagraphBlob::add(LineBlob& blob){
   
   if (lineblobs.size() == 0){
      x = blob.x;
      y = blob.y;
      height = blob.height;
      width = blob.width;
      
   }else{
      
      int x1,y1,x2,y2;
      x1 = min(x, blob.x);
      y1 = min(y, blob.y);
      x2 = max(x+width, blob.x + blob.width);
      y2 = max(y+height, blob.y + blob.height);
      
      x = x1;
      y = y1;
      height = y2 - y1;
      width = x2 - x1;
   }
   lineblobs.push_back(blob);
}



void denoise(Mat& src){
   
   //src = src * 0.1;
   
   Mat kernel = Mat::ones(3,3,CV_32FC1);
   kernel.at<float>(2,2) = 0;
   
   Mat srcF, destF;
   src.convertTo(srcF, CV_32FC1, 0.1);
   
   filter2D(srcF, destF, -1, kernel);
   
   Mat destU;
   destF.convertTo(destU, CV_8UC1);
   
   threshold(destU, destU, 60, 255, THRESH_BINARY);
   
   bitwise_and(src, destU, src);
}




void
cvgui::linkLineBlobsIntoPagagraphBlobs(vector<LineBlob>& blobs, vector<ParagraphBlob>& parablobs){
   
   sort(blobs, sort_blob_by_y);
   
   for (vector<LineBlob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      LineBlob& blob = *it;
      
      vector<ParagraphBlob>::iterator it1;
      for (it1 = parablobs.begin(); 
           it1 != parablobs.end(); ++it1){
         
         ParagraphBlob& parablob = *it1;
         
         bool left_aligned = abs(parablob.x - blob.x) < 10;
         bool small_vertical_spacing = abs(blob.y - (parablob.y + parablob.height)) < 15;
         
         bool same_paragraph = left_aligned & small_vertical_spacing; 
         
         if (same_paragraph){
            parablob.add(blob);
            break;
         }
      }
      
      if (it1 ==  parablobs.end()){
         ParagraphBlob new_parablob;
         new_parablob.add(blob);
         parablobs.push_back(new_parablob);
      }
   }
}

void
cvgui::mergeLineBlobs(vector<LineBlob>& blobs, vector<LineBlob>& merged_blobs){
   
   sort(blobs, sort_blob_by_x);
   
   for (vector<LineBlob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      LineBlob& blob = *it;
      
      vector<LineBlob>::iterator l;
      for (l = merged_blobs.begin(); l != merged_blobs.end(); ++l){
         
         LineBlob& merged_blob = *l;
         
         bool similar_baseline = abs((merged_blob.y + merged_blob.height) - (blob.y + blob.height)) < 5;
         bool small_spacing = (blob.x - (merged_blob.x + merged_blob.width)) < 10;
         
         bool cond1 = merged_blob.isContainedBy(blob) || blob.isContainedBy(merged_blob);
         
         bool same_line = cond1 || (similar_baseline && small_spacing);
         
         if (same_line){
            merged_blob.merge(blob);
            break;
         }
      }
      
      if (l == merged_blobs.end()){
         merged_blobs.push_back(blob);
      }
      
   }
}

void
cvgui::linkBlobsIntoLineBlobs(vector<Blob>& blobs, vector<LineBlob>& lines, int max_spacing){
   
   
   sort(blobs, sort_blob_by_x);
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& blob = *it;
      
      vector<LineBlob>::iterator l;
      for (l = lines.begin(); l != lines.end(); ++l){
         
         LineBlob& line = *l;
         Blob& last = line.blobs.back();
         
         bool similar_baseline = abs((last.y + last.height) - (blob.y + blob.height)) < 5;
         bool similar_centerline = abs((last.y + last.height/2) - (blob.y + blob.height/2)) < 5;
         bool similar_height = 1.0*min(last.height,blob.height)/max(last.height,blob.height) > 0.5;
         bool similar_width = 1.0*min(last.width,blob.width)/max(last.width,blob.width) > 0.5;
         
         bool small_overlap = blob.x > ((last.x + last.width)-2);
         
         
         bool small_spacing = (blob.x - (last.x + last.width)) < max_spacing;
         
         bool is_dot = blob.height < 3 && blob.width < 3 && similar_baseline && small_spacing;
         
         int th = 40;
         bool similar_foreground_color = 
         abs(last.mr - blob.mr) < th && abs(last.mg - blob.mg) < th && abs(last.mb - blob.mb) < th;
         
         
         //bool is_probably_i = blob.width < 2 || last.width < 2;
         
         
         bool same_line = (similar_centerline || similar_baseline)
         && similar_height
         && small_overlap
         //&& similar_width
         && small_spacing
         && similar_foreground_color; //|| is_probably_i);
         
         
         bool very_close = (blob.x - (last.x + last.width)) < 3;
         
         //         bool vertically_overlap = similar_baseline;
         
         if (same_line || (very_close && similar_height && similar_baseline)){
            line.add(blob);
            break;
         }
      }
      
      if (l == lines.end()){
         
         LineBlob newline;
         newline.add(blob);
         lines.push_back(newline);
      }
      
   }
}


void 
cvgui::run_ocr_on_lineblobs(vector<LineBlob>& ocr_input_lineblobs,
                            Mat& input_image,
                            vector<OCRLine>& ocrlines){
   
   
   for (vector<LineBlob>::iterator it = ocr_input_lineblobs.begin(); 
        it != ocr_input_lineblobs.end(); ++it){
      
      LineBlob& lineblob = *it;
      OCRLine ocrline;
      OCRWord ocrword;
      
      // if (lineblob.blobs.size()<10)
      //         continue;
      //      
      
      
      Mat wordImage(input_image,lineblob);
      
      Mat ocrImage;  // the image passed to tesseract      
      bool upsampled = false;
      if (wordImage.rows < 20){
         upsampled = true;
         resize(wordImage, ocrImage, Size(wordImage.cols*2,wordImage.rows*2));
      }else {
         ocrImage = wordImage.clone(); 
      }  
      
      vector<OCRChar> ocr_chars;
      ocr_chars = OCR::recognize((unsigned char*)ocrImage.data,
                                            ocrImage.cols,
                                            ocrImage.rows,
                                            8); 
      
      for (vector<OCRChar>::iterator iter = ocr_chars.begin(); 
           iter != ocr_chars.end(); iter++){
         
         
         OCRChar& ocrchar = *iter;
         
         
         if (upsampled){
            // scale back the coordinates in the OCR result
            
            ocrchar.x = ocrchar.x/2;
            ocrchar.y = ocrchar.y/2;
            ocrchar.width = ocrchar.width/2;
            ocrchar.height = ocrchar.height/2;
         }
         
         
         ocrchar.x += lineblob.x;
         ocrchar.y += lineblob.y;
         
         
         char ch = ocrchar.ch;
         if (ch < 0 || ch > '~') 
            ch = '?';
         ocrchar.ch = ch;
         
         
         if (iter > ocr_chars.begin()){
            OCRChar& previous_ocrchar = *(iter-1);
            
            int spacing = ocrchar.x - (previous_ocrchar.x + previous_ocrchar.width);
            //cout << '[' << ocrchar.height << ':' << spacing << ']';
            //cout << '[' << spacing << ']';
            
            
            if (lineblob.height > 6 && spacing >= 4){// || spacing >= 2){
               ocrline.addWord(ocrword);
               ocrword.clear();               
               cout << ' ';
            }
         } 
         
         cout << ocrchar.ch;
         
         ocrword.add(ocrchar);
         ocrword.y = lineblob.y;
         ocrword.height = lineblob.height;
         
         
         
         
         
      }
      
      
      ocrline.addWord(ocrword);
      
      cout << endl;
      
      
      ocrlines.push_back(ocrline);
      
   }
}

void getLeafBlobs(vector<Blob>& blobs, vector<Blob>& leaf_blobs){
   
   leaf_blobs.clear();
   
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& a = *it;
      
      // check if blob 'a' contains any other blob
      vector<Blob>::iterator it1;
      for (it1 = blobs.begin(); 
           it1 != blobs.end(); ++it1){
         
         Blob& b = *it1;
         
         if (it != it1 && b.isContainedBy(a))
            break;
      }
      
      // if not, it is a leave blob
      if (it1 == blobs.end())
         leaf_blobs.push_back(a);
         
   }
}

Mat
cvgui::findPokerBoxes(const Mat& screen, vector<Blob>& output_blobs){
   Mat dark;
   Util::rgb2grayC3(screen,dark);
   dark = dark * 0.5;
   
   
   VLOG("Input", screen);
   
   Mat gray;
   cvtColor(screen,gray,CV_RGB2GRAY);
   
   //medianBlur(gray, gray, 3);
   //VLOG("Blurred", gray); 
   
   Mat edges;
   
   // Code for experimenting differenty parameters for Canny
   //   for (int c=10;c<=100;c=c+10){
   //      char buf[50];
   //      Mat test;
   //      sprintf(buf,"Canny%d",c);
   //      Canny(gray,test,0.66*c,1.33*c,3,true);  
   //      VLOG(buf, test);       
   //   }
   
   int s = 100;
   Canny(gray,edges,0.66*s,1.33*s,3,true);  
   VLOG("Canny", edges); 
   
   
   Mat v = Mat::ones(2,2,CV_8UC1);

   dilate(edges, edges, v);
   erode(edges, edges, v);

   VLOG("Dilated", edges); 

   Mat lines;
   findLongLines(edges, lines, 15);
   
   Mat w = Mat::ones(5,5,CV_8UC1);
   dilate(lines, lines, w);
   erode(lines, lines, w);
   
   VLOG("Lines", lines); 

   
   Mat lines2;
   Mat z = Mat::ones(2,2,CV_8UC1);   

   dilate(lines, lines, z);
   
   findLongLines(lines, lines2, 30, 15);

   //dilate(lines2, lines2, z);
   //erode(lines2, lines2, z);
   
   VLOG("Lines2", lines2); 


   
   Mat result = dark.clone();
   //Mat copy = edges.clone();
   //Mat copy = lines.clone();
   
   Mat copy = lines2.clone();
   
   
   vector<vector<Point> > contours;
   vector<Vec4i> hierarchy;
   
   findContours( copy, contours, hierarchy,
                CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );
  
   vector<Blob> blobs;
   
   Mat blank = dark.clone();
   blank = 0.0;
   
   Mat contour_shading = blank;//dark.clone();
   
   for (vector<vector<Point> >::iterator it = contours.begin();
        it != contours.end(); ++it){
      
      vector<Point>& contour = *it;
      
      Rect r = boundingRect(Mat(contour));
      
      double a1 = contourArea(Mat(contour));
      double a2 = r.height * r.width;
      
      
      if (a2 < 100)
         continue;
         
      
      if (abs(r.width - 65) > 5)
         continue;
      
      //if ( min(a1,a2)/max(a1,a2) > 0.80){
         
         vector<vector<Point> > cs;
         cs.push_back(contour);
         drawContours(contour_shading, cs, -1, Color::RANDOM(), CV_FILLED);
         
         
         Blob blob(r);
         blobs.push_back(blob);
      //}
      
   }
   
   Mat contours_image = contour_shading*0.7 + dark*0.5;

   Painter::drawBlobs(contours_image, blobs, Color::WHITE);

   VLOG("ColoredSelectedContours", contours_image); 
   
   
   Mat blobs_result = dark.clone();
   Painter::drawBlobs(blobs_result, blobs, Color::RED);
   
   VLOG("OutputBlobs", blobs_result);
   
   // print output to stdout
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& b = *it;
      
      cout << b.x << " " << b.y << " " << b.width << " " << b.height << endl;
   }
      
   return contours_image;
         
}

void
cvgui::findBoxes(const Mat& screen, vector<Blob>& output_blobs){

   Mat dark;
   Util::rgb2grayC3(screen,dark);
   dark = dark * 0.5;
   
   
   VLOG("Input", screen);
   
   Mat gray;
   cvtColor(screen,gray,CV_RGB2GRAY);
   
   //medianBlur(gray, gray, 3);
   //VLOG("Blurred", gray); 
   
   Mat edges;

// Code for experimenting differenty parameters for Canny
//   for (int c=10;c<=100;c=c+10){
//      char buf[50];
//      Mat test;
//      sprintf(buf,"Canny%d",c);
//      Canny(gray,test,0.66*c,1.33*c,3,true);  
//      VLOG(buf, test);       
//   }

   int s = 100;
   Canny(gray,edges,0.66*s,1.33*s,3,true);  
   VLOG("Canny", edges); 


   dilate(edges, edges, Mat::ones(2,2,CV_8UC1));
   VLOG("Dilated", edges); 

   
   Mat result = dark.clone();
   Mat copy = edges.clone();
   
   
   vector<vector<Point> > contours;
   vector<Vec4i> hierarchy;
   
   findContours( copy, contours, hierarchy,
                CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );

//   findContours(edges, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

   //findContours( copy, contours, hierarchy,
    //            CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );
   
//   cout << contours.size() << " " << hierarchy.size() << endl;
   // iterate through all the top-level contours,
   // draw each connected component with its own random color
//   int idx = 0;
//   for( ; idx >= 0; idx = hierarchy[idx][0]){
//      
//      vector<Point>& contour = contours[idx];
//      
//      
//      //Scalar color( rand()&255, rand()&255, rand()&255 );
//      Scalar cyan(255,255,0);
//      vector<vector<Point> > cs;
//      cs.push_back(contour);
//      drawContours( result, cs, -1, cyan, 2);//, CV_FILLED);
//      
// //     drawContours( result, contours, idx, color, CV_FILLED, 8, hierarchy );
//
//   }
   
   vector<Blob> blobs;
   
   
   Mat box_contour_image = dark.clone();
   
   for (vector<vector<Point> >::iterator it = contours.begin();
        it != contours.end(); ++it){
      
      vector<Point>& contour = *it;
    
      Rect r = boundingRect(Mat(contour));
      
      double a1 = contourArea(Mat(contour));
      double a2 = r.height * r.width;
      
      
      
      
      if ( min(a1,a2)/max(a1,a2) > 0.80){

         vector<vector<Point> > cs;
         cs.push_back(contour);
         drawContours(box_contour_image, cs, -1, Color::RANDOM(), 2);//, CV_FILLED);
         
         
         Blob blob(r);
         blobs.push_back(blob);
      }
      
   }
   
        
   VLOG("Box-shaped contours", box_contour_image); 
   
   
   Mat blobs_result = dark.clone();
   Painter::drawBlobs(blobs_result, blobs, Color::RED);
   
   VLOG("Blobs", blobs_result);
   
   
   vector<Blob> unique_blobs;
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& a = *it;

      
      vector<Blob>::iterator it1;
      for (it1 = unique_blobs.begin(); 
           it1 != unique_blobs.end(); ++it1){
         
         Blob& b = *it1;
         
         int d = 5;
         bool similar_bounding_box = 
            abs(a.x-b.x) < d && abs(a.y-b.y) < d &&
         abs(a.height-b.height) < 2*d &&
         abs(a.width-b.width) < 2*d;
         
         
         if (similar_bounding_box)
            break;
            
      }
      
      // if no blob whose bounding box is similar
      if (it1 == unique_blobs.end()){
       
         // add this blob to the list of unique blobs
         unique_blobs.push_back(a);
      }
   }
   
   Mat unique_blobs_result = dark.clone();
   Painter::drawBlobs(unique_blobs_result, unique_blobs, Color::RED);
   
   VLOG("UniqueBlobs", unique_blobs_result);
   
   
   vector<Blob> leaf_blobs;
   getLeafBlobs(unique_blobs, leaf_blobs);
   
      
   Mat leaf_blobs_result = dark.clone();
   Painter::drawBlobs(leaf_blobs_result, leaf_blobs, Color::RED);
   VLOG("LeafBlobs", leaf_blobs_result);
   
   output_blobs = leaf_blobs;
}




void
cvgui::computeUnitBlobs(const Mat& screen, Mat& output){
   
   VLOG("Input", screen);
   
   Mat gray;
   cvtColor(screen,gray,CV_RGB2GRAY);
   
   
   Mat edges;
   Canny(gray,edges,0.66*50,1.33*50,3,true);  
   VLOG("Canny", edges); 
   
//   Mat corners;
//   cornerHarris(gray,corners,10,5,1.0);

//   Mat corners_result = screen.clone();
//   vector<Point2f> corners;
//   goodFeaturesToTrack(gray, corners, 50, 0.5, 10);
//   for (vector<Point2f>::iterator it = corners.begin(); it != corners.end(); ++it){
//      Point2f& p = *it;
//      circle(corners_result, p, 3, Scalar(0,0,255));
//   }
//   VLOG("Corners", corners_result); 
   
   
   adaptiveThreshold(gray, gray, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 5, 1);
   VLOG("AdaptiveThresholded", gray);
   
   Mat lines;
   cvgui::findLongLines(gray, lines);
   
   Mat lines_viz = lines.clone();
   dilate(lines_viz, lines_viz, Mat::ones(4,4,CV_8UC1));
   VLOG("LongLinesFound", lines_viz);
   
   Mat not_lines;
   bitwise_not(lines, not_lines);
   
   Mat foreground;
   bitwise_and(gray, not_lines, foreground);
   
   gray.setTo(0, lines);
   VLOG("LongLinesRemoved",gray);
   
   
   dilate(edges, edges, Mat::ones(3,3,CV_8UC1));
   bitwise_and(gray, edges, gray);
   
   VLOG("NonEdgeRemoved", gray);
   
   output = gray;
}



void 
cvgui::getParagraphBlobs(const Mat& screen, vector<ParagraphBlob>& output_parablobs){
      
   Mat screen_gray;
   cvtColor(screen,screen_gray,CV_RGB2GRAY);
   
   Mat blobs_mask;
   cvgui::computeUnitBlobs(screen, blobs_mask);
   
   Mat dark;
   Util::rgb2grayC3(screen,dark);
   dark = dark * 0.5;
   
   vector<Blob> blobs;
   cvgui::extractBlobs(blobs_mask, blobs);
   
   cvgui::calculateColor(blobs, screen, blobs_mask);
   
   // draw blobs
   Mat result_blobs = dark.clone();
   Painter::drawBlobs(result_blobs, blobs, Scalar(255,255,0));   
   VLOG("blobs-extracted",result_blobs);
   
   
   vector<Blob> filtered_blobs;
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& blob = *it;
      
      //  if (blob.height < 3 && blob.width < 3)
      //         continue;
      //      
      //      if (blob.height < 4)
      //         continue;
      
      filtered_blobs.push_back(blob);
   }
   
   Mat result_filtered_blobs = dark.clone();
   Painter::drawBlobs(result_filtered_blobs, filtered_blobs, Scalar(255,255,0));
   
   VLOG("blobs-filtered",result_filtered_blobs);
   
   
   Mat result_lineblobs = dark.clone();
   
   vector<LineBlob> lineblobs;
   cvgui::linkBlobsIntoLineBlobs(filtered_blobs, lineblobs, 20);
   
   Painter::drawLineBlobs(result_lineblobs, lineblobs, Scalar(255,255,0));
   VLOG("lineblobs", result_lineblobs);
   
   
   Mat result_lineblobs_filtered = dark.clone();
   vector<LineBlob> filtered_lineblobs;
   
   // Calculate the bounding rectangle of each line of linked blobs
   for (vector<LineBlob>::iterator it_lines = lineblobs.begin();
        it_lines != lineblobs.end(); ++ it_lines){
      
      LineBlob& line = *it_lines;
      
      // Ignore lines with fewer than X elements
      if (line.blobs.size()<2)
         continue;

      
      if (line.height < 3)
         continue;
      
      filtered_lineblobs.push_back(line);
   }
   
   
   
   Painter::drawLineBlobs(result_lineblobs_filtered, filtered_lineblobs, Scalar(255,255,0));   
   VLOG("lineblobs-filtered", result_lineblobs_filtered);
   
   
   Mat result_merged_blobs = dark.clone();
   
   vector<LineBlob> merged_lineblobs;
   cvgui::mergeLineBlobs(filtered_lineblobs, merged_lineblobs);
   Painter::drawLineBlobs(result_merged_blobs, merged_lineblobs, Scalar(255,255,0));
   VLOG("lineblobs-merged", result_merged_blobs);
   
   
   vector<ParagraphBlob> parablobs;
   cvgui::linkLineBlobsIntoPagagraphBlobs(merged_lineblobs, parablobs);
   
   Mat result_linked_parablobs = dark.clone();
   
   Painter::drawParagraphBlobs(result_linked_parablobs, parablobs, Scalar(255,0,0));
   
   VLOG("paragblobs", result_linked_parablobs);
   
   
   output_parablobs = parablobs;
   
   
 //  Mat screen_dark = screen * 0.2;
//   Mat ocr_result_image = screen_dark;
//   
//   
//   for (vector<ParagraphBlob>::iterator it = parablobs.begin(); 
//        it != parablobs.end(); ++it){
//      
//      
//      ParagraphBlob& parablob = *it;      
//      
//      vector<OCRLine> ocrlines;
//      
//      cvgui::run_ocr_on_lineblobs(parablob.lineblobs, screen_gray, ocrlines);
//      
//      Painter::drawOCRLines(ocr_result_image, ocrlines);
//      
//   }
//   
//   VLOG("OCR-Result", ocr_result_image);
   
   
}


void
cvgui::getLineBlobsAsIndividualWords(const Mat& screen, vector<LineBlob>& output_lineblobs){

   Mat screen_gray;
   cvtColor(screen,screen_gray,CV_RGB2GRAY);
   
   Mat blobs_mask;
   cvgui::computeUnitBlobs(screen, blobs_mask);
   
   Mat dark;
   Util::rgb2grayC3(screen,dark);
   dark = dark * 0.5;
   
   vector<Blob> blobs;
   cvgui::extractBlobs(blobs_mask, blobs);
   
   cvgui::calculateColor(blobs, screen, blobs_mask);
   
   // draw blobs
   Mat result_blobs = dark.clone();
   Painter::drawBlobs(result_blobs, blobs, Scalar(255,255,0));   
   VLOG("blobs-extracted",result_blobs);
   
   
   vector<Blob> filtered_blobs;
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& blob = *it;
      
      //  if (blob.height < 3 && blob.width < 3)
      //         continue;
      //      
      //      if (blob.height < 4)
      //         continue;
      
      filtered_blobs.push_back(blob);
   }
   
   Mat result_filtered_blobs = dark.clone();
   Painter::drawBlobs(result_filtered_blobs, filtered_blobs, Scalar(255,255,0));
   
   VLOG("blobs-filtered",result_filtered_blobs);
   
   
   Mat result_lineblobs = dark.clone();
   
   vector<LineBlob> lineblobs;
   cvgui::linkBlobsIntoLineBlobs(filtered_blobs, lineblobs, 3);
   
   Painter::drawLineBlobs(result_lineblobs, lineblobs, Scalar(255,255,0));
   VLOG("lineblobs", result_lineblobs);
   
   
   Mat result_lineblobs_filtered = dark.clone();
   vector<LineBlob> filtered_lineblobs;
   
   // Calculate the bounding rectangle of each line of linked blobs
   for (vector<LineBlob>::iterator it_lines = lineblobs.begin();
        it_lines != lineblobs.end(); ++ it_lines){
      
      LineBlob& line = *it_lines;
      
      // Ignore lines with fewer than X elements
//      if (line.blobs.size()<2)
//         continue;
      
      if (line.height < 3)
         continue;
      
      filtered_lineblobs.push_back(line);
   }
   
   
   
   Painter::drawLineBlobs(result_lineblobs_filtered, filtered_lineblobs, Scalar(255,255,0));   
   VLOG("lineblobs-filtered", result_lineblobs_filtered);
   
   
   
   output_lineblobs = filtered_lineblobs;
   
  // Mat result_merged_blobs = dark.clone();
//   
//   vector<LineBlob> merged_lineblobs;
//   cvgui::mergeLineBlobs(filtered_lineblobs, merged_lineblobs);
//   Painter::drawLineBlobs(result_merged_blobs, merged_lineblobs, Scalar(255,255,0));
//   VLOG("lineblobs-merged", result_merged_blobs);
//   
//   
//   vector<ParagraphBlob> parablobs;
//   cvgui::linkLineBlobsIntoPagagraphBlobs(merged_lineblobs, parablobs);
//   
//   Mat result_linked_parablobs = dark.clone();
//   
//   Painter::drawParagraphBlobs(result_linked_parablobs, parablobs, Scalar(255,0,0));
//   
//   VLOG("paragraph-linked", result_linked_parablobs);
//   
//   
//   Mat screen_dark = screen * 0.2;
//   Mat ocr_result_image = screen_dark;
//   
//   
//   for (vector<ParagraphBlob>::iterator it = parablobs.begin(); 
//        it != parablobs.end(); ++it){
//      
//      
//      ParagraphBlob& parablob = *it;      
//      
//      vector<OCRLine> ocrlines;
//      
//      cvgui::run_ocr_on_lineblobs(parablob.lineblobs, screen_gray, ocrlines);
//      
//      Painter::drawOCRLines(ocr_result_image, ocrlines);
//      
//   }
//   
//   VLOG("OCR-Result", ocr_result_image);
}

void 
cvgui::segmentScreenshot(const Mat& screen, vector<Blob>& text_blobs, vector<Blob>& image_blobs){
   
   VLOG("Input", screen);
   
   Mat gray;
   cvtColor(screen,gray,CV_RGB2GRAY);
   Mat screen_gray = gray.clone();
   
   adaptiveThreshold(gray, gray, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 5, 1);
   VLOG("AdaptiveThresholded", gray);
   
   
   Mat lines;
   cvgui::findLongLines(gray, lines);
   
   Mat lines_viz = lines.clone();
   dilate(lines_viz, lines_viz, Mat::ones(4,4,CV_8UC1));
   VLOG("LongLinesFound", lines_viz);
   
   Mat not_lines;
   bitwise_not(lines, not_lines);
   
   Mat foreground;
   bitwise_and(gray, not_lines, foreground);
   
   gray.setTo(0, lines);
   VLOG("LongLinesRemoved",gray);
   
   
   denoise(gray);   
   VLOG("NoiseRemoved", gray);  
   
   dilate(gray,gray,Mat::ones(1,3,CV_8UC1));
   VLOG("Dilated",gray);
   
   vector<Rect> rects;
   //cvgui::extractRects(gray, rects);
   
   vector<Blob> blobs;
   cvgui::extractBlobs(gray, blobs);
   
   
   Mat dilated = gray.clone();
   
   
   // visualization
   Mat result;
   Mat g;
   cvtColor(screen, g, CV_RGB2GRAY);
   
   Mat ss[3] = {g,g,g};
   merge(ss,3,result);   
   result = result * 0.5;
   
   Mat bg = result.clone();
   Mat screen_darken = bg.clone();
   
   
   text_blobs.clear();
   
   for (vector<Blob>::iterator b = blobs.begin(); b != blobs.end(); ++b){
      
      Blob& blob = *b;
      Rect& bound = blob;
      Mat part(screen, bound);
      //paste(part, result, r->x, r->y);
      
      Mat p = part.clone();
      Mat g;
      cvtColor(p, g, CV_RGB2GRAY);
      adaptiveThreshold(g, g, 255, 
                        ADAPTIVE_THRESH_MEAN_C, 
                        THRESH_BINARY_INV, 3, 1);
      vector<Rect> rs;
      cvgui::extractSmallRects(g, rs);
      for (vector<Rect>::iterator r = rs.begin();r != rs.end(); ++r){
         r->x += bound.x;
         r->y += bound.y;
      }
      
      vector<Rect> rs0;
      for (vector<Rect>::iterator q = rs.begin();
           q != rs.end(); ++q){
         
         // exclude overly short element (e.g., dot of i)
         if (q->height < 5)
            continue;         
         
         rs0.push_back(*q);
      }
      
      rs = rs0;
      
      // re-calculate the bounds
      int x1,y1,x2,y2;
      x1 = bound.x + bound.width;
      y1 = bound.y + bound.height;
      x2 = bound.x;
      y2 = bound.y;
      
      for (vector<Rect>::iterator r = rs.begin();
           r != rs.end(); ++r){
         x1 = min(x1, r->x);
         y1 = min(y1, r->y);
         x2 = max(x2, r->x + r->width);
         y2 = max(y2, r->y + r->height);
      }
      
      //blob.bound = Rect(x1-2,y1-2,x2-x1+4,y2-y1+4);     
      //blob.bound = Rect(x1,y1,x2-x1,y2-y1); 
      
      // make the bound bigger to be consistent with the dilated blobs
      blob = Blob(Rect(x1-2,y1-1,x2-x1+4,y2-y1+2)); 
      
      
      int MINIMUM_NUM_CHARBLOBS = 2;
      
      if (cvgui::areHorizontallyAligned(rs) && rs.size()>=MINIMUM_NUM_CHARBLOBS)
         Painter::drawRects(result, rs, Scalar(0,255,0));
      else
         Painter::drawRects(result, rs, Scalar(0,0,255));
      
      if (cvgui::areHorizontallyAligned(rs) && rs.size()>=MINIMUM_NUM_CHARBLOBS){
         text_blobs.push_back(blob);
      }
      
   }
   
   //drawRects(result, rects, Scalar(255,0,0));
   Painter::drawBlobs(result, blobs, Scalar(255,0,0));
   
   VLOG("TextBlocksExtracted",result);
   
   
   
   Mat text_mask = Mat::zeros(screen.size(), CV_8UC1);
   
   
   for (vector<Blob>::iterator b = text_blobs.begin();
        b != text_blobs.end(); ++b){
      
      Rect& r = *b;
      Mat m(text_mask, 
            Range(r.y,r.y+r.height),
            Range(r.x,r.x+r.width));
      
      m = 255;
      
   }
   
   VLOG("TextBinaryMaskComputed", text_mask);
   
   
   
   
   gray.setTo(0, text_mask);
   VLOG("AfterTextRemoved",gray);
   
   dilate(gray,gray,Mat());
   VLOG("DilatedAgain",gray);
   
   Mat dilated_again = gray.clone();
   
   vector<Blob> temp;
   //cvgui::extractRects(gray, temp);
   cvgui::extractBlobs(gray, temp);
   
   // only keep image rects larger than n pixels
   
   const int MIN_IMAGE_RECT_AREA = 150;
   
   image_blobs.clear();
   for (vector<Blob>::iterator b = temp.begin();
        b != temp.end(); ++b){
      
      if (b->width * b->height < MIN_IMAGE_RECT_AREA)
         continue;
      
      //      // blob is too big, something is wrong
      //      if (b->bound.width > 0.8 * screen.width)
      //         continue;
      //      if (b->bound.height > 0.8 * screen.height)
      //         continue;
      
      image_blobs.push_back(*b);
      
   }
   
   Mat image_result = bg.clone();
   
   Painter::drawBlobs(image_result, image_blobs, Scalar(0,0,255));
   VLOG("ImageRecordsExtracted", image_result);

   return;
   
   Mat ui_mask = Mat::zeros(screen.size(), CV_8UC1);
   Mat photo_mask = Mat::zeros(screen.size(), CV_8UC1);
   for (vector<Blob>::iterator b = image_blobs.begin();
        b != image_blobs.end(); ++b){
      
      Rect& r = *b;
      Mat mask;
      if (r.height < 100)
         mask = ui_mask;
      else 
         mask = photo_mask;
      
      Mat m(mask, 
            Range(r.y,r.y+r.height),
            Range(r.x,r.x+r.width));
      
      m = 255;
   }
   
   VLOG("UIMask", ui_mask);
   VLOG("PhotoMask", photo_mask);
   
   Mat segmap;
   Mat segmapr;
   Mat segmapg;
   Mat segmapb;
   
   bitwise_and(dilated,text_mask,segmapr);
   bitwise_and(dilated,ui_mask,segmapg);
   bitwise_and(dilated_again,photo_mask,segmapb);
   //segmapb = photo_mask;
   
   
   Mat cs[3] = {segmapb, segmapg,segmapr};
   merge(cs,3,segmap);   
   VLOG("SegMap", segmap);
   
   
   //Mat wordmap = screen_darken.clone();//segmap.clone();
   Mat wordmap = Mat::zeros(screen.size(), CV_8UC3);
   
   
   screen.copyTo(wordmap, segmapg);
   screen.copyTo(wordmap, photo_mask);
   screen.copyTo(wordmap, text_mask);
   
   
   
   Mat random_ids = Mat::ones(1,100,CV_8UC1);
   randu(random_ids, 22, 80);
   
   Scalar white(255,255,255);
   Scalar black(0,0,0);
   Scalar yellow(255,255,0);
   
   
   int i = 0;
   
   for (vector<Blob>::iterator b = image_blobs.begin();
        b != image_blobs.end(); ++b){
      
      Rect& r = *b;
      
      
      char vwstr[100];
      
      
      if (r.height < 100){
         
         int xc = r.x + r.width/2;
         int yc = r.y + r.height/2;
         
         
         //         putTextWithBackgroundCentered(black, wordmap, "u12", Point(xc,yc), 
         //                               FONT_HERSHEY_SIMPLEX, 0.5, white);
         
         int id = random_ids.at<uchar>(0,i++);
         sprintf(vwstr,"u%d",id);   
         
//         putTextWithBackgroundCentered(white, wordmap, vwstr, Point(xc,yc), 
//                                       FONT_HERSHEY_DUPLEX, 0.5, black);
         
         
      }else{ 
         
         
         SURF surf_extractor(6.0e3);
         vector<KeyPoint> keypoints;
         
         // printf("Extracting keypoints\n");
         surf_extractor(screen_gray, photo_mask, keypoints);
         //printf("Extracted %d keypoints from the image\n", (int)keypoints.size());
         
         for (vector<KeyPoint>::iterator it = keypoints.begin(); it != keypoints.end(); ++it){
            KeyPoint& p = *it;
            
            circle(wordmap, p.pt, p.size*1.5, yellow);
            
            //            putTextWithBackgroundCentered(black, wordmap, "v12", p.pt,  
            //                                  FONT_HERSHEY_SIMPLEX, 0.5, white);
            
            int id = random_ids.at<uchar>(0,i++);
            sprintf(vwstr,"s%d",id);
            
//            putTextWithBackgroundCentered(white, wordmap, vwstr, p.pt,  
//                                          FONT_HERSHEY_DUPLEX, 0.5, black);
            
         }
      }
      
      
   }
   
   VLOG("WordMap", wordmap);
}




static int L1dist(Vec3b p1, Vec3b p2){
	return max(p1[0],p2[0])-min(p1[0],p2[0])+	
   max(p1[1],p2[1])-min(p1[1],p2[1])+
   max(p1[2],p2[2])-min(p1[2],p2[2]);	
}



bool 
cvgui::areHorizontallyAligned(const vector<Rect>& rects){
   
   if (rects.size() <= 1)
      return true;
   
   vector<Rect> sorted_rects = rects;
   sort(sorted_rects, sort_by_x);
   
   int ymin = 10000;
   int ymax = 0;
   
   bool no_overlap = true;
   for (vector<Rect>::const_iterator r = sorted_rects.begin() + 1;
        r != sorted_rects.end(); ++r){
      
      no_overlap = no_overlap && (r->x >= (r-1)->x + (r-1)->width - 2);
      
      int baseline = r->y + r->height;
      ymin = min(baseline, ymin);
      ymax = max(baseline, ymax);
   }
   
   bool baseline_aligned = (ymax - ymin < 10);
   
   int minv = 10000;
   int maxv = 0;   
   for (vector<Rect>::const_iterator r = sorted_rects.begin();
        r != sorted_rects.end(); ++r){
      
      int v = r->height;
      minv = min(v, minv);
      maxv = max(v, maxv);
   }
   
   bool height_similar = (maxv - minv < 10);
   
   // if the difference between the highest and lowest baseline
   // is too large, it means the rects are not alginged horizontally
   
   return baseline_aligned && height_similar && no_overlap;
}


bool
cvgui::hasMoreThanNUniqueColors(const Mat& src, int n){
   
   
   Mat_<Vec3b>::const_iterator it = src.begin<Vec3b>(),
   itEnd = src.end<Vec3b>();
   
   
   vector< pair<Vec3b,int> > colors; 
   //vector<int> counts;
   colors.push_back( pair<Vec3b,int>(*it,1) );
   ++it;
   
   for(; it != itEnd; ++it){
      
      bool matched;
      matched = false;
      for (vector< pair<Vec3b,int> >::iterator c = colors.begin();
           c != colors.end();
           ++c){
         
         Vec3b& color = (*c).first;
         int& count = (*c).second;
         
         int d = L1dist((*it), (color));
         //cout << d << endl;
         if (d < 150){
            matched = true;
            count++;
            break;
         }
      }
      
      if (!matched){
         
         //    if (colors.size() == 4)
         //     return true;
         // else
         colors.push_back(pair<Vec3b,int>(*it,1));
         
      }
      
   }
   
   cout << endl << endl;
   for (vector< pair<Vec3b,int> >::iterator c = colors.begin();
        c != colors.end();
        ++c){
      
      Vec3b& color = (*c).first;
      int& count = (*c).second;
      //cout << count << endl;
   }
   
   return (colors.size() > 5);
}


void 
cvgui::extractSmallRects(const Mat& src, 
                         vector<Rect>& rects){
   
   Mat copy = src.clone();
   
   vector<vector<Point> > contours;
   findContours(copy, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
   
   for (vector<vector<Point> >::iterator contour = contours.begin();
        contour != contours.end(); ++contour){
      
      Rect bound = boundingRect(Mat(*contour));
      rects.push_back(bound);
   }
   
}

void
cvgui::calculateColor(vector<Blob>& blobs, 
                      const Mat& color_image,
                      const Mat& foreground_mask){
   
   
   for (vector<Blob>::iterator it = blobs.begin();
        it != blobs.end(); ++it){
      
      Blob& blob = *it;
      
      
      Mat part(color_image, blob);
      Mat mask(foreground_mask, blob);
      Scalar mean, stddev;
      meanStdDev(part, mean, stddev, mask);
      
      blob.mr = mean[0];
      blob.mg = mean[1];
      blob.mb = mean[2];
      
   }
   
}


void 
cvgui::extractBlobs(const Mat& src, vector<Blob>& blobs){
   
   Mat copy = src.clone();
   blobs.clear();
   
   vector<vector<Point> > contours;
   findContours(copy, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
   
   
   for (vector<vector<Point> >::iterator contour = contours.begin();
        contour != contours.end(); ++contour){
      
      
      double area = contourArea(Mat(*contour));
      
      Rect bound = boundingRect(Mat(*contour));
      
      
      
      
      Blob blob(bound);
      blob.area = area;
      blobs.push_back(blob);
      
   }
   
   
   
   
   
}


void 
cvgui::extractRects(const Mat& src, 
                    vector<Rect>& rects){
   
   Mat copy = src.clone();
   rects.clear();
   
   
   vector<vector<Point> > contours;
   findContours(copy, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
   
   
   for (vector<vector<Point> >::iterator contour = contours.begin();
        contour != contours.end(); ++contour){
      
      
      double area = contourArea(Mat(*contour));
      
      Rect bound = boundingRect(Mat(*contour));
      
      
      // too small
      if (bound.width < 6 || bound.height < 6)
         continue;
      
      // too empty
      if (bound.height > 100 && bound.width * bound.height * 0.50 > area)
         continue;
      
      rects.push_back(bound);
      
   }
   
}


void
cvgui::findLongLines(const Mat& src, Mat& dest, int min_length, int extension){
   
   dest = src.clone();
   
   Mat mask;
   cvgui::findLongLines_Horizontal(dest,mask, min_length, extension);
   
   Mat maskT, destT;
   transpose(dest,destT);
   cvgui::findLongLines_Horizontal(destT,maskT,min_length, extension);
   
   Mat maskTT;
   transpose(maskT,maskTT);
   
   bitwise_or(mask,maskTT,dest);
   
}


void
cvgui::findLongLines_Horizontal(const Mat& binary, Mat& dest,
                                int min_length, int extension){
   
   typedef uchar T;
   
  	dest = Mat::zeros(binary.rows,binary.cols,CV_8UC1);
   
	Size size = binary.size();
	for( int i = 0; i < size.height; i +=1 ){
      
		const T* ptr1 = binary.ptr<T>(i);
		T* ptr2 = dest.ptr<T>(i);	
		
		bool has_previous_baseline = false;
		int previous_baseline_endpoint = 0;
		int current_baseline_startpoint = 0;
      
      
      
      
      for( int j = 1; j < size.width; j += 1 ){			
			
         
         if (ptr1[j] > 0 && ptr1[j-1] == 0){
            current_baseline_startpoint = j;
         }
         
         if (ptr1[j-1] > 0 &&  (ptr1[j] == 0 || j == size.width - 1)){
            
            
				// check for the condition of a baseline hypothesis
				// the length of the baseline must be > 15
				if ((j - current_baseline_startpoint) > min_length){// || j == size.width - 1){
					
               //					int closeness_threshold = 1; 					
               //					if (has_previous_baseline && 
               //                   (current_baseline_startpoint - previous_baseline_endpoint) 
               //						 <= closeness_threshold){
               //						
               //						
               //						// merge the current baseline with the previously baseline
               //						for (int k=previous_baseline_endpoint; 
               //                       k < current_baseline_startpoint; k += 1){
               //	                     ptr2[k] = ptr1[j];
               //						}
               //					}
					
               //	has_previous_baseline = true;
					//previous_baseline_endpoint = j;
					
               
					for (int k=current_baseline_startpoint; k < j; ++k){
                  ptr2[k] = 255;
					}	

               for (int k=j; k < min(j+extension,size.width-1); ++k){
                  ptr2[k] = 255;
					}	
               
					
				}
				
				// forming a new baseline hypothesis
				//current_baseline_startpoint = j+1;
			}
      }
   }	 
} 


Mat
cvgui::obtainGrayBackgroundMask(const Mat& input){
   
   Mat copy = input.clone();
   
   Mat_<Vec3b>::iterator it = copy.begin<Vec3b>(),
   itEnd = copy.end<Vec3b>();
   for(; it != itEnd; ++it){
      
      uchar& b = (*it)[0];
      uchar& g = (*it)[1];
      uchar& r = (*it)[2];
      
      if (r == 0 && b == 0 && g == 0){
         r = 1; b = 1; g = 1;
      }
      
      if (b > 50 &&
          abs(b-g) < 10 && abs(g-b) < 10 && abs(b-r) < 10){
         r = 0;
         g = 0;
         b = 0;
      } 
      
      
      
   }
   
   
   Mat ms[3];
   split(copy, ms);
   
   Mat mask;
   threshold(ms[0],mask,0,255,THRESH_BINARY);
   
   dilate(mask,mask,Mat());	
   
   return mask;
}


Mat
cvgui::removeGrayBackground(const Mat& input){
   
//   Mat copy = input.clone();
//   Mat mask;
//   //  (Size(copy.rows,copy.cols), CV_8UC1);
//   
//   Mat_<Vec3b>::iterator it = copy.begin<Vec3b>(),
//   itEnd = copy.end<Vec3b>();
//   for(; it != itEnd; ++it){
//      
//      uchar& b = (*it)[0];
//      uchar& g = (*it)[1];
//      uchar& r = (*it)[2];
//      
//      if (r == 0 && b == 0 && g == 0){
//         r = 1; b = 1; g = 1;
//      }
//      
//      if (b > 50 &&
//          abs(b-g) < 10 && abs(g-b) < 10 && abs(b-r) < 10){
//         r = 0;
//         g = 0;
//         b = 0;
//      } 
//      
//      
//      
//   }
//   
//   
//   Mat ms[3];
//   split(copy, ms);
//   
//   //threshold(ms[0],mask,0,255,THRESH_BINARY_INV);
//   threshold(ms[0],mask,0,255,THRESH_BINARY);
//   
//   dilate(mask,mask,Mat());	
//   
//   
//   vector<vector<Point> > contours;
//   findContours(ms[0], contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
//   
//   Scalar red(0,0,255);
//   //drawContours(copy, contours, -1, red);
//   
//   
//   vector<Rect> boundingRects;
//   
//   for (vector<vector<Point> >::iterator contour = contours.begin();
//        contour != contours.end(); ++contour){
//      
//      
//      double area = contourArea(Mat(*contour));
//      
//      Rect bound = boundingRect(Mat(*contour));
//      
//      
//      if (bound.width * bound.height * 0.5 > area)
//         continue;
//      
//      boundingRects.push_back(bound);
//      
//      rectangle(copy, 
//                Point(bound.x, bound.y),
//                Point(bound.x + bound.width, bound.y + bound.height),
//                red);
//      
//      cout << bound.x << "," <<  bound.y << endl;
//      
//   }
//   
//   
//   
//   Mat result(Size(input.cols,input.rows), CV_8UC3);   
//   for (vector<Rect>::iterator bound = boundingRects.begin();
//        bound != boundingRects.end(); ++bound){
//      
//      if (bound->width < 10 || bound->height < 8)
//         continue;
//      
//      Mat r(input, *bound);
//      paste(r, result, bound->x, bound->y);
//      
//      
//   }
//   
//   
//   namedWindow("result",1);
//   imshow("result",result);
//   
//   
//   namedWindow("copy",1);
//   imshow("copy",copy);
//   
//   namedWindow("mask",1);
//   imshow("mask",mask);
//   waitKey(0);
   return Mat();
   
}

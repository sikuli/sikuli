#include <string>
#include <stdlib.h>
#include <cv.h>
#include <highgui.h>

#include "ocr-matcher.h"

using namespace std;

bool is_char_match(char a, char b){  
  return (tolower(a) == tolower(b));
}

int search(string& ocr, char target, int c){

     for (int k = max(c-1,0); k < min(c+1,(int)ocr.length()); k++){
      
      if (is_char_match(ocr[k],target)){
        return k;
      }
    }

  return -1;
}



OCRMatcher::~OCRMatcher(){  
  cvReleaseMat(&scores);
}

OCRMatcher::OCRMatcher(OCRResult& _result, const string& _target) : ocr_result(_result), target(_target) {

  ocr = ocr_result.str();
  

  scores = cvCreateMat(1,ocr.length(),CV_32FC1);
  cvSet(scores,cvScalar(0));

  for (int i=0;i<ocr.length();i++){
    
    for (int j=0;j<target.length();j++){


      if (is_char_match(ocr[i],target[j])){

        int k;
        k = search(ocr,target[0],i-j+1);
        if (k>=0)
          scores->data.fl[k] += 1;

        k = search(ocr,target[1],i-j+2);
        if (k>=0)
          scores->data.fl[k] += 1;

      }
     

    }
  }

  for (int i=0;i<ocr.length();i++){
    //cout << ocr[i] << scores->data.fl[i] << " ";
  }
}

Match 
OCRMatcher::next(){
    double maxval;
    CvPoint maxloc;
    cvMinMaxLoc(scores, 0, &maxval, 0, &maxloc);

    int  startpos = maxloc.x;

    int endpos = -1;
    int offset = target.length() - 1;
    int endpos0 = startpos + offset;
    
    while (endpos == -1 && endpos0 > startpos){
    
        char endchar = target[offset];
        
        endpos  = search(ocr, endchar, endpos0);
        
        endpos0--;
        offset--;
    }

    if (endpos == -1)
      return Match(0,0,0,0,-1);
    

    OCRChar start = ocr_result[startpos];
    OCRChar end   = ocr_result[endpos];
  

    //cout << endl;
    int max_height = 0;

    for (int i=startpos; i<= endpos; i++){
        //cout << ocr[i] << scores->data.fl[i] << " ";
        scores->data.fl[i] = 0;
    }
    //cout << endl;

    // find the height of the tallest character
    for (int i=startpos; i<= endpos; i++){
      OCRChar ocrchar = ocr_result[i];        
      max_height = max(max_height, ocrchar.y1 - ocrchar.y0);
    }
  
    int x = start.x0;
    int y = start.y0;
    int w = end.x1 - x;
    int h = max_height;
      
    return Match(x,y,w,h,maxval);
}



IplImage* create_ocr_result_image(IplImage* original_img, OCRResult& result){

  CvFont font;
  IplImage* img = cvCreateImage(cvSize(original_img->width, original_img->height), IPL_DEPTH_8U, 3);
  
  char txt[2];
  for (OCRResult::Iterator iter = result.begin(); iter != result.end(); iter++){
    //cout << iter->ch;

    txt[0] = iter->ch;
    txt[1] = '\0';

    cvInitFont(&font,CV_FONT_HERSHEY_PLAIN, 1, 1, 0, 1);                    
    
    CvSize s;
    int ymin;
    cvGetTextSize(txt, &font, &s, &ymin);    

    float hscale = 1.0 * (iter->x1 - iter->x0) / s.width;
    float vscale = 1.0 * (iter->y1 - iter->y0) / s.height;

    cvInitFont(&font,CV_FONT_HERSHEY_PLAIN, hscale, vscale, 0, 1);

    if (hscale < 2 && vscale < 2){  

      cvRectangle(img,
        cvPoint(iter->x0, iter->y0),
        cvPoint(iter->x1, iter->y1),
        cvScalar( 100, 0, 100), 1, 0, 0 );

      CvPoint loc = cvPoint(iter->x0, iter->y1);
      cvPutText(img, txt, loc, &font, cvScalar(255,255,255));

    }
  }

  return img;
}

void draw_matches(IplImage* img, vector<Match>& matches){

  for (vector<Match>::iterator iter = matches.begin(); iter != matches.end(); ++iter){
    Match& m = *iter;

    cvRectangle(img,
        cvPoint(m.x, m.y),
        cvPoint(m.x + m.w, m.y + m.h),
        cvScalar( 0, 255, 0), 1, 0, 0 );
  }
}


Matches match_by_ocr(const char* screen_image_filename, 
                     const char* target_string, 
                     int max_num_matches, 
                     double min_similarity_threshold,
                     int x, int y, int w, int h,
                     bool write_images, bool display_images)
{

  IplImage*  original_img = cvLoadImage( screen_image_filename, CV_LOAD_IMAGE_GRAYSCALE );

  IplImage*  ocr_img;


  bool search_region = x >= 0 && y >= 0 && w > 0 && h > 0 && x + w < original_img->width && y + w < original_img->height;
  if (search_region){

    // obtain a cropped image of the specified region
    cvSetImageROI(original_img, cvRect(x, y, w, h));
    IplImage* cropped_img = cvCreateImage( cvSize(w, h), IPL_DEPTH_8U, 1);
    cvCopy(original_img, cropped_img);
    cvResetImageROI(original_img);

    ocr_img = cropped_img;

  }else{

    ocr_img = original_img;

  }

  // scale the input image to %200 so text is large enoguh to recognize
  IplImage* scaled_img = cvCreateImage(cvSize(ocr_img->width*2, ocr_img->height*2), IPL_DEPTH_8U, 1 );
  cvResize(ocr_img, scaled_img);

  // run OCR on the scaled image
  OCRResult ocr_result = OCR::recognize((unsigned char*)scaled_img->imageData,
                                        scaled_img->width,
                                        scaled_img->height,
                                        8);  
  cvReleaseImage(&scaled_img);

    
  // scale back the coordinates in the OCR result
  for (OCRResult::Iterator iter = ocr_result.begin(); iter != ocr_result.end(); iter++){
    iter->x0 = iter->x0/2;
    iter->y0 = iter->y0/2;
    iter->x1 = iter->x1/2;
    iter->y1 = iter->y1/2;
  }

  if (search_region){
    for (OCRResult::Iterator iter = ocr_result.begin(); iter != ocr_result.end(); iter++){
      iter->x0 = x + iter->x0;
      iter->y0 = y + iter->y0;
      iter->x1 = x + iter->x1;
      iter->y1 = y + iter->y1;
    }

    cvReleaseImage(&ocr_img);
  }

  // find the target string in the OCR result 
  OCRMatcher ocr_matcher(ocr_result,string(target_string));

  Matches matches;
  int cnt = 0;  // count the number of matches retrieved
  while (true){
    Match m = ocr_matcher.next();
    cnt++;

    if (cnt > max_num_matches || m.score < min_similarity_threshold)
      break;
    else
      matches.push_back(m);   

  }


      
  IplImage* result_img = NULL; 
  if (display_images || write_images){
    result_img = create_ocr_result_image(original_img, ocr_result);  
    draw_matches(result_img, matches);

    if (search_region){
      // draw region box if applicable
      cvRectangle(result_img,
        cvPoint(x, y),
        cvPoint(x + w, y + h),
        cvScalar( 0, 0, 255), 1, 0, 0 );
    }

  }

  if (write_images){
    cvSaveImage("ocr_output.jpg", result_img);
  }

  if (display_images){
    cvNamedWindow("Input Image", CV_WINDOW_AUTOSIZE);
    cvShowImage("Input Image",original_img);

    cvNamedWindow("Result", CV_WINDOW_AUTOSIZE);
    cvShowImage("Result",result_img);

    cvWaitKey( 0 );
  }
  
  cvReleaseImage( &result_img );
  cvReleaseImage( &original_img );

  return matches;
}



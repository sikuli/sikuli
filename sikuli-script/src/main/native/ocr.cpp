#include <iostream>
#include <vector>
#include <iomanip>



#include "cv.h"
#include "highgui.h"

#include "cv-util.h"

#include "ocr.h"

#include "TimingBlock.h"

#ifdef DEBUG
#define dout std::cerr
#else
#define dout if(0) std::cerr
#endif

using namespace cv;
using namespace std;

enum{
	ALL,
	LOWER_ONLY,
	UPPER_ONLY
};

bool filter_lineRect(Rect& line, const char word[]);
bool filter_wordRect(WordRect& wordRect, const char word[]);

Rect strip_top_bottom(Mat input);

int L2dist(Vec3b p1, Vec3b p2){
	Vec3i p1i(p1);
	Vec3i p2i(p2);
	return norm(p1i - p2i);
}
	
int L2dist(uchar p1, uchar p2){
	return abs(p1 - p2);
}

int L1dist(Vec3b p1, Vec3b p2){
	return max(p1[0],p2[0])-min(p1[0],p2[0])+	
		   max(p1[1],p2[1])-min(p1[1],p2[1])+
		   max(p1[2],p2[2])-min(p1[2],p2[2]);	
}


Mat remove_horizontal_lines(Mat& im1, int min_length, int min_intensity){
   
	typedef uchar T;
	//typedef Vec3b T;
   
  	Mat im2 = Mat::ones(im1.rows,im1.cols,CV_8UC1)*255;
 
	Size size = im1.size();
	for( int i = 0; i < size.height; i +=1 )
   {

		T* ptr1 = im1.ptr<T>(i);
		uchar* ptr2 = im2.ptr<uchar>(i);	
		
		bool has_previous_baseline = false;
		int previous_baseline_endpoint = 0;
		int current_baseline_startpoint;
		
		current_baseline_startpoint = 0;
		
      for( int j = 1; j < size.width; j += 1 )
      {			
			
			// if a contrast transition is encountered or 
			// at the right-most edge of the image
         
			//float diff = norm(ptr1[j]-ptr1[j-1]);//1,p2);//L2dist(p1,p2);//(p1 - p2);//tr1[j] - ptr1[j-1]);
			//float diff = L1dist(ptr1[j],ptr1[j-1]);
			float diff = abs(ptr1[j]-ptr1[j-1]);
			if (diff > 50 || j == size.width - 1){
				
				// check for the condition of a baseline hypothesis
				// the length of the baseline must be > 15
				if ((j - current_baseline_startpoint) > min_length || j == size.width - 1){
					
					// if there's a previous baseline hypothesis 
					// and close to the current one, 
					//int closeness_threshold = 12;
					
					// set small for find task because lines usually
					// have plentiy of spacing vertically
					int closeness_threshold = 1; 
					
					if (has_previous_baseline && 
                   L2dist(ptr1[current_baseline_startpoint], 
                          ptr1[previous_baseline_endpoint]) < 150 &&
                   (current_baseline_startpoint - previous_baseline_endpoint) 
						 <= closeness_threshold){
						
						
						// merge the current baseline with the previously baseline
						for (int k=previous_baseline_endpoint; 
                       k < current_baseline_startpoint; k += 1){
							if (ptr2[k]>=min_intensity)
                        ptr2[k] = 0;
						}
					}
					
					has_previous_baseline = true;
					previous_baseline_endpoint = j;
					
					for (int k=current_baseline_startpoint; k < j; ++k){
						if (ptr2[k]>=min_intensity)
                     ptr2[k] = 0;
					}	
					
				}
				
				// forming a new baseline hypothesis
				current_baseline_startpoint = j+1;
			}
      }
   }	 
   
   return im2;
}

vector<Rect> segment_image(Mat color){
	TimingBlock tb("segment_image");
	Mat gray;
	cvtColor(color, gray, CV_RGB2GRAY);	
	
#if DISPLAY_SEGMENT_IMAGE
	Mat resultImage(color.size(), color.type());
#endif
	
   
// TODO: clean this up
	Mat im1 = gray;
	//Mat im2 = im1.clone();
	
   Mat im2 = remove_horizontal_lines(im1,6,0);
   Mat im1T;
   transpose(im2,im1T);
   Mat im4 = remove_horizontal_lines(im1T,30,0);
   //Mat im4 = im1T;
   Mat im4T;
   transpose(im4,im4T);
   bitwise_and(im2,im4T,im4T);
   
#if DISPLAY_SEGMENT_IMAGE
	Mat im3;
   threshold(im4T,im3,0,255,THRESH_BINARY);
	namedWindow("segment:binary", CV_WINDOW_AUTOSIZE);			
	imshow("segment:binary",im3);			
#endif
   
   im2 = im4T;		
	
	CvMemStorage* storage = cvCreateMemStorage();
	CvSeq* first_contour = NULL;
	
	CvMat mat = (CvMat) im2;	
	{
	TimingBlock  tb("FindContour");

	cvFindContours(
				   &mat,
				   storage,
				   &first_contour,
				   sizeof(CvContour),
				   CV_RETR_EXTERNAL);
	
	}
	CvSeq* c = first_contour;
 	
	vector<Rect> rects;	
	{
		TimingBlock tb("while loop");
	while (c!= NULL){
		
		// find bounding boxes
		int x1=im2.cols;
		int x2=0;
		int y1=im2.rows;
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
		
		// add a 2-pixel margin around each line block
		x1 = max(x1-2,0);
		y1 = max(y1-2,0);
		x2 = min(x2+2,im1.cols-1);
		y2 = min(y2+2,im1.rows-1);
		
		int h = y2-y1+1;
		int w = x2-x1+1;
		
		Rect rect(x1,y1,w,h);
		
		if (h<8 || w<8 || h/w > 5){
			
		}else{
			
#if DISPLAY_SEGMENT_IMAGE
			
			rectangle(im2,
					  Point( x1, y1), 
					  Point( x2, y2),
					  Scalar(200), 0.5, 0, 0 ); 
			
			//			rectangle(color, 
			//					  Point( x1, y1), 
			//					  Point( x2, y2),
			//					  Scalar(0,200,0), 0.5, 0, 0 ); 
			
			Mat src(color,rect);
			Mat dest(resultImage,rect);
			src.copyTo(dest);
			
			//			waitKey();
			
#endif			
			
			rects.push_back(rect);	
			
		}
		
		
		c = c->h_next;
		
	}
	}
#if DISPLAY_SEGMENT_IMAGE
	
	namedWindow("segment:gray", CV_WINDOW_AUTOSIZE);			
	imshow("segment:gray",resultImage);			
	
	namedWindow("segment:color", CV_WINDOW_AUTOSIZE);			
	imshow("segment:color",color);			
	
	waitKey();
#endif		
	
	cvReleaseMemStorage(&storage);
	return rects;
	
}



bool sort_by_x (Rect a, Rect b){ 
	return (a.x < b.x); 
}

vector<Rect> segment_lineImage(Mat& input){
	
	Mat im1 = input;
	//Mat& imrgb = input;

	
   
   //cvtColor(imrgb, im1, CV_RGB2GRAY);	
   //im1 = Mat::ones(im1.size(), im1.type())*255 - im1;
   
   int m = 3;
   if (input.rows > 15)
      m = 5;
 
//   dout << input.rows << endl;
   
   adaptiveThreshold(im1,im1,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY_INV,m,1);
   
   Mat imm = remove_horizontal_lines(im1,15,0);  
   bitwise_and(im1,imm,im1);

#if DISPLAY_SEGMENT_LINEIMAGE_RESULT	
   imshowDebugZoom("segment line binary mask",im1,true);
#endif	
  
  
	CvMemStorage* storage = cvCreateMemStorage();
	CvSeq* first_contour = NULL;
	
	
	Mat m2 = im1.clone();
	CvMat mat = (CvMat) m2;
	
	cvFindContours(
				   &mat,
				   storage,
				   &first_contour,
				   sizeof(CvContour),
				   CV_RETR_EXTERNAL);
	
	CvSeq* c = first_contour;
 	
	vector<Rect> rects;	
	while (c!= NULL){
		
		// find bounding boxes
		int x1=im1.cols;
		int x2=0;
		int y1=im1.rows;
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
		
		int h = y2-y1+1;
		int w = x2-x1+1;
		
		
		Rect rect(x1,y1,w,h);
		
		if (h>4){
			
			
#if DISPLAY_SEGMENT_LINEIMAGE_STEP
			rectangle(imrgb, 
					  Point( rect.x, rect.y), 
					  Point( rect.x + rect.width, rect.y + rect.height),
					  Scalar(0,0,200), 1, 0, 0 ); 			 
			imshowDebugZoom("t",imrgb);
#endif
			rects.push_back(rect);	
			
		}
		c = c->h_next;	
	}
	cvReleaseMemStorage(&storage);
	
	sort(rects.begin(), rects.end(), sort_by_x);	
	

#if DISPLAY_SEGMENT_LINEIMAGE_RESULT	
	for (int i=0; i<rects.size();++i){		
		Rect& rect = rects[i];
		rectangle(imrgb, 
				  Point( rect.x, rect.y), 
				  Point( rect.x + rect.width, rect.y + rect.height),
				  Scalar(0,0,200), 1, 0, 0 ); 
	}
	imshowDebugZoom("t",imrgb);
#endif
	
	return rects;
}

vector<Rect> extract_characters(const Mat& imrgb){
	
	vector<Rect> ret;
	
	Mat im1;
	cvtColor(imrgb, im1, CV_RGB2GRAY);	
	Mat im = imrgb.clone();
	
	vector<Rect> lines = segment_image(imrgb);
	
	dout << "found " << lines.size() << " lines" << endl;
	for (int j=0; j < lines.size() ; ++j){
		
		dout << "segmenting line: " << j << " ...";
		Rect o = lines[j];
		
		dout << o.x << " " << o.y << " " << o.width << " " << o.height;
		
		Mat m(im1, o);
		vector<Rect> chs = segment_lineImage(m);
		
		dout << " found " << chs.size() << " blocks" << endl;
		for (int i=0; i < chs.size() ; ++i){
			Rect c = chs[i];		
			
			c.x += o.x;
			c.y += o.y;
			
			ret.push_back(c);
			
			rectangle(im, 
					  Point( c.x, c.y), 
					  Point( c.x + c.width, c.y + c.height),
					  Scalar(0,0,200), 1, 0, 0 ); 	
		}
		
	}	
	return ret;	
}


vector<Mat> letterImages;
char recognize_character(Mat& charImageGray, int mode = 0){
	
	float bestScore=0;
	char bestChar=' ';
	
	for (int j=0; j < letterImages.size() ; ++j){
		//Rect lt = letters[j];
		Mat imlt = letterImages[j];
		
		
		Size ch = charImageGray.size();
		Size lt = imlt.size();
		float ar1 = 1.0*ch.height / ch.width;
		float ar2 = 1.0*lt.height / lt.width;
		
		char c;
		if (mode == LOWER_ONLY && j < 26)
			continue;
		
		if (mode == UPPER_ONLY && j >= 26)
			continue;
		
		if (j<26){
			c = 'A' + j;
		}
		else {
			c = 'a' + j - 26;	
		}
		
		float aspect_ratio_measure = min(ar1,ar2)/max(ar1,ar2);
		if (aspect_ratio_measure>0.5){
			
			
			Size nsize;
			nsize.width = max(ch.width, lt.width);
			nsize.height = max(ch.height, lt.height);
			
			//Mat imch(imrgb,ch);
			Mat nch;
			Mat nlt;
			resize(charImageGray,nch,nsize);
			resize(imlt,nlt,nsize);
			
			/*
			 Size nsize;			
			 nsize.width = lt.width;
			 nsize.height = ch.height * (1.0*lt.width/ch.width);
			 
			 
			 Mat nch;// = imch;
			 Mat nlt = imlt;
			 resize(imch,nch,nsize);
			 */
			
			Mat result;
			matchTemplate(nch,nlt,result,CV_TM_CCOEFF_NORMED);
			double minValue, maxValue;
			Point minLoc, maxLoc;
			minMaxLoc(result, &minValue, &maxValue, &minLoc, &maxLoc);
			
			if (maxValue > bestScore){
				bestScore = maxValue;
				bestChar = c;
			}
		}
	}
	
	dout << bestChar << " " << bestScore << endl;
	
	return bestChar;
	
}


Rect strip_top_bottom(Mat input){
	
	typedef uchar T;
	
	Mat copy = input.clone();
  	
	int bg = input.at<T>(0,0);
	
	if (bg>150){
		threshold(input,copy,bg-135,255,THRESH_BINARY_INV);
	}else{
		threshold(input,copy,bg+135,255,THRESH_BINARY);
	}
	
	//print_matrix(copy);
	
	bool blank;
	int i,j;
	
	blank = true;
	i=0;
	while (blank && i < copy.rows){		
		T* ptr = copy.ptr<T>(i);		
		j=0;
		while (blank && j < copy.cols){
			if (ptr[j]>0)
				blank = false;
			j++;
		}	
		i++;
	}
	
	int y1=i-1;
	
	blank = true;
	i=copy.rows-1;
	while (blank && i >= 0){		
		T* ptr = copy.ptr<T>(i);		
		j=copy.cols-1;
		while (blank && j >= 0){
			if (ptr[j]>0)
				blank = false;
			j--;
		}	
		i--;
	}
	
	int y2=i+1;
	
	//cout << input.rows << ":" << y1 << " " << y2 << endl;
	
	return Rect(0,y1,input.cols,y2-y1+1);	
}

bool isUpper(char ch){
	return ch >= 'A' && ch <= 'Z';
}

void recognize_helper(const Mat& image){
	
	
	Mat gray;
	cvtColor(image, gray, CV_RGB2GRAY);	
	
	Mat resultImage = Mat(image.size(), image.type());
	
	vector<Rect> lines = segment_image(image);
	
	dout << "found " << lines.size() << " lines" << endl;		
	//for (int j=0; j < min(20,(int)lines.size()) ; ++j){
	for (int j=0; j < lines.size() ; ++j){
		
		dout << "segmenting line: " << j << " ...";
		Rect o = lines[j];
		Rect lineRect = lines[j];
		
		dout << o.x << " " << o.y << " " << o.width << " " << o.height;
		
		Mat m(image, o);
		vector<Rect> chs = segment_lineImage(m);
		
		
		uchar lineBgColor = m.at<uchar>(0,0);
		
		
		char prevChar;
		Rect prevCharRect;
		
		dout << " found " << chs.size() << " blocks" << endl;
		for (int i=0; i < chs.size() ; ++i){
			Rect c = chs[i];	
			
			
			c.x += o.x;
			c.y += o.y;
			Rect charRect = c;
			
			Mat charImageGray(gray,c);
			
			if (lineBgColor<50){
				
				//Mat::ones(gray.rows, gray.cols, CV_8U)*255;
				Mat white = Mat::ones(charImageGray.rows, 
									  charImageGray.cols, CV_8U)*255 - charImageGray;
				subtract(white, charImageGray, charImageGray); 
			}	
			
			
			int mode;
			
			//imshowDebugZoom("1", charImageGray);
			
			
			//				c.y = o.y;
			//				c.height = o.height;
			
			//				Rect r = strip_top_bottom(Mat(gray,c));
			//				c.y += r.y;
			//				c.height = r.height;
			//				
			
//			if ((charRect.y + charRect.height) < (lineRect.y + 0.9*lineRect.height)){
//				mode = LOWER_ONLY;
//			}else {
//				mode = UPPER_ONLY;
//			}
			
			
			//char bestChar = recognize_character(charImageGray, mode);
			char bestChar = recognize_character(charImageGray);
			
#if DISPLAY_RECOGNIZE
			
			Scalar textColor;
			
			if (lineBgColor > 50)
				textColor = Scalar(0,0,255);
			else
				textColor = Scalar(0,0,255);
			
			//				rectangle(resultImage, 
			//						  Point( c.x, c.y), 
			//						  Point( c.x + c.width, c.y + c.height),
			//						  Scalar(0,0,200), 1, 0, 0 ); 	
			
			
			Mat charImage(image, charRect);			
			paste(charImage,resultImage, charRect.x, charRect.y);
			
			
			char buf[10];
			Point center;
			center.x = c.x;// + ch.width/2;
			center.y = o.y;// + ch.height;
			
			sprintf(buf,"%c",bestChar);
			putText(resultImage,buf,center, FONT_HERSHEY_SIMPLEX, 0.4, textColor);
			
			//		imshowDebug("1",copy);	
			
#endif				
			prevChar = bestChar;
			prevCharRect = charRect;
			
			
		}
				
	}	
	imshowDebug("1",resultImage);
	
	
}


Mat& get_letter_image(char ch){
	
	int i=0;
	if (ch >= 'a' && ch <= 'z')
		i = ch - 'a' + 26;
	else
		i = ch - 'A';
	
	return letterImages[i];
}

Mat& get_char_image(char ch){
	return get_letter_image(ch);
}

Mat generate_word_image(const char word[]){
	
    int n = strlen(word);
	
	
	int x=0;
	int total_width=0;
	int max_height=0;
	int margin=0;  // set margin to 0 because the two letters are too close
	int spacing=6;
	int offset=3;
	int above=0;
	int below=0;
	for (int i=0; i < n; ++i){		
		char ch = word[i];
		
		if (ch == ' '){
			total_width += margin + spacing;		
		}
		else{
			
			Mat& letterImage = get_letter_image(ch);
			
			total_width += letterImage.cols;
			total_width += margin;	
			
			if (ch == 'p' || ch == 'y' || ch == 'g' || ch == 'q' || ch == 'j'){
				below = offset;
				above = max(above, letterImage.rows - offset);
			}
			else{				
				above = max(above, letterImage.rows);				
			}
				
		}
	}
	total_width -= margin;
	max_height = below + above+1;
	
	
	Mat img = Mat::ones(max_height,total_width,CV_8U)*255;	
	
	for (int i=0; i < n; ++i){		
		char ch = word[i];
		
		if (ch == ' '){
			x += spacing;		
		}
		else{
			
			
			Mat& letterImage = get_letter_image(ch);
			
			if (ch == 'p' || ch == 'y' || ch == 'g' || ch == 'q' || ch == 'j'){
			
			//Mat dest(img, Rect(x,0, letterImage.cols, letterImage.rows));
			Mat dest(img, Rect(x,max_height-letterImage.rows, letterImage.cols, letterImage.rows));
			letterImage.copyTo(dest);
			
			}else{
	
				Mat dest(img, Rect(x,above-letterImage.rows, letterImage.cols, letterImage.rows));
				letterImage.copyTo(dest);
				
			}
			
			x += letterImage.cols;
			x += margin;
		}
	}
	
	//imshowDebugZoom("wordimage", img);
	return img;
}

vector<WordRect> characterRects_to_wordRects(const vector<Rect>& characterRects){
	
	vector<WordRect> wordRects;
	
	
   
   if (characterRects.size() == 1){
      Rect charRect = characterRects[0];
      WordRect wordRect(charRect);
      wordRect.charRects.push_back(charRect);
      wordRects.push_back(wordRect);
      return wordRects;
   }
   
   
   float averageSpacing=0;
   float maxSpacing=0;
   float minSpacing=100000;
   for (int j=0; j < characterRects.size() - 1 ; ++j){ 
      
      Rect charRect1 = characterRects[j];     
      Rect charRect2 = characterRects[j+1];  
      float spacing = max(0,charRect2.x - (charRect1.x + charRect1.width));
      
      averageSpacing += spacing;
      maxSpacing = max(maxSpacing, spacing);
      minSpacing = min(minSpacing, spacing);
   }
   averageSpacing = averageSpacing / (characterRects.size()-1);
   
   
   if ((maxSpacing - minSpacing) < 3){
      
      Rect charRect = characterRects[0];	
      WordRect wordRect(charRect);
      
      for (int j=0; j < characterRects.size() ; ++j){         
         Rect charRect = characterRects[j];	
         
         merge(wordRect, charRect);
         wordRect.charRects.push_back(charRect); 
      }
      
      wordRects.push_back(wordRect);
      return wordRects;
   }        
      

//	float averageWidth=0;
//   float averageHeight=0;
//	for (int j=0; j < characterRects.size() ; ++j){
//		averageWidth += characterRects[j].width;
//      averageHeight += characterRects[j].height;
//	}
//	averageWidth = averageWidth / characterRects.size();
//	averageHeight = averageHeight / characterRects.size();
//   
//   float spacing_threshold = max(3.0, averageSpacing * 0.4);
   Rect charRect = characterRects[0];	
   WordRect wordRect(charRect);
   wordRects.push_back(wordRect);
	
   for (int j=0; j < characterRects.size() ; ++j){
      
      
		Rect charRect = characterRects[j];		
		WordRect& currentWordRect = wordRects.back();

      // get rid of overly short char blocks      
//      if (charRect.height <= averageHeight*0.2)
//         continue;
      float spacing = max(0,charRect.x - (currentWordRect.x + currentWordRect.width));
      
		if (abs(spacing - minSpacing) < 3 || j== 0){
		
         // merge the current character into the current word
			merge(currentWordRect, charRect);
			currentWordRect.charRects.push_back(charRect);
         
		}else{
			
			WordRect newWordRect(charRect);			
			newWordRect.charRects.push_back(charRect);	
			wordRects.push_back(newWordRect);
				
		}
	}
	
	return wordRects;
		
}

Mat visualize_segmentation(const Mat& inputImage, bool flipContrast = false){
	
	//Mat resultImage = inputImage.clone();
	Mat resultImage1;
	Mat resultImage2;
	
   
   Mat inputImageGray;
   cvtColor(inputImage,inputImageGray,CV_RGB2GRAY);
   
   if (flipContrast)
   absdiff(inputImageGray, 255, inputImageGray);

	
	resultImage1 = inputImage.clone();	
	resultImage2 = Mat(inputImage.size(), inputImage.type(), Scalar(0,0,0));
	
	vector<Rect> linesRects = segment_image(inputImage);
	for (int i=0; i < linesRects.size(); ++i){
		
		Rect lineRect = linesRects[i];
		Mat lineImageGray = Mat(inputImageGray, lineRect);
		
		vector<Rect> characterRects = segment_lineImage(lineImageGray);
		if (characterRects.empty())
			continue;
		
	//	
//		Mat resultLineImage = lineImage.clone();
//		for (int j=0; j < characterRects.size() ; ++j){
//			Rect& c = characterRects[j];
//			draw_rectangle(resultLineImage, c, Scalar(0,0,255));
//		}
//		//imshowDebugZoom("resultLineImage",resultLineImage);
//			
		
		for (int j=0; j < characterRects.size() ; ++j){
			Rect c = characterRects[j];		
			c.x += lineRect.x;
			c.y += lineRect.y;
		}

		for (int j=0; j < characterRects.size() ; ++j){
			Rect c = characterRects[j];		
			
			//add_margin(characterRect, 1, inputImage.size());
			
			rectangle(resultImage1, 
					  Point( c.x, c.y), 
					  Point( c.x + c.width, c.y + c.height),
					  Scalar(0,0,200), 0.5, 0, 0 ); 
			
			rectangle(resultImage2, 
					  Point( c.x, c.y), 
					  Point( c.x + c.width, c.y + c.height),
					  Scalar(0,0,255), 0.5, 0, 0 ); 	
			
						
			Mat src(inputImage, c);
			Mat dest(resultImage2, c);
			src.copyTo(dest);	
		}
		
		
		Rect& c = lineRect;
		rectangle(resultImage1, 
				  Point( c.x, c.y), 
				  Point( c.x + c.width, c.y + c.height),
				  Scalar(0,200,0), 0.5, 0, 0 ); 	

		rectangle(resultImage2, 
				  Point( c.x, c.y), 
				  Point( c.x + c.width, c.y + c.height),
				  Scalar(255,0,0), 0.5, 0, 0 ); 	
		
		
		vector<WordRect> wordRects = characterRects_to_wordRects(characterRects);
		
		for (int j=0; j < wordRects.size() ; ++j){
			WordRect wordRect = wordRects[j];
			wordRect.x += lineRect.x - 1;
			wordRect.y += lineRect.y - 1;
         wordRect.height += 2;
         wordRect.width += 2;
         
 			draw_rectangle(resultImage2, wordRect, Scalar(0,255,0));

         if (wordRect.charRects.size()>1){
            char buf[50];
            sprintf(buf, "%d", wordRect.charRects.size());
            Point loc(wordRect.x, wordRect.y);
            Scalar textColor(255,255,255);
            putText(resultImage2,buf, loc, FONT_HERSHEY_SIMPLEX, 0.6, textColor);
         }
		}
		
      
      for (int j=0; j < characterRects.size() ; ++j){
			Rect c = characterRects[j];		
			c.x += lineRect.x;
			c.y += lineRect.y;
         
         
         Mat src(inputImage, c);
			Mat dest(resultImage2, c);
			src.copyTo(dest);	
         

			//add_margin(characterRect, 1, inputImage.size());
			
			rectangle(resultImage1, 
                   Point( c.x, c.y), 
                   Point( c.x + c.width, c.y + c.height),
                   Scalar(0,0,200), 0.5, 0, 0 ); 
			
			rectangle(resultImage2, 
                   Point( c.x, c.y), 
                   Point( c.x + c.width, c.y + c.height),
                   Scalar(0,0,255), 0.5, 0, 0 ); 	
			
         
		}
      
		//imshowDebugZoom("resultLineImage",resultLineImage);

		
		
	}
	
			
	
	//imshowDebug("resultImage1", resultImage1,false);	
	//imshowDebug("resultImage2", resultImage2,false);
	//waitKey();
   
   return resultImage2;
}



bool sort_by_score(Match& m1, Match& m2){
	return m1.score > m2.score;
}




   
float match_image(Mat& inputG, Mat& targetG){
   
	Mat inputGN;
	resize(inputG, inputGN, targetG.size());
	

	Mat result;
	matchTemplate(inputGN, targetG, result, CV_TM_CCOEFF_NORMED); 	
	
	double minValue, maxValue;
	Point minLoc, maxLoc;
	minMaxLoc(result, &minValue, &maxValue, &minLoc, &maxLoc);	
	
#if DISPLAY_MATCH_CHAR
   imshowCompareZoom(inputGN, targetG);
#endif
	
	float score = max(abs(maxValue),abs(minValue));
	
	return score;
}


float match_char(Mat& inputCharImage, char targetChar){
   
   if (targetChar == 'l' || targetChar == 'I' || targetChar == 'i'){
      // non-image based matching for special characters
      
      if ((inputCharImage.rows / inputCharImage.cols) > 5){
         return 0.8;
      }else{
         return 0.0;
      }
   }
   
   Mat targetCharImage = get_char_image(targetChar);
   return match_image(inputCharImage, targetCharImage);
}

float match_2chars(Mat& input, char c1, char c2){
	
	char str[3];
	str[0] = c1;
	str[1] = c2;
	str[2] = '\0';
	
	Mat target = generate_word_image(str);
	return match_image(input, target);
}

float match_word(const Mat& inputImage, const WordRect& wordRect, const char targetWord[]){
	float score = 0;

#if DISPLAY_MATCH_WORD	
	dout << wordRect.charRects.size() << endl;		
	Mat m1(inputImage, wordRect);
	Mat m1g;
	cvtColor(m1, m1g, CV_RGB2GRAY);
	Mat m2 = generate_word_image(targetWord);
	imshowCompareZoom(m1g, m2);
#endif	

	
	int i= 0;
	int n = strlen(targetWord);
	int j= 0;
	int m = wordRect.charRects.size();

	bool skipFlag = true;
	
	while (i < n && j < m){
		Rect charRect = wordRect.charRects[j];
		Mat inputCharImage(inputImage, charRect);
 
	   char targetChar = targetWord[i];

      
		float score_i;		
		int cnt=0;
		
      
      
      cnt = 1;
      score_i = match_char(inputCharImage, targetChar);
#if DISPLAY_MATCH_WORD		
		dout << "(" << i << " , " << targetChar << ") : " 
      << setprecision(2)  << score_i << endl; 
#endif
      
      
		if (score_i < MIN_CHAR_MATCH_THRESHOLD && i < n-1){
			
			float score2 = match_2chars(inputCharImage, targetWord[i], targetWord[i+1]);

#if DISPLAY_MATCH_WORD		
         dout << "(" << i << " , " << targetWord[i] << targetWord[i+1] << ") : " 
         << setprecision(2)  << score2 << endl; 
#endif
			
			if (score2 >= MIN_CHAR_MATCH_THRESHOLD){
				score_i = score2;
				i++;	
				cnt = 2;
			}
		
		}						

		
		if (score_i >= MIN_CHAR_MATCH_THRESHOLD){
			
         //score = score + cnt * (1 + (0.1*score_i));
			score = score + cnt;//
 			
         i++;
			j++;
         
		}else{
			
			if (skipFlag){
				j++; // skip this charRect
				score = score - 0.1;
				
			}else {
				i++;	// skip this char
				score = score - 0.1;
			}
			skipFlag = !skipFlag;

		}
	}	
	
   score = score - 0.2*(m-j);
   
   
#if DISPLAY_MATCH_WORD		
	dout << "Total score:" << score << endl; 
#endif
	
	return score;	
}

bool filter_lineRect(Rect& line, const char word[]){
	return line.width < strlen(word)*3;
}

bool filter_wordRect(WordRect& wordRect, const char word[]){
	int inputWordLength = wordRect.charRects.size();
	int targetWordLength = strlen(word);

	
	if (inputWordLength > targetWordLength + 2 ){
		return true; // too long
	}	
	
	if (inputWordLength < targetWordLength - 2){
		return true; // too short
	}	
	
	if (wordRect.width < strlen(word) * 3){
		return true; // too narrow
	}	
	
	return false;
}

void train_by_image(const Mat& trainingImage){
   if (letterImages.empty()){
   Mat trainingImageGray;
	cvtColor(trainingImage, trainingImageGray, CV_RGB2GRAY);	
	
	vector<Rect> letters = extract_characters(trainingImage);
	for (int i=0; i < letters.size(); ++i)
		letterImages.push_back(Mat(trainingImageGray, letters[i]));  
   }
}


Mat visualize_matches(const Mat& inputImage, vector<Match> matches){
   
   Mat resultImage = inputImage.clone();

   for (int i=0; i < matches.size(); ++i){
      
      Match m = matches[i];        
      Rect r(m.x,m.y,m.w,m.h);
      draw_rectangle(resultImage, r);
   }
   
   for (int i=0; i < matches.size(); ++i){
      
      Match m = matches[i];        
      Rect r(m.x,m.y,m.w,m.h);
      
      char buf[50];
      sprintf(buf, "(%d) %0.2f", i+1, m.score);

      Point loc(r.x,r.y+25);
      Scalar black(0,0,0);
      Scalar red(0,0,255);
      
      Scalar textColor(255,255,255);
      Scalar bgColor;
      
      if (m.score < 0.5){
         bgColor = black;
      }else{
         bgColor = red;
      }
      
      
      putTextWithBackground(bgColor,resultImage,buf,loc,FONT_HERSHEY_SIMPLEX, 0.4, textColor);      
      
   }
   
   return resultImage;
}


Match 
match_words(const Mat& inputImage, 
            const vector<WordRect>& wordRects, 
            const vector<string>& targetWords){
   
   // if the words on this line are too few, skip
   if (wordRects.size() < targetWords.size())
      return Match(0,0,0,0,-1);
   
   int i=0; // word rects
   int j=0; // target words
   int n=wordRects.size();
   int m=targetWords.size();
   vector<WordRect> matchedWordRects;
  
   while (i<n && j<m){
      
      WordRect wordRect = wordRects[i];
      string targetWord = targetWords[j];
      
//      Mat wordImage(inputImage,wordRect);
//      imshowDebugZoom("wordImage", wordImage);
      
      double score_per_word = match_word(inputImage, wordRect, targetWord.c_str());
      // noramlzie the score
      score_per_word = score_per_word / targetWord.size();
      
      if (score_per_word > 0.5){

         dout << "[ocr:find_phrase] " << targetWord << " : " << score_per_word << endl;

         matchedWordRects.push_back(wordRect);
         
         i++;
         j++;
         
      }else{
         
         matchedWordRects.clear();
         
         i++;            
         j=0;
      }
   }
   
   
   if (matchedWordRects.size() != targetWords.size())
      return Match(0,0,0,0,-1);

   Rect r = matchedWordRects[0];
   for (int j=1; j < matchedWordRects.size(); ++j){
      merge(r, matchedWordRects[j]);
   }
   
   
   Match match;
   match.score = 1.0;
   match.x = r.x;
   match.y = r.y;
   match.h = r.height;
   match.w = r.width;
   
   return match;
}

vector<Match> 
find_phrase_helper(const Mat& inputImageColor, 
                   vector<string> targetWords,
                   bool flipContrast = false){
  
   
//   Mat segResultImage = visualize_segmentation(inputImage);
//   imshowDebug("find_phrase:segment", segResultImage);
   //sprintf(buf,"/tmp/ocr.%s.seg.png", word);
   //imwrite(buf, segResultImage);
   
   
   Mat inputImageGray;
   cvtColor(inputImageColor,inputImageGray,CV_RGB2GRAY);
   
   if (flipContrast)
      absdiff(inputImageGray, 255, inputImageGray);
 
   vector<Match> candidateMatches;

  	vector<Rect> linesRects = segment_image(inputImageColor);
   
	for (int i=0; i < linesRects.size(); ++i){
		
		Rect lineRect = linesRects[i];
		Mat lineImageGray = Mat(inputImageGray, lineRect);
		
		// break the line down into characters
		vector<Rect> charRects = segment_lineImage(lineImageGray);
     
      if (charRects.size() == 0){
			continue;
		}	
      
 		for (int j=0; j < charRects.size(); ++j){
			Rect& charRect = charRects[j];
			charRect.x += lineRect.x;
			charRect.y += lineRect.y;
		}
      
		vector<WordRect> wordRects = characterRects_to_wordRects(charRects);
      
      Match match = match_words(inputImageGray, wordRects, targetWords);

      if (match.score > 0)
         candidateMatches.push_back(match);
    
      
   }
   
   return candidateMatches;
}


vector<Match> 
find_phrase(const Mat& inputImageColor, 
            vector<string> targetWords){
   
   vector<Match> candidateMatches;
   
   candidateMatches = find_phrase_helper(inputImageColor, targetWords);
   
   vector<Match> candidateMatchesMore = find_phrase_helper(inputImageColor, targetWords, true);
   
   candidateMatches.insert(candidateMatches.end(), candidateMatchesMore.begin(), candidateMatchesMore.end() );
  	sort(candidateMatches, sort_by_score);				
   return candidateMatches;
}

vector<Match> 
find_word_helper(const Mat& inputImageColor, 
                 const char word[],
                 bool flipContrast = false){
   
	Mat targetWordImage = generate_word_image(word);
   
   Mat inputImageGray;
   cvtColor(inputImageColor,inputImageGray,CV_RGB2GRAY);
	
   if (flipContrast)
      absdiff(inputImageGray, 255, inputImageGray);

	Rect bestRect;
	vector<Match> candidateMatches;
	
	vector<Rect> linesRects = segment_image(inputImageColor);
	
	for (int i=0; i < linesRects.size(); ++i){

		Rect lineRect = linesRects[i];
		Mat lineImageGray = Mat(inputImageGray, lineRect);
      
      
		
		// line is too short
		if (filter_lineRect(lineRect, word)){
			continue;
		}
		
		// break the line down into characters
		vector<Rect> charRects;
		charRects = segment_lineImage(lineImageGray);
	
		// skip if too few characters
		if (charRects.size() < strlen(word) - 2){
			continue;
		}
		
		if (charRects.size() == 0){
			continue;
		}	
				
		for (int j=0; j < charRects.size(); ++j){
			Rect& charRect = charRects[j];
			charRect.x += lineRect.x;
			charRect.y += lineRect.y;
		}
		
      vector<WordRect> wordRects = characterRects_to_wordRects(charRects);
		for (int j=0; j < wordRects.size(); ++j){
			WordRect wordRect = wordRects[j];
			
			if (filter_wordRect(wordRect, word))
				continue;
			
			double score = 0;
			score = match_word(inputImageGray, wordRect, word);

			Match m;
			m.score = min(1.0, score / (strlen(word)));
			m.x = wordRect.x;
         m.y = wordRect.y;
         m.h = wordRect.height;
         m.w = wordRect.width;
			candidateMatches.push_back(m);
			
		}
	}



	sort(candidateMatches, sort_by_score);				

	return candidateMatches;								   
}



vector<Match> find_word_by_image(const Mat& inputImageColor, 
                                 const char word[]){
   
   vector<Match> candidateMatches;
   
   candidateMatches = find_word_helper(inputImageColor, word);
   
   vector<Match> candidateMatchesMore = find_word_helper(inputImageColor, word, true);
   
   candidateMatches.insert(candidateMatches.end(), candidateMatchesMore.begin(), candidateMatchesMore.end() );
  	sort(candidateMatches, sort_by_score);				
 

#if OUTPUT_RESULT_IMAGES   
   Mat resultImage = visualize_matches(inputImageColor, candidateMatches);
   char buf[100];
   sprintf(buf,"/tmp/ocr.%s.output.png", word);
   imwrite(buf, resultImage);
   
   sprintf(buf,"/tmp/ocr.%s.input.png", word);
   imwrite(buf, inputImageColor);
   
   Mat segResultImage1 = visualize_segmentation(inputImageColor);
   sprintf(buf,"/tmp/ocr.%s.seg.1.png", word);
   imwrite(buf, segResultImage1);

   Mat segResultImage2 = visualize_segmentation(inputImageColor,true);
   sprintf(buf,"/tmp/ocr.%s.seg.2.png", word);
   imwrite(buf, segResultImage2);
   

#endif   
   
   return candidateMatches;
}

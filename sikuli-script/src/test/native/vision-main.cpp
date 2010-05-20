/*
 *  vision-main.cpp
 *  vision
 *
 *  Created by Tom Yeh on 5/1/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

//#define DEBUG

#include <iostream>
#include "finder.h"
#include "event-manager.h"


#define SIMILARITY_THRESHOLD 0.90
#define MAX_MATCHES 10
#define DEBUG_DISPLAY 1
#define DEBUG_FINDALL 1




// TEST WordFinder

#define DISPLAY_NUM_TOP_MATCHES 1

string screen_image_path(int screen_i){
	stringstream ss;
	ss << "testdata/images/"  << screen_i << "-screen.png";
	ss.flush();
	return ss.str();
}

string target_image_path(int screen_i, int target_i){
	stringstream ss;
	ss << "testdata/images/"  << screen_i << "-target-" << target_i<< ".png";
	ss.flush();
	return ss.str();
}

string result_image_path(int screen_i, int target_i, string testname){
	stringstream ss;
	ss << "testdata/results/" << screen_i << "-[" << testname << "]-target-" << target_i<< ".png";
	return ss.str();
}

void test(int screen_i, int target_i){
  	
	string source_filename(screen_image_path(screen_i));
	string target_filename = target_image_path(screen_i,target_i);
	
	char filename[100];
	


	sprintf(filename,"testdata/images/%d-screen.png",screen_i);
	
	IplImage* sourceIpl=cvLoadImage(filename,1);
	Mat source = imread(filename,3);
	
	sprintf(filename,"testdata/images/%d-target-%d.png",screen_i,target_i);	
	IplImage* targetIpl=cvLoadImage(filename,1);	
	Mat target = imread(filename,3);
	
	cout << endl << endl << "========================" << endl;
	cout << filename << endl;
	
	//Finder f(sourceIpl);
	Finder f(source);
	
	int x=0;
	int y=0;
	int w=source.cols-1;
	int h=source.rows-1;
//	f.setROI(x,y,w,h);
	
#if DEBUG_FINDALL
	f.find_all(target,SIMILARITY_THRESHOLD);
#else
	f.find(target,SIMILARITY_THRESHOLD);
#endif
	//f.find(targetIpl,1.00);
	
	
#if DEBUG_DISPLAY	
	Mat upperLeftCorner(source, Rect(5,5,target.cols,target.rows));
	target.copyTo(upperLeftCorner);	
	
	// draw a thick border around the template image
    rectangle(source,
			  Point(5, 5),
			  Point(5+target.cols,5+target.rows),
			  Scalar(0, 0, 0), 5, 0, 0 );  
	
    rectangle(source,
			  Point(5, 5),
			  Point(5+target.cols, 5+target.rows),
			  Scalar(180, 180, 180), 2, 0, 0 );  
	
	
	// draw a thick border around the template image
    rectangle(source,
			  Point(x, y),
			  Point(x+w,x+h),
			  Scalar(0, 0, 0), 5, 0, 0 );  
#endif
	
	int i=0;
	while (f.hasNext() && i < MAX_MATCHES){
		++i;
		
		//Match match = pm.next();	
		Match match = f.next();
		cout << "#" << i << ": (" << match.x << "," << match.y << ") " << match.w << "x" << match.h << " " << match.score << endl;
		
#if DEBUG_DISPLAY
		
		rectangle(source, 
				  Point( match.x, match.y), 
				  Point( match.x + match.w, match.y + match.h),
				  Scalar( 0, 0, (int)255*match.score, 0 ), 2, 0, 0 );  
		
		Point center;
		center.x = match.x + match.w/2;
		center.y = match.y + match.h/2;
     	
		
		/*	{
		 stringstream ss;
		 ss << match.score;  
		 Point loc = center;
		 loc.y = loc.y + 20;
		 //cvInitFont(&font,FONT_HERSHEY_SIMPLEX, 0.5,0.5,0,1);                  
		 putText(source,ss.str().c_str(),loc, FONT_HERSHEY_SIMPLEX, 1.0, Scalar(255,0,0));
		 }
		 */	
		{
			char buf[10];
			sprintf(buf,"%d",i);
			//cvInitFont(&font,FONT_HERSHEY_PLAIN, 1.0,1.0,0,3);                  
			putText(source,buf,center, FONT_HERSHEY_PLAIN, 1.0, Scalar(255,0,0));
		}
#endif		
		
	}
	
	
#if DEBUG_DISPLAY
	namedWindow("matches", CV_WINDOW_AUTOSIZE);

	
	Mat resized;
	if (source.rows >= 700){
		pyrDown(source, resized);
		imshow("matches", resized);
		
	}else{	
		imshow("matches", source);
	}
	
	waitKey();
	
#endif	
	
	
}


void  test_change_finder(){
	
	Mat before = imread("testdata/images/before.png");
	Mat after = imread("testdata/images/after.png");
	
	ChangeFinder cf = ChangeFinder(before);	
	cf.find(after);
	
	while (cf.hasNext()){
		Match match = cf.next();
		cout << match.x << " " << match.y << " " << match.score << endl;
		
		
		rectangle(after, 
				  Point( match.x, match.y), 
				  Point( match.x + match.w, match.y + match.h),
				  Scalar( 0, 0, (int)255*match.score, 0 ), 2, 0, 0 );  
		
		
	}
	
	
	namedWindow("changes", CV_WINDOW_AUTOSIZE);
    imshow("changes", after);
	waitKey();	
}

void test_finder(){
	if (true){

//      test(10,1);     
//      test(10,2); 
//      test(10,3); 
//      test(10,4); 
//      test(10,5); 
//      test(10,6); 
//      test(10,7); 
      
      test(11,1);
      test(11,2);
      test(11,3);
      test(11,4);
      test(11,5);
		

	}else{
		
	test(1,1);
	test(1,2);
	test(1,3);
	test(1,4);
	test(1,5);

	test(2,1);
	test(2,2);
	test(2,3); 
	test(2,4);
	
	test(3,1);
	test(3,2);
	test(3,3); 
	test(3,4);
	test(3,5); 
	test(3,6);
	
	test(4,1);
	test(4,2);
	test(4,3);
	test(4,4);
	
	test(5,1);
	test(5,2);
	test(5,3);
	test(5,4);
	
	test(6,1);
	test(6,2);
	test(6,3);
	test(6,4);
	test(6,5);
	test(6,6);
	test(6,7);
	
	test(7,1);
	test(7,2);
	test(7,3);
	test(7,4);
	test(7,5);
	test(7,6);
	test(7,7);
	test(7,8);
	
	test(8,1);
	test(8,2);	
	test(8,3);
	test(8,4);
	test(8,5);

	test(9,1);
	test(9,2);
	test(9,3);
	test(9,4);
      
      
      test(10,1);     
      test(10,2);     
	}
}

/*
void test_sem(){
	
	SikuliEventManager sem;
	sem.addObserver(SIKULI_EVENT_APPEAR, "testdata/images/advanced.png", 1, 450, 300, 400, 400);
	sem.addObserver(SIKULI_EVENT_VANISH, "testdata/images/advanced.png", 2, 450, 300, 400, 400);
	sem.addObserver(SIKULI_EVENT_CHANGE, "", 3, 450, 300, 400, 400);
	sem.addObserver(SIKULI_EVENT_APPEAR, "testdata/images/firewire.png", 4, 100, 150, 250, 400);
	sem.addObserver(SIKULI_EVENT_VANISH, "testdata/images/firewire.png", 4, 100, 150, 250, 400);
	
	
	vector<Event> events;
	
	char buf[50];
	for (int i=1;i<=20;++i){
		
		cout << "frame " << i << " =======================" << endl;
		sprintf(buf,"testdata/frames/%d.png",i);
		
		
		events = sem.update(buf);  
		
		for (vector<Event>::iterator it = events.begin(); it != events.end(); it++){
			
			
			if (it->type == SIKULI_EVENT_APPEAR){
				cout << "APPEAR";
			}
			else if (it->type == SIKULI_EVENT_VANISH){
				cout << "VANISH";
			}else if (it->type == SIKULI_EVENT_CHANGE){
				cout << "CHANGE";
			}
			
			cout << " @ " << "(" << it->x << " , " << it->y <<  " , " << it->w << " , " << it->h << " ) ";
			cout << " --> handler " << it->handler_id << endl;
		}
		
		
	}
}
*/


string inputImageName;
vector<string> testWords;
void testcase_network(){
   //	inputImageName = "network_small.png";		
   //	inputImageName = "network_medium.png";	
	inputImageName = "network_large.png";	
   
	testWords.push_back("Apply");
	testWords.push_back("Connect");
	testWords.push_back("Assist");
	testWords.push_back("Revert");
	testWords.push_back("Show");
	testWords.push_back("Advanced");	
	testWords.push_back("UMIACS");
   
}	

void testcase_trash(){
	inputImageName = "trash_large.png";	

//  	inputImageName = "trash_tom.png";	 
//   testWords.push_back("Tom");	

//	inputImageName = "trash_contacts.png";	   
//   testWords.push_back("Contacts");
   
   
	testWords.push_back("Empty");	
	testWords.push_back("Trash");	
	testWords.push_back("Compose");	
	testWords.push_back("Calendar");		
	testWords.push_back("Applications");		
	testWords.push_back("Dropbox");		
	testWords.push_back("Cancel");	
   
	testWords.push_back("Finder");	
	testWords.push_back("File");	
	testWords.push_back("Edit");	
	testWords.push_back("View");	
	testWords.push_back("Go");	
	testWords.push_back("Window");		
	testWords.push_back("Help");	
	testWords.push_back("Back");	
   
	testWords.push_back("Action");	
	testWords.push_back("Search");
	
	testWords.push_back("Starred");
	//testWords.push_back("Inbox");
	testWords.push_back("sikuli");
	testWords.push_back("Contacts");
	testWords.push_back("Tasks");
	
	testWords.push_back("Tom");	
	testWords.push_back("Yeh");	
	
	testWords.push_back("Older");	
	testWords.push_back("Oldest");	
	
	testWords.push_back("Images");	
	testWords.push_back("Yesterday");	
	testWords.push_back("Documents");	
	testWords.push_back("Macintosh");	
	
}

void testcase_keyboard(){
	inputImageName = "keyboard.png";	
   
	testWords.push_back("Services");	
	testWords.push_back("Keyboard");	
	testWords.push_back("Restore");		
	testWords.push_back("Spotlight");	
	testWords.push_back("Bluetooth");	
	testWords.push_back("Shortcuts");		
	testWords.push_back("Dashboard");
	testWords.push_back("double");
	testWords.push_back("Application");
	testWords.push_back("Replacing");
	testWords.push_back("Batteries");
	testWords.push_back("Defaults");
	testWords.push_back("Show");
	testWords.push_back("All");
}


void testcase_access(){
	inputImageName = "access.png";	

	testWords.push_back("VoiceOver");	
	
	testWords.push_back("Enhance");	
	testWords.push_back("Contrast");		
	testWords.push_back("white");	
	testWords.push_back("Display");	
	testWords.push_back("Zoom");	
	testWords.push_back("Options");	
	testWords.push_back("Hearing");	
	testWords.push_back("Keyboard");	
	testWords.push_back("Mouse");	
	testWords.push_back("Trackpad");	
	testWords.push_back("on");
   
}


void testcase_gmail(){
	inputImageName = "gmail.png";	
	
	testWords.push_back("Compose");
	testWords.push_back("Archive");
	testWords.push_back("Delete");
	testWords.push_back("All");
	testWords.push_back("None");
	testWords.push_back("Read");
	testWords.push_back("Unread");
	testWords.push_back("Calendar");
	testWords.push_back("Document");
   
}

void testcase_gmail_zoom(){
	inputImageName = "gmail_zoom.png";	

	//testWords.push_back("Drafts");
   
	testWords.push_back("Sent");
	testWords.push_back("Compose");
	testWords.push_back("Inbox");
	testWords.push_back("Buzz");
	testWords.push_back("Starred");	
	testWords.push_back("Mail");
	
}

void testcase_xp(){
	inputImageName = "xp.png";	
 
  	testWords.push_back("Authentication");
 
   
	testWords.push_back("Configure");	
	testWords.push_back("Cancel");
	testWords.push_back("OK");   
	testWords.push_back("Internet");
	testWords.push_back("Properties");
	testWords.push_back("Install");
	testWords.push_back("Uninstall");
	testWords.push_back("Folders");
	testWords.push_back("Help");

   
  	testWords.push_back("Favorites");
	testWords.push_back("Tools");
	testWords.push_back("View");
	testWords.push_back("Edit");
	testWords.push_back("File");
   
	testWords.push_back("Back");   
	testWords.push_back("Search");   
   
 
	testWords.push_back("General");   

}
   

#include "cv-util.h"
#include "ocr.h"
void test_word_finder(){
   
   Mat trainingImage = imread("testdata/ocr/arial.png",1);
   WordFinder::train(trainingImage);

   
	//testcase_network();
	//testcase_trash();
	//testcase_keyboard();
	//testcase_access();
	//testcase_gmail_zoom();
   testcase_xp();

   char buf[50];
   sprintf(buf,"%s/%s","testdata/ocr",inputImageName.c_str());   
	Mat inputImage    = imread(buf,1);
	
	WordFinder wf(inputImage);
   
#if DISPLAY_TEST_SEGMENT
   test_segment(inputImage,"test");
#endif   
   
   Mat resultImage = inputImage.clone();
   
   
   for (vector<string>::iterator iter = testWords.begin(); 
        iter != testWords.end(); ++iter){
      
      string testWord = *iter;
      

      wf.find(testWord.c_str(), 0.5);
      
      int i = 0;
      while (wf.hasNext() && i < DISPLAY_NUM_TOP_MATCHES){
      // draw each match on the result image for visualization
 
         Match m = wf.next();         
         Rect r(m.x,m.y,m.w,m.h);
         draw_rectangle(resultImage, r);
         
         char buf[50];
         sprintf(buf, "%d:%s:%0.2f", i+1, 
                 testWord.c_str(), 
                 m.score, 
                 testWord.length());
         
         
         // determine the region the text would occupy
         // so we can draw a solid background for the
         // text
         int baseline = 0;
         Size textSize = getTextSize(buf, 
                                     FONT_HERSHEY_SIMPLEX,
                                     0.4, 1, &baseline);         
         Point loc(r.x,r.y+25);
         Scalar black(0,0,0);
         Scalar red(0,0,255);
         Scalar fillColor;
         
         if (m.score > 0.8){
            fillColor = black;
         }else{
            fillColor = red;
         }
         
         rectangle(resultImage, 
                   loc+Point(0,baseline), 
                   loc+Point(textSize.width, -textSize.height),
                   fillColor, CV_FILLED);
         
         
         Scalar textColor(255,255,255);
         putText(resultImage,buf, loc, FONT_HERSHEY_SIMPLEX, 0.4, textColor);
         //imshowDebug("test_find:resultImage",resultImage);
         
         cout << i << endl;
         i++;
      }
   }
	
	imshowDebug("test_find:resultImage",resultImage);
	
   
}   

int main (int argc, const char * argv[]) {
	
	
   
	//test_change_finder();
	//test_sem();
	
   //while (1)
	//test_finder();
	
   test_word_finder();
   
    return 0;
}

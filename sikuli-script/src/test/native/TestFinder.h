#include <cxxtest/TestSuite.h>

#include<sstream>
#include "finder.h"

#define SHOW_DEBUG_IMAGE 0
#define NUM_SCREEN 5

class TestFinder : public CxxTest::TestSuite 
{
private:
  string screen_image_path(int screen_i){
     stringstream ss;
     ss << "testdata/images/"  << screen_i << "-screen.png";
     return ss.str();
  }

string target_image_path(int screen_i, int target_i){
   stringstream ss;
   ss << "testdata/images/"  << screen_i << "-target-" << target_i<< ".png";
   return ss.str();
}

string result_image_path(int screen_i, int target_i, string testname){
   stringstream ss;
   ss << "testdata/results/" << screen_i << "-[" << testname << "]-target-" << target_i<< ".png";
   return ss.str();
}

public:

   void testAbove08(){ for(int i=1;i<=NUM_SCREEN;i++)   testAbove08(i); }
   void testTop(){ for(int i=1;i<=NUM_SCREEN;i++)   testTop(i); }
   void testTop5(){ for(int i=1;i<=NUM_SCREEN;i++)   testTop5(i); }
   void testROI_Right(){ for(int i=1;i<=NUM_SCREEN;i++)   testROI_Right(i); }
   void testROI_Left(){ for(int i=1;i<=NUM_SCREEN;i++)   testROI_Left(i); }
   void testROI_LowerRight(){ for(int i=1;i<=NUM_SCREEN;i++)   testROI_LowerRight(i); }

   void testAbove08(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(true);
      string testcase = "above-0.8";
      for (int target_i=1;target_i<=1;target_i++){
         f.find(target_image_path(screen_i,target_i).c_str(),0.8);
         while (f.hasNext()){
            Match m;
            m = f.next();

         }
         cout << "saving test result to " << result_image_path(screen_i,target_i,testcase) << endl;
         f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());
         if (SHOW_DEBUG_IMAGE)
            f.debug_show_image();
      }
   }

   void testTop5(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(true);
      string testcase = "top-5";
      for (int target_i=1;target_i<=4;target_i++){
         f.find(target_image_path(screen_i,target_i).c_str());
         for (int i=0;i<5;++i){      
            Match m;
            m = f.next();
         }
         cout << "saving test result to " << result_image_path(screen_i,target_i,testcase) << endl;
         f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());
         if (SHOW_DEBUG_IMAGE)
            f.debug_show_image();
      }
   }

   void testTop(int screen_i){
    Finder f(screen_image_path(screen_i).c_str());
    f.debug(true);

    string testcase = "top";
    for (int target_i=1;target_i<=4;target_i++){
       Match m;
       f.find(target_image_path(screen_i,target_i).c_str());
       m = f.next();
       cout << "saving test result to " << result_image_path(screen_i,target_i,testcase) << endl;
       f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());
       if (SHOW_DEBUG_IMAGE)
          f.debug_show_image();

    }
   }

   void testROI_Left(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(true);
      string testcase = "roi-left-above-0.8";
      for (int target_i=1;target_i<=4;target_i++){
         Match m;
         f.setROI(0,0,f.get_screen_width()/2,f.get_screen_height());
         f.find(target_image_path(screen_i,target_i).c_str(),0.8);             
         while (f.hasNext()){
            f.next();   
         }
         cout << "saving test result to " << result_image_path(screen_i,target_i,testcase) << endl;
         f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());
         if (SHOW_DEBUG_IMAGE)
            f.debug_show_image();
      }
   }

   void testROI_Right(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(true);
      string testcase = "roi-right-above-0.9";
      for (int target_i=1;target_i<=4;target_i++){

         Match m;

         f.setROI(f.get_screen_width()/2,0,f.get_screen_width()/2,f.get_screen_height());
         f.find(target_image_path(screen_i,target_i).c_str(),0.9);             

         while (f.hasNext()){
            f.next();   
         }

         cout << "saving test result to " << result_image_path(screen_i,target_i,testcase) << endl;
         f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());

         if (SHOW_DEBUG_IMAGE)
            f.debug_show_image();
      }
   }

   void testROI_LowerRight(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(true);
      string testcase = "roi-lower-right";
      for (int target_i=3;target_i<=3;target_i++){

         Match m;

         f.setROI(f.get_screen_width()/2,f.get_screen_height()/2,f.get_screen_width()/2,f.get_screen_height()/2);
         f.find(target_image_path(screen_i,target_i).c_str());             

         for (int i=0; i < 5 && f.hasNext(); i++)
            m = f.next();

         cout << "saving test result to " << result_image_path(screen_i,target_i,testcase) << endl;
         f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());
         if (SHOW_DEBUG_IMAGE)
            f.debug_show_image();

      }
   }

};


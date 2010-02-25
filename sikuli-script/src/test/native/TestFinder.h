#include <cxxtest/TestSuite.h>

#include<sstream>
#include "finder.h"

#define SHOW_DEBUG_IMAGE 0
#define NUM_SCREEN 2

class TestFinder : public CxxTest::TestSuite 
{
   const static bool DEBUG = false;

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
   void testROI_Left(){ for(int i=1;i<=NUM_SCREEN;i++)   testROI_Left(i); }
   void testROI_Right(){ for(int i=1;i<=NUM_SCREEN;i++)   testROI_Right(i); }
   void testROI_LowerRight(){ for(int i=1;i<=NUM_SCREEN;i++)   testROI_LowerRight(i); }

   static const int Above08_Count[][1];
   void testAbove08(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(DEBUG);
      string testcase = "above-0.8";
      for (int target_i=1;target_i<=1;target_i++){
         f.find(target_image_path(screen_i,target_i).c_str(),0.8);
         int count = 0;
         while (f.hasNext()){
            Match m;
            m = f.next();
            count++;
         }
         TS_ASSERT_EQUALS(count, Above08_Count[screen_i-1][target_i-1]);

         if(DEBUG){
            cout << "saving test result to " 
                 << result_image_path(screen_i,target_i,testcase) << endl;
            f.debug_save_image(
               result_image_path(screen_i,target_i,testcase).c_str());
            if(SHOW_DEBUG_IMAGE) f.debug_show_image();
         }
      }
   }

   static const int Top5_XY[][4][5][2];
   void testTop5(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(DEBUG);
      string testcase = "top-5";
      for (int target_i=1;target_i<=4;target_i++){
         f.find(target_image_path(screen_i,target_i).c_str());
         //cout << "{ ";
         for (int i=0;i<5;++i){      
            Match m;
            m = f.next();
            //cout << "{" <<  m.x << "," << m.y << "}, " ;
            TS_ASSERT_EQUALS(m.x, Top5_XY[screen_i-1][target_i-1][i][0]);
            TS_ASSERT_EQUALS(m.y, Top5_XY[screen_i-1][target_i-1][i][1]);
         }
         //cout << "}," << endl;
         if(DEBUG){
            cout << "saving test result to " 
                 << result_image_path(screen_i,target_i,testcase) << endl;
            f.debug_save_image(
               result_image_path(screen_i,target_i,testcase).c_str());
            if(SHOW_DEBUG_IMAGE) f.debug_show_image();
         }
      }
   }

   static const int Top_XY[][4][2];
   void testTop(int screen_i){
    Finder f(screen_image_path(screen_i).c_str());
    f.debug(true);

    string testcase = "top";
    //cout << "{\n";
    for (int target_i=1;target_i<=4;target_i++){
       Match m;
       f.find(target_image_path(screen_i,target_i).c_str());
       m = f.next();
       TS_ASSERT_EQUALS(m.x, Top_XY[screen_i-1][target_i-1][0]);
       TS_ASSERT_EQUALS(m.y, Top_XY[screen_i-1][target_i-1][1]);
       //cout << "{" << m.x << "," << m.y << "}, ";
       if(DEBUG){
          cout << "saving test result to " 
               << result_image_path(screen_i,target_i,testcase) << endl;
          f.debug_save_image(
             result_image_path(screen_i,target_i,testcase).c_str());
          if(SHOW_DEBUG_IMAGE) f.debug_show_image();
       }
    }
    //cout << "},\n";
   }

   static const int TestFinder::ROI_Left_Count[][4];
   void testROI_Left(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(DEBUG);
      string testcase = "roi-left-above-0.8";
      for (int target_i=1;target_i<=4;target_i++){
         Match m;
         f.setROI(0,0,f.get_screen_width()/2,f.get_screen_height());
         f.find(target_image_path(screen_i,target_i).c_str(),0.8);             
         int count=0;
         while (f.hasNext()){
            f.next();
            count++;
         }
         TS_ASSERT_EQUALS(count, ROI_Left_Count[screen_i-1][target_i-1]);
         if(DEBUG){
            cout << "saving test result to " 
                 << result_image_path(screen_i,target_i,testcase) << endl;
            f.debug_save_image(result_image_path(screen_i,target_i,testcase).c_str());
            if (SHOW_DEBUG_IMAGE) f.debug_show_image();
         }
      }
   }

   static const int TestFinder::ROI_Right_Count[][4];
   void testROI_Right(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(DEBUG);
      string testcase = "roi-right-above-0.9";
      for (int target_i=1;target_i<=4;target_i++){
         Match m;
         f.setROI(f.get_screen_width()/2,0,f.get_screen_width()/2,f.get_screen_height());
         f.find(target_image_path(screen_i,target_i).c_str(),0.9);             
         int count=0;
         while (f.hasNext()){
            f.next();   
            count++;
         }
         //cout << count << ",\n";
         TS_ASSERT_EQUALS(count, ROI_Right_Count[screen_i-1][target_i-1]);
         if(DEBUG){
            cout << "saving test result to " 
                 << result_image_path(screen_i,target_i,testcase) << endl;
            f.debug_save_image(
               result_image_path(screen_i,target_i,testcase).c_str());
            if(SHOW_DEBUG_IMAGE) f.debug_show_image();
         }
      }
   }


   static const int TestFinder::ROI_LowerRight_Count[][1];
   void testROI_LowerRight(int screen_i){
      Finder f(screen_image_path(screen_i).c_str());
      f.debug(DEBUG);
      string testcase = "roi-lower-right";
      for (int target_i=3;target_i<=3;target_i++){
         Match m;
         f.setROI(f.get_screen_width()/2,f.get_screen_height()/2,f.get_screen_width()/2,f.get_screen_height()/2);
         f.find(target_image_path(screen_i,target_i).c_str());             
         int count=0;
         for (int i=0; i < 5 && f.hasNext(); i++){
            m = f.next();
            count++;
         }
         TS_ASSERT_EQUALS(count, ROI_LowerRight_Count[screen_i-1][0]);
         //cout << count << ",\n";

         if(DEBUG){
            cout << "saving test result to " 
                 << result_image_path(screen_i,target_i,testcase) << endl;
            f.debug_save_image(
               result_image_path(screen_i,target_i,testcase).c_str());
            if(SHOW_DEBUG_IMAGE) f.debug_show_image();
         }
      }
   }

};

const int TestFinder::Above08_Count[][1] = { {6}, {1}, {25}, {1}, {1} };
const int TestFinder::ROI_Left_Count[][4] = 
   {{3, 0, 5, 1}, {0, 0, 0, 10}, {10, 2, 0, 0}, {1, 0, 0, 0}, {0, 2, 1, 185}};

const int TestFinder::ROI_Right_Count[][4] = 
   {{3, 3, 0, 0}, {1, 6, 3, 1}, {1, 0, 1, 1}, {0, 1, 0, 0}, {0, 0, 0, 3}};

const int TestFinder::ROI_LowerRight_Count[][1] = {{5}, {5}, {5}, {0}, {5}};

const int TestFinder::Top5_XY[][4][5][2] = {
   {  
      { {157,163}, {407,441}, {407,163}, {532,302}, {282,24}, }, 
      { {404,24}, {529,24}, {654,24}, {153,165}, {403,165}, }, 
      { {267,159}, {267,298}, {267,437}, {17,298}, {17,159}, }, 
      { {152,443}, {639,291}, {403,26}, {653,26}, {651,433}, }, 
   }, 
   {
      { {796,620}, {784,575}, {473,373}, {546,373}, {630,373} },
      { {1046,461}, {1046,571}, {1046,351}, {928,21}, {1046,681}, }, 
      { {1157,135}, {1157,245}, {1157,25}, {930,241}, {1038,21}, }, 
      { {787,3}, {725,3}, {562,3}, {136,3}, {529,3}, }, 
   },
   {
      { {755,470}, {583,470}, {234,133}, {1144,133}, {726,133}, }, 
      { {142,542}, {1198,631}, {83,542}, {1150,631}, {14,542}, }, 
      { {928,495}, {17,443}, {744,495}, {134,156}, {596,495}, }, 
      { {1170,399}, {1203,632}, {141,543}, {527,491}, {1182,492}, }, 
   },
   {
      { {55,338}, {669,338}, {88,122}, {406,338}, {274,338}, }, 
      { {690,349}, {678,479}, {43,370}, {374,289}, {87,498}, }, 
      { {65,211}, {74,27}, {104,430}, {75,409}, {15,276}, }, 
      { {366,311}, {72,439}, {681,311}, {366,308}, {3,311}, }, 
   },
   {
      { {603,404}, {1206,70}, {1121,70}, {254,70}, {944,70}, }, 
      { {505,1020}, {639,1020}, {1175,1020}, {910,1020}, {237,1020}, }, 
      { {50,370}, {53,266}, {44,162}, {52,214}, {41,318}, }, 
      { {605,103}, {255,122}, {399,122}, {501,122}, {788,122}, }
   }
};

const int TestFinder::Top_XY[][4][2] = {
   { {157,163}, {404,24}, {267,159}, {152,443}, },
   { {796,620}, {1046,461}, {1157,135}, {787,3}, },
   { {755,470}, {142,542}, {928,495}, {1170,399}, },
   { {55,338}, {690,349}, {65,211}, {366,311}, },
   { {603,404}, {505,1020}, {50,370}, {605,103}, },
};

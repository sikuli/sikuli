%module VisionProxy
%{
#include "vision.h"
#include <iostream>

%}

%include "std_vector.i"
%include "std_string.i"
%include "typemaps.i"
%include "various.i"


%template(FindResults) std::vector<FindResult>;

%typemap(jni) unsigned char*        "jbyteArray"
%typemap(jtype) unsigned char*      "byte[]"
%typemap(jstype) unsigned char*     "byte[]"

// Map input argument: java byte[] -> C++ unsigned char *
%typemap(in) unsigned char* {
   long len = JCALL1(GetArrayLength, jenv, $input);
   $1 = (unsigned char *)malloc(len + 1);
   if ($1 == 0) {
      std::cerr << "out of memory\n";
      return 0;
   }
   JCALL4(GetByteArrayRegion, jenv, $input, 0, len, (jbyte *)$1);
}

%typemap(freearg) unsigned char* %{
   free($1);
%}

// change Java wrapper output mapping for unsigned char*
%typemap(javaout) unsigned char* {
    return $jnicall;
 }

%typemap(javain) unsigned char* "$javainput" 




namespace sikuli {

class FindInput{
      
public:
   
   FindInput(cv::Mat source, cv::Mat target);
   FindInput(cv::Mat source, const char* target, bool text = false);
   FindInput(const char* source_filename, const char* target, bool text = false);

   cv::Mat getSourceMat();
   cv::Mat getTargetMat();
      
   void setFindAll(bool all);
      
   bool isFindingAll();
   bool isFindingText();

   void setLimit(int limit);
   int getLimit();
   
   void setSimilarity(double similarity);
   double getSimilarity();
     
   std::string getTargetText();
   
private:
   
   void init(cv::Mat source_, const char* target_string, bool text);
   void init();

      
   cv::Mat source;
   cv::Mat target;
   std::string targetText;
   
   int limit;
   double similarity;
   
   int ordering;
   int position;
   
   bool bFindingAll;
   bool bFindingText;
};


class Vision{
public:
      
   static std::vector<FindResult> find(FindInput q);
   
   static double compare(cv::Mat m1, cv::Mat m2);
   
   static void initOCR(const char* ocrDataPath);
      
   static std::string recognize(cv::Mat image);
   
};

}


namespace cv{
   class Mat {
     int _w, _h;
     unsigned char* _data;

   public:
     Mat(int _rows, int _cols, int _type, unsigned char* _data);
   };
}

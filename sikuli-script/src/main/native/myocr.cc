#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <stdlib.h>
#include "myocr.h"

using namespace std;

#include "baseapi.h"
#define COMPUTE_IMAGE_XDIM(xsize,bpp) ((bpp)>8 ? ((xsize)*(bpp)+7)/8 :((xsize)+8/(bpp)-1)/(8/(bpp)))
char* mytesseract(const unsigned char* imagedata,
                int width, int height, int bpp){

  char datapath[] = "tessdata";
  char outputbase[] = "output";
  char lang[] = "eng";
  bool numeric_mode = false;
  TessBaseAPI::InitWithLanguage(datapath,outputbase,lang,NULL,numeric_mode,0,0);
  int bytes_per_pixel = bpp / 8;        
  int bytes_per_line = COMPUTE_IMAGE_XDIM(width,bpp);
  char* text = TessBaseAPI::TesseractRectBoxes(imagedata,
                                           bytes_per_pixel,
                                           bytes_per_line, 0, 0,
                                           width,
                                           height,
                                           height);
  return text;
}


void
OCRResult::add(OCRChar ocr_char){
  ocr_chars_.push_back(ocr_char);
};

void
OCRResult::print_text(){
  for (vector<OCRChar>::iterator it = ocr_chars_.begin(); it != ocr_chars_.end(); ++it){
    cout << it->ch;    
  }
};

string
OCRResult::str(){
  string ret;
  for (vector<OCRChar>::iterator it = ocr_chars_.begin(); it != ocr_chars_.end(); ++it){
    ret = ret + it->ch;    
  }
  return ret;
};

char  
encode(char ch){
  char code;
  if (ch >= '0' && ch <= '9')
    code = ch - '0' + 2;
  else if (ch >= 'a' && ch <= 'z')
    code = ch - 'a' + 12;
  else if (ch >= 'A' && ch <= 'Z')
    code = ch - 'A' + 12;
  else 
    code = 0;
  return code;
}


// produce a new image 200% the size of the given image
unsigned char* x2(const unsigned char* imagedata,
                    int width, int height, int bpp){

    int bytes_per_pixel = bpp / 8;

    unsigned char* newimage = new unsigned char[width*height*4];

    const unsigned char* p = imagedata;
    unsigned char* q = newimage;

    for (int k=0;k<height;++k){
      
      const unsigned char* p1 = p;

      for (int i=0;i<2;++i){
        for (int j=0;j<width;++j){
            *q = *p1;
            q++;            
            *q = *p1;
            q++;p1++;
        }
      }

      p += width * bytes_per_pixel;
    }

    return newimage;

  }

OCRResult
OCR::recognize(const unsigned char* imagedata,
               int width, int height, int bpp){
/*  
  PGMImage img;
  ifstream ifs(pgm_filename.c_str(),ios::binary);
  img.ReadFromStream(ifs);   

  unsigned char* imgx2 = x2(img.data(),img.width(),img.height(),8);
  char* text = mytesseract(imgx2,2*img.width(),2*img.height(),8);
*/
  
  //unsigned char* imgx2 = x2(imagedata,width,height,bpp);
  //char* text = mytesseract(imgx2,2*width,2*height,bpp);

  char* text = mytesseract(imagedata,width,height,bpp);

  OCRResult ocr_result;

  stringstream str(text);
  string ch;
  int x0,y0,x1,y1;
  while (str >> ch >> x0 >> y0 >> x1 >> y1){    
    //cout << ch << " " << x0 << " " << y0 << " " << x1 << " " << y1 << endl;  

    //convert back to the screen coordinate (0,0) - (left,top)
    OCRChar ocr_char(ch[0],x0,height-y1,x1,height-y0);
    ocr_result.add(ocr_char);
  };

  //delete imgx2;
  delete text;

  return ocr_result;
}


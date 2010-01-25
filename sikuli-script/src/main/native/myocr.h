#ifndef _MYOCR_H_
#define _MYOCR_H_

#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <stdlib.h>


using namespace std;

struct OCRChar{
  OCRChar(char _ch, int _x0, int _y0, int _x1, int _y1)
    : ch(_ch), x0(_x0), y0(_y0), x1(_x1), y1(_y1){};
  char ch;
  int x0,y0,x1,y1;
};

class OCRResult{

public:
  void add(OCRChar ocr_char);
  void print_text();
  string str();
  
  typedef vector<OCRChar>::iterator Iterator;
  Iterator begin() { return ocr_chars_.begin();};
  Iterator end() { return ocr_chars_.end();};


  OCRChar operator[](int index) const { return ocr_chars_[index]; }

private:
  vector<OCRChar> ocr_chars_;



};

class OCR {

public:
  static OCRResult recognize(const unsigned char* imagedata,
                              int width, int height, int bpp);
  
};

#endif // _MYOCR_H_
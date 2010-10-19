/*
 *  myocr.h
 *
 *  Created by Tom Yeh on 8/1/10.
 *  Copyright 2010 sikuli.org. All rights reserved.
 *
 */

#ifndef _MYOCR_H_
#define _MYOCR_H_

#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <stdlib.h>

#include "opencv.hpp"
#include "find-result.h"


using namespace std;


class OCRRect {
  
public:
   
   OCRRect(){};
   
   OCRRect(int x_, int y_, int width_, int height_)
   : x(x_), y(y_), width(width_), height(height_){};
   
   int x;
   int y;
   int height;
   int width;
};

class OCRChar : public OCRRect{
   
public:
   
   OCRChar(char ch_, int x_, int y_, int width_, int height_)
   : ch(ch_), OCRRect(x_,y_,width_,height_){};
   
   char ch;
};

class OCRWord : public OCRRect {

public:
   void add(const OCRChar& ocr_char);
   
   string str();
   
    void clear() { ocr_chars_.clear();};
   
    bool empty() { return ocr_chars_.empty();};
   
   bool isValidWord();
   
   string getString();

   
//private:
   
   vector<OCRChar> ocr_chars_;
};

class OCRLine : public OCRRect{
public:
   
   void addWord(OCRWord& word);
   
   
   string getString();
//private:   
   
   vector<OCRWord> ocr_words_;
};

class OCRParagraph : public OCRRect{
public:  
   
   void addLine(OCRLine& line);
   
//private:
   
   vector<OCRLine> ocr_lines_;
   
};

class OCRText{

public:   
   void add(OCRWord& ocr_word);
   void addLine(OCRLine& ocr_line);
   void addParagraph(OCRParagraph& ocr_paragraph);
   
   typedef vector<OCRWord>::iterator iterator;
   iterator begin() { return ocr_words_.begin();};
   iterator end() { return ocr_words_.end();};
   
   void save(const char* filename);
   void save_with_location(const char* filename);
   
   vector<string> getLineStrings();
   vector<string> getWordStrings();
   string getString();
   
//private:
   vector<OCRLine> ocr_lines_;
   vector<OCRWord> ocr_words_;
   vector<OCRParagraph> ocr_paragraphs_;
   
};

class OCR {

public:
   static vector<OCRChar> recognize(const unsigned char* imagedata,
                                    int width, int height, int bpp);
   
   static OCRText recognize(cv::Mat mat);
   
   static vector<FindResult> find_word(const cv::Mat& mat, string word);
   static vector<FindResult> find_phrase(const cv::Mat& mat, vector<string> words);
   
   
   static OCRText recognize_screenshot(const char* screenshot_filename);
                                         
   
   static void init();
   static void init(const char* datapath);
   
   
private:
   
   static bool isInitialized;
   
};

#endif // _MYOCR_H_

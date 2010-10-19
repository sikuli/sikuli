/*
 *  imgdb.h
 *  sikuli
 *
 *  Created by Tom Yeh on 10/14/10.
 *  Copyright 2010 sikuli.org. All rights reserved.
 *
 */

#ifndef _IMGDB_H_
#define _IMGDB_H_

#include "cvgui.h"

class ImageRecord{
   
   
public:
   
   int id;
   int screenshot_id;
   int x;
   int y;
   int height;
   int width;
   int area;
   
   int mr;
   int mg;
   int mb;
   
   //Scalar mean;
   //Scalar std;
   
   //   static vector<ImageRecord>
   //   create_from_blobs(const Mat& src, const vector<Blob> image_blobs);
   //   
   //   static vector<ImageRecord>
   //   create_from_imagefile(const char* filename);
   
   void write(std::ostream& output_stream);
   void read(std::istream& input_stream);
   
};



class Database{
   
public:   
   Database();
   
   void insert(const ImageRecord& b);
   vector<ImageRecord> find(const ImageRecord& q);
   
   
   // File is broken into components, return the top match
   // of each component
   vector<ImageRecord> find(const char* filename);
   
   
   void insert_file(const char* filename, int screenshot_id);   
   
   
   void write(std::ostream& output_stream);
   void read(std::istream& input_stream);   
   
private:
   
   vector<ImageRecord> _image_records;
   
   vector<ImageRecord> create_image_records_from_blobs(const Mat& src, const vector<Blob> image_blobs);
   vector<ImageRecord> create_image_records_from_imagefile(const char* imagefile);
   
};

#endif
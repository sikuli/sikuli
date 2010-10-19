/*
 *  imgdb.cpp
 *  sikuli
 *
 *  Created by Tom Yeh on 10/14/10.
 *  Copyright 2010 sikuli.org. All rights reserved.
 *
 */

#include "imgdb.h"

void
ImageRecord::write(ostream& output_stream){   
   output_stream.write((char *) this, sizeof(class ImageRecord));
}

void
ImageRecord::read(istream& input_stream){
   input_stream.read((char *) this, sizeof(class ImageRecord));
}

Database::Database(){
}



vector<ImageRecord> 
Database::find(const char* filename){
   
   vector<ImageRecord> top_matches;
   vector<ImageRecord> records;
   
   records = create_image_records_from_imagefile(filename);   
   
   for (vector<ImageRecord>::iterator r = records.begin();
        r != records.end(); ++r){
      
      //cout << endl << r->area << " : ";
      vector<ImageRecord> matches = find(*r);
      for (vector<ImageRecord>::iterator m = matches.begin();
           m != matches.end(); ++m){
         
         //cout << "(" << m->screenshot_id << ":" << m->id << ")";
      }
      
      if (!matches.empty())
         top_matches.push_back(matches[0]);
   }
   
   return top_matches;
   
}

void
Database::insert(const ImageRecord& b){
   _image_records.push_back(b);
}

vector<ImageRecord>
Database::find(const ImageRecord& q){
   
   vector<ImageRecord> ret;
   
   vector<ImageRecord>::iterator it = _image_records.begin();
   for (; it != _image_records.end(); ++it){
      
      ImageRecord& p = *it;
      
      if (abs(q.area - it->area) > 10)
         continue;
      
      if (abs(q.height - it->height) > 5)
         continue;
      
      if (abs(q.width - it->width) > 5)
         continue;
      
      
      float s = 1.0;
      
      
      if (abs(q.mr - it->mr) > 10)
         continue;
      
      if (abs(q.mg - it->mg) > 10)
         continue;
      
      if (abs(q.mb - it->mb) > 10)
         continue;
      
      //      cout << it->id << ": ";
      //      cout << q.mr*s << " " << q.mg*s << " " << q.mb*s << "<->";
      //      cout << p.mr << " " << p.mg << " " << p.mb << endl;      
      
      ret.push_back(*it);	
      
      return ret;
   }
   
   return ret;
   
}

static int image_record_id = 0;
static int word_id = 0;

#include <fstream>

void
Database::insert_file(const char* filename, int screenshot_id){
   
   Mat image = imread(filename, 1);
   
   
   char buf[200];
   sprintf(buf,"%s.ui.txt",filename);
   std::ofstream fout(buf);
   
   sprintf(buf,"%s.ui.loc",filename);
   std::ofstream fout_loc(buf);
   
   vector<Blob> text_blobs, image_blobs;
   cvgui::segmentScreenshot(image, text_blobs, image_blobs);  
   
   
   vector<ImageRecord> records;
   records = create_image_records_from_blobs(image, image_blobs);
   
   for (int i = 0; i < image_blobs.size(); ++i){
      
      
      Blob& b = image_blobs[i];
      ImageRecord& r = records[i];
      
      // ignore small elements
      if (r.width < 15 || r.height < 15)
         continue;      
      
      vector<ImageRecord> matches;
      matches = find(r);
      
      if (matches.empty()){
         
         r.id = word_id;
         
         insert(r);
#if 0                  
         char buf[80];
         Mat part(image, b.bound);
         sprintf(buf, "research/result/ir-%d-%d.png",word_id,image_record_id);
         imwrite(buf, part);
#endif         
         word_id++;
         
      }else{
         
         r.id = matches[0].id;
      }
      
      fout << "ui" << r.id << " ";
      
      
      
      fout_loc << r.x << " " << r.y << " " << r.width << " " << r.height << " ";
      fout_loc << "ui" << r.id;
      fout_loc << endl;
   }
   
   fout << endl;
   fout.close();
   fout_loc.close();
} 


vector<ImageRecord>
Database::create_image_records_from_imagefile(const char* filename){
   vector<Blob> text_blobs;
   vector<Blob> image_blobs;
   
   Mat image = imread(filename, 1);
   cvgui::segmentScreenshot(image, text_blobs, image_blobs);  
   
   return create_image_records_from_blobs(image, image_blobs);
}


vector<ImageRecord>
Database::create_image_records_from_blobs(const Mat& src, const vector<Blob> image_blobs){
   
   
   vector<ImageRecord> ret;
   
   
   for (vector<Blob>::const_iterator b = image_blobs.begin();
        b != image_blobs.end(); ++b){
      
      Rect r = *b;      
      Mat part(src, r);
      
      
      ImageRecord ib;
      ib.x = r.x;
      ib.y = r.y;
      ib.width = r.width;
      ib.height = r.height;
      ib.area = b->area;
      
      
      Scalar mean, stddev;
      meanStdDev(part, mean, stddev, Mat());
      
      ib.mr = mean[0];
      ib.mg = mean[1];
      ib.mb = mean[2];
      
      ret.push_back(ib);
      
      image_record_id++;      
   }
   
   return ret;
}






//vector<ImageRecord>
//ImageRecord::create_from_imagefile(const char* filename){
//   
//   vector<Blob> text_blobs;
//   vector<Blob> image_blobs;
//  
//   Mat image = imread(filename, 1);
//   cvgui::segmentScreenshot(image, text_blobs, image_blobs);  
//
//   return ImageRecord::create_from_blobs(image, image_blobs);
//}
//
//
//vector<ImageRecord>
//ImageRecord::create_from_blobs(const Mat& src, const vector<Blob> image_blobs){
//   
//   
//   vector<ImageRecord> ret;
//   
//   
//   for (vector<Blob>::const_iterator b = image_blobs.begin();
//        b != image_blobs.end(); ++b){
//      
//      
//      
//      Rect r = b->bound;
//      
//      Mat part(src, r);
//      
//      //char buf[80];
//      //sprintf(buf, "research/result/ir-%d.png",image_record_id);
//      //imwrite(buf, part);
//      
//      
//      ImageRecord ib;
//      ib.x = r.x;
//      ib.y = r.y;
//      ib.width = r.width;
//      ib.height = r.height;
//      ib.area = b->area;
//
//      ret.push_back(ib);
//      
//      //image_record_id++;
//   }
//   
//   return ret;
//}

void
Database::write(ostream& stream){   
   
   int32_t num_records = _image_records.size();
   stream.write((char *)(&num_records), sizeof(int32_t));
   
   for (int jj = 0; jj < num_records; ++jj) {
      _image_records[jj].write(stream);
   }
}

void
Database::read(istream& stream){
   int32_t num_records;
   stream.read((char *)(&num_records), sizeof(int32_t));
   
   for (int jj = 0; jj < num_records; ++jj) {
      ImageRecord rec;
      rec.read(stream);
      _image_records.push_back(rec);
   }
}



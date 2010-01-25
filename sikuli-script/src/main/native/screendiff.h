#ifndef _SCREENDIFF_H
#define _SCREENDIFF_H

#include <vector>
using namespace std;
struct DiffRegion{
  int x;
  int y;
  int w;
  int h;
};

vector<DiffRegion> screendiff(const char* before_image_filename,
                         const char *after_image_filename,const char* output_image_filename);


#endif


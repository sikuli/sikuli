#ifndef _SCREEN_MATCH_H
#define _SCREEN_MATCH_H

#include <list>
#include <vector>
#include "template-matcher.h"
/*
extern "C" struct Match {
   int x, y;
   int w, h;
   double score;
   Match(int _x, int _y, int _w, int _h, double _score){
      x = _x; y = _y;
      w = _w; h = _h;
      score = _score;
   }


};
*/

/*extern "C"*/ 
//typedef std::list<Match> Matches;


//*extern "C"*/ Matches screen_match(const char *fname_tpl, const char* fname_img, int numMatches=10, double threshold=0.7);

typedef std::vector<Match> Matches;
Matches match_by_template(const char* screen_image_filename, 
                          const char* template_image_filename,
                          int max_num_matches, 
                          double min_similarity_threshold,
                          bool search_multiscale,
                          int x, int y, int w, int h,                                    
                          bool display_images,
                          const char* output_image_filename);

#endif

